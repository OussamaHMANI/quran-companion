package com.quoren.app.data.audio

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import dagger.Lazy
import com.google.common.util.concurrent.ListenableFuture
import com.quoren.app.R
import com.quoren.app.data.local.QuranDatabase
import com.quoren.app.domain.audio.RepeatEngine
import com.quoren.app.domain.audio.RepeatStep
import com.quoren.app.domain.model.AudioError
import com.quoren.app.domain.model.DownloadStatus
import com.quoren.app.domain.model.PlaybackPosition
import com.quoren.app.domain.model.PlaybackRequest
import com.quoren.app.domain.model.PlaybackState
import com.quoren.app.domain.model.PlaybackStatus
import com.quoren.app.domain.model.RepeatCount
import com.quoren.app.domain.model.Reciter
import com.quoren.app.domain.model.StreamingQuality
import com.quoren.app.domain.model.Surah
import com.quoren.app.domain.repository.AudioPlayerManager
import com.quoren.app.domain.repository.AudioSettingsRepository
import com.quoren.app.domain.repository.AudioSourceProvider
import com.quoren.app.domain.repository.QuranRepository
import com.quoren.app.domain.repository.ReciterRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Drives the shared ExoPlayer one ayah per media item. [RepeatEngine] decides what follows each ayah; the player pauses at the
 * end of every item so repetition and delays are handled here. Player calls stay on the main (application) thread;
 * URL/file resolution runs on IO.
 */
