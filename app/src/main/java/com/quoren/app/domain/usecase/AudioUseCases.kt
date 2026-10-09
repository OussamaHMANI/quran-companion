package com.quoren.app.domain.usecase

import com.quoren.app.domain.model.AudioSettings
import com.quoren.app.domain.model.DownloadedAudio
import com.quoren.app.domain.model.PlaybackPosition
import com.quoren.app.domain.model.PlaybackRequest
import com.quoren.app.domain.model.PlaybackState
import com.quoren.app.domain.model.Reciter
import com.quoren.app.domain.repository.AudioCacheManager
import com.quoren.app.domain.repository.AudioPlayerManager
import com.quoren.app.domain.repository.AudioSettingsRepository
import com.quoren.app.domain.repository.DownloadRepository
import com.quoren.app.domain.repository.ReciterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetRecitersUseCase @Inject constructor(private val reciters: ReciterRepository) {
    suspend operator fun invoke(): List<Reciter> = reciters.getAllReciters()
}

class ObserveDefaultReciterUseCase @Inject constructor(private val reciters: ReciterRepository) {
    operator fun invoke(): Flow<Reciter> = reciters.observeDefaultReciter()
}

class SetDefaultReciterUseCase @Inject constructor(private val reciters: ReciterRepository) {
    suspend operator fun invoke(id: String) = reciters.setDefaultReciter(id)
}

class ObserveAudioSettingsUseCase @Inject constructor(private val settings: AudioSettingsRepository) {
    operator fun invoke(): Flow<AudioSettings> = settings.observe()
}

class UpdateAudioSettingsUseCase @Inject constructor(
    private val settings: AudioSettingsRepository,
    private val cache: AudioCacheManager,
) {
    suspend operator fun invoke(transform: (AudioSettings) -> AudioSettings) {
        settings.update(transform)
        cache.setLimitMb(settings.observe().first().cacheLimitMb)
    }
}

class ObservePlaybackStateUseCase @Inject constructor(private val player: AudioPlayerManager) {
    operator fun invoke(): Flow<PlaybackState> = player.state
}

class ObservePlaybackPositionUseCase @Inject constructor(private val player: AudioPlayerManager) {
    operator fun invoke(): Flow<PlaybackPosition> = player.positionFlow()
}

class PlayAudioUseCase @Inject constructor(
    private val player: AudioPlayerManager,
    private val settings: AudioSettingsRepository,
    private val reciters: ReciterRepository,
    private val downloads: DownloadRepository,
) {
    suspend operator fun invoke(request: PlaybackRequest) {
        player.play(request)
        val current = settings.observe().first()
        if (current.autoDownload) {
            val reciterId = request.reciterId ?: reciters.getDefaultReciter().id
            val existing = downloads.observeDownloads().first()
                .any { it.reciterId == reciterId && it.surahId == request.surahId }
            if (!existing) downloads.download(reciterId, request.surahId)
        }
    }
}

class PlaybackControlsUseCase @Inject constructor(private val player: AudioPlayerManager) {
    fun pause() = player.pause()
    fun resume() = player.resume()
    fun stop() = player.stop()
    fun seekToProgress(progress: Float) = player.seekToProgress(progress)
    fun next() = player.next()
    fun previous() = player.previous()
}

class ObserveDownloadsUseCase @Inject constructor(private val downloads: DownloadRepository) {
    operator fun invoke(): Flow<List<DownloadedAudio>> = downloads.observeDownloads()
}

class ManageDownloadsUseCase @Inject constructor(
    private val downloads: DownloadRepository,
    private val reciters: ReciterRepository,
) {
    suspend fun downloadForDefaultReciter(surahId: Int) = downloads.download(reciters.getDefaultReciter().id, surahId)
    suspend fun pause(item: DownloadedAudio) = downloads.pause(item.reciterId, item.surahId)
    suspend fun resume(item: DownloadedAudio) = downloads.resume(item.reciterId, item.surahId)
    suspend fun retry(item: DownloadedAudio) = downloads.retry(item.reciterId, item.surahId)
    suspend fun delete(item: DownloadedAudio) = downloads.delete(item.reciterId, item.surahId)
}

class AudioCacheUseCase @Inject constructor(private val cache: AudioCacheManager) {
    suspend fun sizeBytes(): Long = cache.cacheSizeBytes()
    suspend fun clear() = cache.clear()
}
