package com.quoren.app.feature.reader

import android.util.LruCache
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quoren.app.domain.model.QuranStructure
import com.quoren.app.domain.audio.AyahSynchronizationEngine
import com.quoren.app.domain.model.CurrentAyah
import com.quoren.app.domain.model.PlaybackRequest
import com.quoren.app.domain.model.RepeatCount
import com.quoren.app.domain.memorization.MemorizationStatus
import com.quoren.app.domain.memorization.OfflineAsrModelState
import com.quoren.app.domain.memorization.RecitationMatch
import com.quoren.app.domain.memorization.RecitationMatchEvaluator
import com.quoren.app.domain.memorization.RecitationMatcher
import com.quoren.app.domain.memorization.RecitationRecognitionState
import com.quoren.app.domain.memorization.RecitationRecognizer
import com.quoren.app.domain.usecase.ObserveAudioSettingsUseCase
import com.quoren.app.domain.usecase.ObservePlaybackStateUseCase
import com.quoren.app.domain.usecase.PlayAudioUseCase
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.quoren.app.domain.model.ReaderSettings
import com.quoren.app.domain.model.ThemeMode
import com.quoren.app.domain.usecase.GetBasmalaUseCase
import com.quoren.app.domain.usecase.GetPageUseCase
import com.quoren.app.domain.usecase.GetAyahUseCase
import com.quoren.app.domain.usecase.CompleteDailyGoalAyahUseCase
import com.quoren.app.domain.usecase.GetSurahsUseCase
import com.quoren.app.domain.usecase.ObserveBookmarkedAyahIdsUseCase
import com.quoren.app.domain.usecase.ObserveReaderSettingsUseCase
import com.quoren.app.domain.usecase.SaveReadingProgressUseCase
import com.quoren.app.domain.usecase.ToggleBookmarkUseCase
import com.quoren.app.domain.usecase.UpdateMemorizationStatusUseCase
import com.quoren.app.domain.usecase.UpdateReaderSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AyahUi(val id: Int, val surahId: Int, val number: Int, val text: String)

sealed interface PageBlock {
    data class SurahHeader(val surahId: Int, val name: String, val basmala: String?) : PageBlock
    data class Ayahs(val items: List<AyahUi>) : PageBlock
}

data class ReaderPageUi(
    val page: Int,
    val juz: Int,
    val title: String,
    val firstSurahId: Int,
    val firstAyahId: Int,
    val blocks: List<PageBlock>,
)

data class ReaderUiState(
    val settings: ReaderSettings = ReaderSettings(),
    val bookmarkedIds: Set<Int> = emptySet(),
    val currentAyah: CurrentAyah? = null,
    val playbackPlaying: Boolean = false,
    val voicePractice: ReaderVoicePracticeState = ReaderVoicePracticeState(),
)

data class ReaderVoicePracticeState(
    val enabled: Boolean = false,
    val currentAyahId: Int? = null,
    val currentPage: Int? = null,
    val ayahIds: List<Int> = emptyList(),
    val completedAyahIds: Set<Int> = emptySet(),
    val transcript: String = "",
    val matchedWords: Int = 0,
    val totalWords: Int = 0,
    val mismatchWordIndex: Int? = null,
    val revealedPart: Int = 0,
    val recognition: RecitationRecognitionState = RecitationRecognitionState.Idle,
    val retryingRecognition: Boolean = false,
    val startingAfterModelDownload: Boolean = false,
    val model: OfflineAsrModelState = OfflineAsrModelState.Missing,
    val feedback: RecitationMatch? = null,
    val onlineRecognition: Boolean = false,
    val complete: Boolean = false,
)