@Singleton
class Media3AudioPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val playerProvider: Lazy<ExoPlayer>,
    private val cacheEvictor: ConfigurableLruCacheEvictor,
    private val reciters: ReciterRepository,
    private val settings: AudioSettingsRepository,
    private val sources: AudioSourceProvider,
    private val quran: QuranRepository,
    private val db: QuranDatabase,
) : AudioPlayerManager, Player.Listener {

    private class Session(
        val reciterId: String,
        val reciterName: String,
        val surahId: Int,
        val surahName: String,
        val start: Int,
        val end: Int,
        val ayahRepeat: RepeatCount,
        val rangeRepeat: RepeatCount,
        val hasBasmalaIntro: Boolean,
    )

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val player: ExoPlayer by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        playerProvider.get().apply {
            pauseAtEndOfMediaItems = false
            addListener(this@Media3AudioPlayerManager)
        }
    }
    private val _state = MutableStateFlow(PlaybackState())
    override val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private var engine: RepeatEngine? = null
    private var session: Session? = null
    private var pendingStep: RepeatStep? = null
    private var delayJob: Job? = null
    private var itemEndHandled = false
    private var finished = false
    private var error: AudioError? = null
    private var controller: ListenableFuture<MediaController>? = null

    override suspend fun play(request: PlaybackRequest) {
        val config = settings.observe().first()
        cacheEvictor.maxBytes = config.cacheLimitMb.toLong() * 1024 * 1024
        val reciter = request.reciterId?.let { reciters.getReciterById(it) } ?: reciters.getDefaultReciter()
        val surah = quran.getSurahs().firstOrNull { it.id == request.surahId } ?: return
        val start = request.startAyah.coerceIn(1, surah.numberOfAyahs)
        val end = (request.endAyah ?: surah.numberOfAyahs).coerceIn(start, surah.numberOfAyahs)
        val (ayahItems, basmalaItem) = withContext(Dispatchers.IO) {
            val ayahs = (start..end).mapNotNull { buildItem(reciter, surah, it, config.streamingQuality) }
            val basmala = if (ReciterCatalog.needsBasmalaIntro(surah.id, start)) {
                buildBasmalaItem(reciter, config.streamingQuality)
            } else null
            ayahs to basmala
        }
        if (ayahItems.isEmpty()) return
        val items = listOfNotNull(basmalaItem) + ayahItems

        withContext(Dispatchers.Main.immediate) {
            player
            ensureServiceBound()
            cancelPending()
            error = null
            finished = false
            itemEndHandled = false
            val ayahRepeat = request.ayahRepeatOverride ?: config.ayahRepeat
            val rangeRepeat = request.rangeRepeatOverride ?: config.rangeRepeat
            player.pauseAtEndOfMediaItems = ayahRepeat != RepeatCount.ONE || rangeRepeat != RepeatCount.ONE
            engine = RepeatEngine(
                ayahItems.size,
                ayahRepeat,
                rangeRepeat,
                config.repeatDelaySeconds * 1000L,
            )
            session = Session(
                reciter.id,
                reciter.arabicName,
                surah.id,
                surah.nameArabic,
                start,
                end,
                ayahRepeat,
                rangeRepeat,
                basmalaItem != null,
            )
            player.setMediaItems(items, 0, 0L)
            player.prepare()
            player.play()
            publish()
        }
    }

    override fun pause() {
        player.pause()
    }

    override fun resume() {
        when {
            session == null -> Unit
            finished -> restartFromBeginning()
            pendingStep != null -> runPending()
            error != null -> {
                error = null
                player.prepare()
                player.play()
                publish()
            }
            else -> player.play()
        }
    }

    override fun stop() {
        cancelPending()
        engine = null
        session = null
        finished = false
        error = null
        player.stop()
        player.clearMediaItems()
        controller?.let { MediaController.releaseFuture(it) }
        controller = null
        publish()
    }

    override fun seekToProgress(progress: Float) {
        val offset = mediaItemOffset()
        val itemCount = player.mediaItemCount - offset
        if (itemCount <= 0) return
        cancelPending()
        val targetIndex = (progress.coerceIn(0f, 1f) * itemCount).toInt().coerceIn(0, itemCount - 1)
        player.seekTo(targetIndex + offset, 0L)
        publish()
    }

    override fun next() {
        if (player.currentMediaItemIndex + 1 >= player.mediaItemCount) return
        cancelPending()
        player.seekToNextMediaItem()
        player.play()
    }

    override fun previous() {
        cancelPending()
        if (player.currentPosition > RESTART_THRESHOLD_MS || player.currentMediaItemIndex == 0) {
            player.seekTo(0)
        } else {
            player.seekToPreviousMediaItem()
        }
        player.play()
    }

    override fun positionFlow(): Flow<PlaybackPosition> = flow {
        while (true) {
            val offset = mediaItemOffset()
            val isBasmala = offset == 1 && player.currentMediaItemIndex == 0
            emit(
                PlaybackPosition(
                    positionMs = if (isBasmala) 0 else player.currentPosition.coerceAtLeast(0),
                    durationMs = if (isBasmala) null else player.duration.takeIf { it != C.TIME_UNSET && it > 0 },
                    mediaItemIndex = (player.currentMediaItemIndex - offset).coerceAtLeast(0),
                    mediaItemCount = (player.mediaItemCount - offset).coerceAtLeast(0),
                ),
            )
            delay(POSITION_POLL_MS)
        }
    }.distinctUntilChanged().flowOn(Dispatchers.Main.immediate)

    // --- Player.Listener ---

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        if (!playWhenReady) {
            if (reason == Player.PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM) handleItemEnded() else delayJob?.cancel()
        } else if (finished) {
            restartFromBeginning()
        } else if (pendingStep != null) {
            runPending()
        }
        publish()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == Player.STATE_ENDED) handleItemEnded()
        publish()
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) = publish()

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        itemEndHandled = false
        finished = false
        val index = player.currentMediaItemIndex - mediaItemOffset()
        if (index >= 0) engine?.jumpTo(index)
        if (pendingStep?.index != null && pendingStep?.index != index) cancelPending()
        publish()
    }

    override fun onPlayerError(error: PlaybackException) {
        val item = player.currentMediaItem
        val remote = item?.mediaMetadata?.extras?.getString(EXTRA_REMOTE)
        if (item?.localConfiguration?.uri?.scheme == "file" && remote != null) {
            discardLocalFile(item)
            player.replaceMediaItem(player.currentMediaItemIndex, item.buildUpon().setUri(remote).build())
            player.prepare()
            player.play()
            return
        }
        if (session?.hasBasmalaIntro == true && player.currentMediaItemIndex == 0 && player.mediaItemCount > 1) {
            cancelPending()
            this.error = null
            itemEndHandled = false
            player.seekTo(1, 0L)
            player.prepare()
            player.play()
            publish()
            return
        }
        cancelPending()
        this.error = AudioErrorMapper.fromPlayerErrorCode(error.errorCode)
        publish()
    }

    // --- internals ---

    private fun handleItemEnded() {
        if (itemEndHandled) return
        itemEndHandled = true
        if (session?.hasBasmalaIntro == true && player.currentMediaItemIndex == 0) {
            player.seekTo(1, 0L)
            player.play()
            return
        }
        val step = engine?.onAyahFinished()
        when {
            step == null -> finished = engine != null
            step.delayMs <= 0 -> execute(step)
            else -> {
                pendingStep = step
                delayJob = scope.launch {
                    delay(step.delayMs)
                    runPending()
                }
            }
        }
    }

    private fun runPending() {
        val step = pendingStep ?: return
        pendingStep = null
        val job = delayJob
        delayJob = null
        job?.cancel()
        execute(step)
    }

    private fun execute(step: RepeatStep) {
        itemEndHandled = false
        player.seekTo(step.index + mediaItemOffset(), 0L)
        player.play()
    }

    private fun mediaItemOffset(): Int = if (session?.hasBasmalaIntro == true) 1 else 0

    private fun restartFromBeginning() {
        finished = false
        itemEndHandled = false
        engine?.reset()
        player.seekTo(0, 0L)
        player.play()
    }

    private fun cancelPending() {
        pendingStep = null
        delayJob?.cancel()
        delayJob = null
    }

    private fun publish() {
        val s = session
        val e = engine
        if (s == null || e == null) {
            _state.value = PlaybackState()
            return
        }
        val status = when {
            error != null -> PlaybackStatus.ERROR
            finished -> PlaybackStatus.ENDED
            delayJob?.isActive == true -> PlaybackStatus.PLAYING
            player.playWhenReady && player.playbackState == Player.STATE_BUFFERING -> PlaybackStatus.BUFFERING
            player.isPlaying -> PlaybackStatus.PLAYING
            else -> PlaybackStatus.PAUSED
        }
        _state.value = PlaybackState(
            status = status,
            reciterId = s.reciterId,
            reciterName = s.reciterName,
            surahId = s.surahId,
            surahName = s.surahName,
            ayahNumber = s.start + e.index,
            rangeStart = s.start,
            rangeEnd = s.end,
            ayahPass = e.ayahPass,
            ayahRepeat = s.ayahRepeat,
            rangePass = e.rangePass,
            rangeRepeat = s.rangeRepeat,
            error = error,
        )
    }

    private suspend fun buildItem(reciter: Reciter, surah: Surah, ayah: Int, quality: StreamingQuality): MediaItem? {
        val source = sources.resolve(reciter.id, surah.id, ayah, quality) ?: return null
        val uri: Uri = source.localPath?.let { File(it).toUri() } ?: Uri.parse(source.remoteUrl)
        val extras = Bundle().apply { putString(EXTRA_REMOTE, source.remoteUrl) }
        return MediaItem.Builder()
            .setMediaId("${reciter.id}:${surah.id}:$ayah")
            .setUri(uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(context.getString(R.string.audio_ayah_title, surah.nameArabic, ayah))
                    .setArtist(reciter.arabicName)
                    .setExtras(extras)
                    .build(),
            )
            .build()
    }

    private suspend fun buildBasmalaItem(reciter: Reciter, quality: StreamingQuality): MediaItem? {
        val source = sources.resolve(reciter.id, 1, 1, quality) ?: return null
        val uri: Uri = source.localPath?.let { File(it).toUri() } ?: Uri.parse(source.remoteUrl)
        val extras = Bundle().apply { putString(EXTRA_REMOTE, source.remoteUrl) }
        return MediaItem.Builder()
            .setMediaId("${reciter.id}:basmala")
            .setUri(uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(context.getString(R.string.audio_basmala_title))
                    .setArtist(reciter.arabicName)
                    .setExtras(extras)
                    .build(),
            )
            .build()
    }

    private fun discardLocalFile(item: MediaItem) {
        val path = item.localConfiguration?.uri?.path ?: return
        val parts = item.mediaId.split(':')
        scope.launch(Dispatchers.IO) {
            File(path).delete()
            if (parts.size == 3) {
                parts[1].toIntOrNull()?.let {
                    db.downloadDao().setStatus(parts[0], it, DownloadStatus.FAILED.name, System.currentTimeMillis())
                }
            }
        }
    }

    private fun ensureServiceBound() {
        if (controller != null) return
        val token = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controller = MediaController.Builder(context, token).buildAsync()
    }

    private companion object {
        const val EXTRA_REMOTE = "remote_url"
        const val POSITION_POLL_MS = 250L
        const val RESTART_THRESHOLD_MS = 3_000L
    }
}