@HiltViewModel
class ReaderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPage: GetPageUseCase,
    private val getSurahs: GetSurahsUseCase,
    private val getBasmala: GetBasmalaUseCase,
    observeSettings: ObserveReaderSettingsUseCase,
    observeBookmarkedIds: ObserveBookmarkedAyahIdsUseCase,
    private val updateSettings: UpdateReaderSettingsUseCase,
    private val toggleBookmark: ToggleBookmarkUseCase,
    private val saveProgress: SaveReadingProgressUseCase,
    private val playAudio: PlayAudioUseCase,
    private val audioSettings: ObserveAudioSettingsUseCase,
    private val getAyahById: GetAyahUseCase,
    private val updateMemorizationStatus: UpdateMemorizationStatusUseCase,
    private val completeDailyGoal: CompleteDailyGoalAyahUseCase,
    private val recognizer: RecitationRecognizer,
    syncEngine: AyahSynchronizationEngine,
    observePlayback: ObservePlaybackStateUseCase,
) : ViewModel() {

    private val voicePractice = MutableStateFlow(ReaderVoicePracticeState())
    private var autoFinishRequestedFor: Int? = null
    private var voiceRetryJob: Job? = null
    private var voiceRetryGeneration = 0L

    val startPage: Int = (savedStateHandle.get<Int>("page") ?: 1).coerceIn(1, QuranStructure.PAGE_COUNT)
    val highlightAyahId: Int? = savedStateHandle.get<Int>("ayah")?.takeIf { it > 0 }

    val uiState: StateFlow<ReaderUiState> = combine(
        observeSettings(),
        observeBookmarkedIds(),
        syncEngine.currentAyah,
        observePlayback().map { it.isPlaying }.distinctUntilChanged(),
        voicePractice,
    ) { settings, ids, current, playing, voice -> ReaderUiState(settings, ids, current, playing, voice) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReaderUiState())

    private val cache = LruCache<Int, ReaderPageUi>(CACHE_SIZE)

    init {
        viewModelScope.launch {
            recognizer.state.collect { recognition ->
                val old = voicePractice.value
                when (recognition) {
                    is RecitationRecognitionState.PartialTranscript -> {
                        val currentId = old.currentAyahId
                        val ayah = currentId?.let(::cachedVoiceAyah)
                        val progress = ayah?.let { RecitationMatcher.wordProgress(it.text, recognition.transcript) }
                        val hasMismatch = old.enabled && progress?.mismatchIndex != null
                        val visibleTranscript = if (hasMismatch) {
                            recognition.transcript.split(Regex("\\s+")).filter(String::isNotBlank)
                                .take(progress?.matchedWords ?: 0).joinToString(" ")
                        } else recognition.transcript
                        voicePractice.value = old.copy(
                            recognition = recognition,
                            transcript = visibleTranscript,
                            matchedWords = progress?.matchedWords ?: 0,
                            totalWords = progress?.totalWords ?: 0,
                            mismatchWordIndex = progress?.mismatchIndex,
                            feedback = if (hasMismatch) RecitationMatch.INCORRECT else null,
                            retryingRecognition = hasMismatch,
                        )
                        if (hasMismatch) scheduleVoiceRetry(currentId)
                        else if (old.enabled) {
                            voiceRetryJob?.cancel()
                            voiceRetryJob = null
                        }
                        if (old.enabled && currentId != null && progress?.isComplete == true && autoFinishRequestedFor != currentId) {
                            autoFinishRequestedFor = currentId
                            recognizer.finish()
                        }
                    }
                    is RecitationRecognitionState.Result -> {
                        val currentId = old.currentAyahId
                        val ayah = currentId?.let(::cachedVoiceAyah)
                        val progress = ayah?.let { RecitationMatcher.wordProgress(it.text, recognition.transcript) }
                        voicePractice.value = old.copy(
                            recognition = recognition,
                            transcript = recognition.transcript,
                            matchedWords = progress?.matchedWords ?: 0,
                            totalWords = progress?.totalWords ?: 0,
                            mismatchWordIndex = progress?.mismatchIndex,
                        )
                        autoFinishRequestedFor = null
                        verifyVoiceRecitation(recognition)
                    }
                    RecitationRecognitionState.NoSpeech -> {
                        voicePractice.value = old.copy(
                            recognition = recognition,
                            feedback = RecitationMatch.UNCERTAIN,
                        )
                        scheduleVoiceRetry(old.currentAyahId)
                    }
                    else -> voicePractice.value = old.copy(recognition = recognition)
                }
            }
        }
        viewModelScope.launch {
            recognizer.modelState.collect { model ->
                val current = voicePractice.value
                val shouldStart = model == OfflineAsrModelState.Ready && current.startingAfterModelDownload
                voicePractice.value = current.copy(
                    model = model,
                    startingAfterModelDownload = if (model == OfflineAsrModelState.Ready || model is OfflineAsrModelState.Failed) {
                        false
                    } else current.startingAfterModelDownload,
                )
                if (shouldStart) startVoiceRecognition()
            }
        }
    }

    suspend fun loadPage(page: Int): ReaderPageUi {
        cache.get(page)?.let { return it }
        val ayahs = getPage(page)
        val surahs = getSurahs().associateBy { it.id }
        val basmala = getBasmala()

        val blocks = ArrayList<PageBlock>()
        var current = ArrayList<AyahUi>()
        fun flush() {
            if (current.isNotEmpty()) blocks += PageBlock.Ayahs(current)
            current = ArrayList()
        }
        for (a in ayahs) {
            if (a.ayahNumber == 1) {
                flush()
                val showBasmala = a.surahId != 1 && a.surahId != 9
                blocks += PageBlock.SurahHeader(a.surahId, surahs.getValue(a.surahId).nameArabic, basmala.takeIf { showBasmala })
            }
            current += AyahUi(a.id, a.surahId, a.ayahNumber, a.textUthmani)
        }
        flush()

        val first = ayahs.first()
        return ReaderPageUi(
            page = page,
            juz = first.juz,
            title = surahs.getValue(first.surahId).nameArabic,
            firstSurahId = first.surahId,
            firstAyahId = first.id,
            blocks = blocks,
        ).also { cache.put(page, it) }
    }

    fun onPageSettled(page: Int) {
        viewModelScope.launch {
            runCatching {
                val ui = loadPage(page)
                saveProgress(ui.firstSurahId, ui.firstAyahId, page)
                if (voicePractice.value.enabled) setVoicePage(ui)
                if (page > 1) loadPage(page - 1)
                if (page < QuranStructure.PAGE_COUNT) loadPage(page + 1)
            }
        }
    }

    fun toggleVoicePractice(page: Int) {
        if (voicePractice.value.enabled) {
            voiceRetryJob?.cancel()
            voiceRetryJob = null
            recognizer.cancel()
            autoFinishRequestedFor = null
            voicePractice.value = ReaderVoicePracticeState(model = voicePractice.value.model)
        } else {
            viewModelScope.launch {
                runCatching { setVoicePage(loadPage(page), enabled = true) }
                    .onFailure { voicePractice.value = voicePractice.value.copy(feedback = RecitationMatch.UNCERTAIN) }
            }
        }
    }

    fun startVoiceRecognition() {
        voiceRetryJob?.cancel()
        voiceRetryJob = null
        if (voicePractice.value.model != OfflineAsrModelState.Ready) {
            voicePractice.value = voicePractice.value.copy(recognition = RecitationRecognitionState.OfflineModelMissing)
            return
        }
        autoFinishRequestedFor = null
        voicePractice.value = voicePractice.value.copy(
            onlineRecognition = false,
            retryingRecognition = false,
            transcript = "",
            matchedWords = 0,
            mismatchWordIndex = null,
            feedback = null,
        )
        recognizer.start()
    }

    fun startVoiceRecognitionWhenReady() {
        if (voicePractice.value.model == OfflineAsrModelState.Ready) {
            startVoiceRecognition()
        } else {
            voicePractice.value = voicePractice.value.copy(startingAfterModelDownload = true)
            downloadVoiceModel()
        }
    }

    fun downloadVoiceModel() = recognizer.downloadOfflineModel()

    fun startOnlineVoiceRecognition() {
        voiceRetryJob?.cancel()
        voiceRetryJob = null
        autoFinishRequestedFor = null
        voicePractice.value = voicePractice.value.copy(
            onlineRecognition = true,
            retryingRecognition = false,
            transcript = "",
            matchedWords = 0,
            feedback = null,
        )
        recognizer.startOnline()
    }

    fun finishVoiceRecognition() = recognizer.finish()

    fun setRecognitionPermissionDenied() {
        voicePractice.value = voicePractice.value.copy(recognition = RecitationRecognitionState.PermissionRequired)
    }

    fun cancelVoiceRecognition() {
        voiceRetryGeneration += 1
        voiceRetryJob?.cancel()
        voiceRetryJob = null
        voicePractice.value = voicePractice.value.copy(retryingRecognition = false, startingAfterModelDownload = false)
        recognizer.cancel()
    }

    fun revealNextVoicePart() {
        voicePractice.value = voicePractice.value.copy(revealedPart = (voicePractice.value.revealedPart + 1).coerceAtMost(4))
    }

    private fun setVoicePage(page: ReaderPageUi, enabled: Boolean = voicePractice.value.enabled) {
        val ayahs = page.blocks.filterIsInstance<PageBlock.Ayahs>().flatMap { it.items }
        val ids = ayahs.map { it.id }
        voicePractice.value = voicePractice.value.copy(
            enabled = enabled,
            currentAyahId = ids.firstOrNull(),
            currentPage = page.page,
            ayahIds = ids,
            completedAyahIds = emptySet(),
            transcript = "",
            matchedWords = 0,
            totalWords = ayahs.firstOrNull()?.let { RecitationMatcher.wordProgress(it.text, "").totalWords } ?: 0,
            mismatchWordIndex = null,
            revealedPart = 0,
            feedback = null,
            retryingRecognition = false,
            complete = ids.isEmpty(),
        )
    }

    private fun verifyVoiceRecitation(result: RecitationRecognitionState.Result) {
        val currentId = voicePractice.value.currentAyahId ?: return
        val current = voicePractice.value
        val ayah = cachedVoiceAyah(currentId) ?: return
        val match = RecitationMatchEvaluator.evaluate(ayah.text, result.transcript, result.confidence)
        if (match != RecitationMatch.CORRECT) {
            voicePractice.value = voicePractice.value.copy(
                feedback = match,
                retryingRecognition = true,
            )
            scheduleVoiceRetry(currentId)
            return
        }
        voiceRetryJob?.cancel()
        val index = current.ayahIds.indexOf(ayah.id)
        val next = current.ayahIds.getOrNull(index + 1)
        val nextAyah = next?.let(::cachedVoiceAyah)
        voicePractice.value = current.copy(
            currentAyahId = next ?: ayah.id,
            completedAyahIds = current.completedAyahIds + ayah.id,
            transcript = "",
            matchedWords = 0,
            totalWords = nextAyah?.let { RecitationMatcher.wordProgress(it.text, "").totalWords } ?: 0,
            mismatchWordIndex = null,
            revealedPart = 0,
            feedback = RecitationMatch.CORRECT,
            retryingRecognition = false,
            complete = next == null,
        )
        if (next != null && current.enabled) {
            if (current.onlineRecognition) recognizer.startOnline() else recognizer.start()
        }
        viewModelScope.launch {
            runCatching {
                updateMemorizationStatus(ayah.id, MemorizationStatus.LEARNING)
                completeDailyGoal(ayah.id, java.time.LocalDate.now())
            }
        }
    }

    private fun cachedVoiceAyah(ayahId: Int): AyahUi? {
        val page = voicePractice.value.currentPage?.let(cache::get) ?: return null
        return page.blocks.asSequence()
            .filterIsInstance<PageBlock.Ayahs>()
            .flatMap { it.items.asSequence() }
            .firstOrNull { it.id == ayahId }
    }

    private fun scheduleVoiceRetry(ayahId: Int?) {
        if (ayahId == null || !voicePractice.value.enabled) return
        val retryGeneration = ++voiceRetryGeneration
        voiceRetryJob?.cancel()
        voicePractice.value = voicePractice.value.copy(retryingRecognition = true)
        voiceRetryJob = viewModelScope.launch {
            delay(2_000L)
            if (retryGeneration != voiceRetryGeneration) return@launch
            val current = voicePractice.value
            if (!current.enabled || current.currentAyahId != ayahId) return@launch
            recognizer.cancel()
            voicePractice.value = current.copy(
                recognition = RecitationRecognitionState.Idle,
                transcript = "",
                matchedWords = 0,
                mismatchWordIndex = null,
                revealedPart = 0,
            )
            if (current.onlineRecognition) startOnlineVoiceRecognition() else startVoiceRecognition()
        }
    }

    override fun onCleared() {
        voiceRetryJob?.cancel()
        recognizer.cancel()
        super.onCleared()
    }

    fun onToggleBookmark(ayahId: Int) {
        viewModelScope.launch { toggleBookmark(ayahId) }
    }

    suspend fun ayahCount(surahId: Int): Int = getSurahs().first { it.id == surahId }.numberOfAyahs

    fun playRange(surahId: Int, start: Int, end: Int?, reciterId: String) {
        viewModelScope.launch { runCatching { playAudio(PlaybackRequest(surahId, start, end, reciterId)) } }
    }

    fun playAyah(ayah: AyahUi, repeat: Boolean, toEndOfSurah: Boolean) {
        viewModelScope.launch {
            runCatching {
                val override = if (repeat && audioSettings().first().ayahRepeat == RepeatCount.ONE) RepeatCount.UNLIMITED else null
                val end = if (toEndOfSurah) null else ayah.number
                playAudio(PlaybackRequest(ayah.surahId, ayah.number, end, ayahRepeatOverride = override))
            }
        }
    }

    fun onFontSizeChanged(sp: Float) {
        viewModelScope.launch { updateSettings { it.copy(fontSizeSp = sp) } }
    }

    fun onLineSpacingChanged(value: Float) {
        viewModelScope.launch { updateSettings { it.copy(lineSpacing = value) } }
    }

    fun onThemeModeChanged(mode: ThemeMode) {
        viewModelScope.launch { updateSettings { it.copy(themeMode = mode) } }
    }

    private companion object {
        const val CACHE_SIZE = 16
    }
}
