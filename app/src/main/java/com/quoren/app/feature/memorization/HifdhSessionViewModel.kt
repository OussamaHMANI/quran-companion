package com.quoren.app.feature.memorization

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quoren.app.domain.model.Ayah
import com.quoren.app.domain.model.DifficultyRating
import com.quoren.app.domain.model.MemorizationMode
import com.quoren.app.domain.model.MemorizationProgress
import com.quoren.app.domain.model.MemorizationSession
import com.quoren.app.domain.memorization.MemorizationStatus
import com.quoren.app.domain.memorization.OfflineAsrModelState
import com.quoren.app.domain.model.PlaybackRequest
import com.quoren.app.domain.model.PlaybackStatus
import com.quoren.app.domain.model.RepeatCount
import com.quoren.app.domain.memorization.RecitationMatch
import com.quoren.app.domain.memorization.RecitationMatchEvaluator
import com.quoren.app.domain.memorization.RecitationMatcher
import com.quoren.app.domain.memorization.RecitationRecognitionState
import com.quoren.app.domain.memorization.RecitationRecognizer
import com.quoren.app.domain.usecase.CompleteDailyGoalAyahUseCase
import com.quoren.app.domain.usecase.CompleteMemorizationSessionUseCase
import com.quoren.app.domain.usecase.GetAyahByReferenceUseCase
import com.quoren.app.domain.usecase.GetMemorizationSessionUseCase
import com.quoren.app.domain.usecase.GetSurahsUseCase
import com.quoren.app.domain.usecase.ObserveAudioSettingsUseCase
import com.quoren.app.domain.usecase.ObserveAyahMemorizationProgressUseCase
import com.quoren.app.domain.usecase.ObservePlaybackStateUseCase
import com.quoren.app.domain.usecase.PlayAudioUseCase
import com.quoren.app.domain.usecase.RateReviewUseCase
import com.quoren.app.domain.usecase.UpdateMemorizationSessionPositionUseCase
import com.quoren.app.domain.usecase.UpdateMemorizationStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


data class HifdhSessionState(
    val loading: Boolean = true,
    val session: MemorizationSession? = null,
    val surahName: String = "",
    val ayah: Ayah? = null,
    val progress: MemorizationProgress? = null,
    val ayahVisible: Boolean = true,
    val hideLevel: Int = 1,
    val playbackStatus: PlaybackStatus = PlaybackStatus.IDLE,
    val recognition: RecitationRecognitionState = RecitationRecognitionState.Idle,
    val offlineModel: OfflineAsrModelState = OfflineAsrModelState.Missing,
    val revealedPart: Int = 0,
    val matchedWords: Int = 0,
    val totalWords: Int = 0,
    val mismatchWordIndex: Int? = null,
    val onlineRecognition: Boolean = false,
    val retryingRecognition: Boolean = false,
    val startingAfterModelDownload: Boolean = false,
    val lastAttemptTranscript: String? = null,
    val recitedAyahTexts: Map<Int, String> = emptyMap(),
    val complete: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class HifdhSessionViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val getSession: GetMemorizationSessionUseCase,
    private val getSurahs: GetSurahsUseCase,
    private val getAyah: GetAyahByReferenceUseCase,
    private val observeProgress: ObserveAyahMemorizationProgressUseCase,
    private val observePlayback: ObservePlaybackStateUseCase,
    private val observeAudioSettings: ObserveAudioSettingsUseCase,
    private val playAudio: PlayAudioUseCase,
    private val setStatus: UpdateMemorizationStatusUseCase,
    private val rateReview: RateReviewUseCase,
    private val updatePosition: UpdateMemorizationSessionPositionUseCase,
    private val completeSession: CompleteMemorizationSessionUseCase,
    private val completeDailyGoal: CompleteDailyGoalAyahUseCase,
    private val recognizer: RecitationRecognizer,
) : ViewModel() {

    private val sessionId = checkNotNull(savedState.get<String>("sessionId"))
    private val _state = MutableStateFlow(HifdhSessionState())
    val state: StateFlow<HifdhSessionState> = _state.asStateFlow()
    private var observeProgressJob: Job? = null
    private var hideAyahJob: Job? = null
    private var retryRecognitionJob: Job? = null
    private var handledEndAyah: Int? = null
    private var autoFinishRequestedFor: Int? = null
    private var retryGeneration = 0L

    init {
        viewModelScope.launch { loadSession() }
        viewModelScope.launch {
            recognizer.modelState.collectLatest { model ->
                val current = _state.value
                val shouldStart = model == OfflineAsrModelState.Ready && current.startingAfterModelDownload
                _state.value = current.copy(
                    offlineModel = model,
                    startingAfterModelDownload = if (model == OfflineAsrModelState.Ready || model is OfflineAsrModelState.Failed) {
                        false
                    } else current.startingAfterModelDownload,
                )
                if (shouldStart) startVoiceRecognition()
            }
        }
        viewModelScope.launch {
            recognizer.state.collectLatest { recognition ->
                val current = _state.value
                val ayah = current.ayah
                when (recognition) {
                    is RecitationRecognitionState.PartialTranscript -> {
                        val progress = ayah?.let { RecitationMatcher.wordProgress(it.textUthmani, recognition.transcript) }
                        _state.value = current.copy(
                            recognition = recognition,
                            matchedWords = progress?.matchedWords ?: 0,
                            totalWords = progress?.totalWords ?: 0,
                            mismatchWordIndex = progress?.mismatchIndex,
                            lastAttemptTranscript = recognition.transcript,
                            error = null,
                        )
                        if (current.session?.mode == MemorizationMode.VOICE_RECALL && ayah != null && progress?.mismatchIndex != null) {
                            _state.value = _state.value.copy(error = "mismatch")
                            scheduleRecognitionRetry(ayah.id)
                        } else if (current.session?.mode == MemorizationMode.VOICE_RECALL && ayah != null) {
                            retryRecognitionJob?.cancel()
                            retryRecognitionJob = null
                            _state.value = _state.value.copy(retryingRecognition = false)
                            if (progress?.isComplete == true && autoFinishRequestedFor != ayah.id) {
                                autoFinishRequestedFor = ayah.id
                                recognizer.finish()
                            }
                        }
                    }
                    is RecitationRecognitionState.Result -> {
                        val progress = ayah?.let { RecitationMatcher.wordProgress(it.textUthmani, recognition.transcript) }
                        _state.value = current.copy(
                            recognition = recognition,
                            matchedWords = progress?.matchedWords ?: 0,
                            totalWords = progress?.totalWords ?: 0,
                            mismatchWordIndex = progress?.mismatchIndex,
                            lastAttemptTranscript = recognition.transcript,
                        )
                        autoFinishRequestedFor = null
                        if (ayah != null) handleVoiceResult(ayah, recognition)
                    }
                    RecitationRecognitionState.NoSpeech -> {
                        _state.value = current.copy(
                            recognition = recognition,
                            lastAttemptTranscript = (current.recognition as? RecitationRecognitionState.PartialTranscript)?.transcript
                                ?: current.lastAttemptTranscript,
                            error = "uncertain",
                        )
                        if (current.session?.mode == MemorizationMode.VOICE_RECALL && ayah != null) {
                            scheduleRecognitionRetry(ayah.id)
                        }
                    }
                    else -> _state.value = current.copy(recognition = recognition)
                }
            }
        }
        viewModelScope.launch {
            observePlayback().collectLatest { playback ->
                _state.value = _state.value.copy(playbackStatus = playback.status)
                val session = _state.value.session
                val ayah = _state.value.ayah
                if (session != null && session.mode in setOf(MemorizationMode.LISTEN_RECALL, MemorizationMode.VOICE_RECALL) && ayah != null &&
                    playback.status == PlaybackStatus.ENDED &&
                    playback.surahId == session.surahId && playback.ayahNumber == ayah.ayahNumber &&
                    handledEndAyah != ayah.ayahNumber
                ) {
                    handledEndAyah = ayah.ayahNumber
                    val waitSeconds = observeAudioSettings().first().repeatDelaySeconds
                    hideAyahJob?.cancel()
                    hideAyahJob = launch {
                        delay(waitSeconds * 1000L)
                        _state.value = _state.value.copy(ayahVisible = false)
                    }
                }
            }
        }
    }

    fun playCurrentAyah() {
        val current = _state.value.session ?: return
        val ayah = _state.value.ayah ?: return
        viewModelScope.launch {
            hideAyahJob?.cancel()
            _state.value = _state.value.copy(ayahVisible = true, error = null)
            if (_state.value.progress?.status == com.quoren.app.domain.memorization.MemorizationStatus.NEW) {
                setStatus(ayah.id, com.quoren.app.domain.memorization.MemorizationStatus.LEARNING)
            }
            runCatching {
                playAudio(
                    PlaybackRequest(
                        surahId = current.surahId,
                        startAyah = ayah.ayahNumber,
                        endAyah = ayah.ayahNumber,
                        reciterId = current.reciterId,
                        ayahRepeatOverride = current.repeatCount,
                        rangeRepeatOverride = RepeatCount.ONE,
                    ),
                )
            }.onFailure { _state.value = _state.value.copy(error = it.message) }
        }
    }

    fun playGroup() {
        val current = _state.value.session ?: return
        viewModelScope.launch {
            runCatching {
                playAudio(
                    PlaybackRequest(
                        surahId = current.surahId,
                        startAyah = current.startAyah,
                        endAyah = current.endAyah,
                        reciterId = current.reciterId,
                        ayahRepeatOverride = RepeatCount.ONE,
                        rangeRepeatOverride = current.repeatCount,
                    ),
                )
            }.onFailure { _state.value = _state.value.copy(error = it.message) }
        }
    }

    fun revealAyah() {
        hideAyahJob?.cancel()
        _state.value = _state.value.copy(ayahVisible = true)
    }

    fun revealNextPart() {
        _state.value = _state.value.copy(revealedPart = (_state.value.revealedPart + 1).coerceAtMost(4))
    }

    fun startVoiceRecognition(preserveFeedback: Boolean = false) {
        retryGeneration += 1
        retryRecognitionJob?.cancel()
        autoFinishRequestedFor = null
        _state.value = _state.value.copy(
            onlineRecognition = false,
            retryingRecognition = false,
            matchedWords = 0,
            mismatchWordIndex = if (preserveFeedback) _state.value.mismatchWordIndex else null,
            lastAttemptTranscript = if (preserveFeedback) _state.value.lastAttemptTranscript else null,
            error = if (preserveFeedback) _state.value.error else null,
        )
        recognizer.start()
    }

    fun downloadOfflineModel() = recognizer.downloadOfflineModel()

    fun startOfflineRecognitionWhenReady() {
        if (_state.value.offlineModel == OfflineAsrModelState.Ready) {
            startVoiceRecognition()
            return
        }
        _state.value = _state.value.copy(startingAfterModelDownload = true)
        if (_state.value.offlineModel !is OfflineAsrModelState.Downloading) downloadOfflineModel()
    }

    fun startOnlineRecognition(preserveFeedback: Boolean = false) {
        retryGeneration += 1
        retryRecognitionJob?.cancel()
        autoFinishRequestedFor = null
        _state.value = _state.value.copy(
            onlineRecognition = true,
            retryingRecognition = false,
            matchedWords = 0,
            mismatchWordIndex = if (preserveFeedback) _state.value.mismatchWordIndex else null,
            lastAttemptTranscript = if (preserveFeedback) _state.value.lastAttemptTranscript else null,
            error = if (preserveFeedback) _state.value.error else null,
        )
        recognizer.startOnline()
    }

    fun stopVoiceRecognition() {
        retryGeneration += 1
        retryRecognitionJob?.cancel()
        retryRecognitionJob = null
        _state.value = _state.value.copy(retryingRecognition = false, startingAfterModelDownload = false)
        recognizer.cancel()
    }

    fun finishVoiceRecognition() = recognizer.finish()

    fun setRecognitionPermissionDenied() {
        _state.value = _state.value.copy(recognition = RecitationRecognitionState.PermissionRequired)
    }

    fun changeHideLevel(delta: Int) {
        _state.value = _state.value.copy(hideLevel = (_state.value.hideLevel + delta).coerceIn(1, 5))
    }

    fun nextAyah() = moveToAyah(1)
    fun previousAyah() = moveToAyah(-1)

    private suspend fun handleVoiceResult(ayah: Ayah, result: RecitationRecognitionState.Result) {
        when (RecitationMatchEvaluator.evaluate(ayah.textUthmani, result.transcript, result.confidence)) {
            RecitationMatch.CORRECT -> {
                retryRecognitionJob?.cancel()
                _state.value = _state.value.copy(
                    recitedAyahTexts = _state.value.recitedAyahTexts + (ayah.ayahNumber to ayah.textUthmani),
                    error = null,
                    retryingRecognition = false,
                    lastAttemptTranscript = null,
                )
                viewModelScope.launch {
                    runCatching { rateReview(ayah.id, DifficultyRating.EASY, LocalDate.now(), sessionId) }
                }
                moveToAyah(1, resumeVoiceRecognition = true)
            }
            RecitationMatch.INCORRECT -> {
                _state.value = _state.value.copy(error = "mismatch")
                scheduleRecognitionRetry(ayah.id)
            }
            RecitationMatch.UNCERTAIN -> {
                _state.value = _state.value.copy(error = "uncertain")
                scheduleRecognitionRetry(ayah.id)
            }
        }
    }

    private fun scheduleRecognitionRetry(ayahId: Int) {
        val generation = ++retryGeneration
        retryRecognitionJob?.cancel()
        _state.value = _state.value.copy(retryingRecognition = true)
        retryRecognitionJob = viewModelScope.launch {
            delay(RECOGNITION_RETRY_DELAY_MS)
            if (generation != retryGeneration) return@launch
            val current = _state.value
            if (current.ayah?.id != ayahId || current.session?.mode != MemorizationMode.VOICE_RECALL) return@launch
            recognizer.cancel()
            _state.value = current.copy(
                recognition = RecitationRecognitionState.Idle,
                matchedWords = 0,
                mismatchWordIndex = null,
                lastAttemptTranscript = null,
                revealedPart = 0,
            )
            if (current.onlineRecognition) startOnlineRecognition(preserveFeedback = true)
            else startVoiceRecognition(preserveFeedback = true)
        }
    }

    fun rateCurrent(rating: DifficultyRating) {
        val ayah = _state.value.ayah ?: return
        viewModelScope.launch {
            runCatching { rateReview(ayah.id, rating, LocalDate.now(), sessionId) }
                .onFailure { _state.value = _state.value.copy(error = it.message) }
            nextAyah()
        }
    }

    private fun moveToAyah(delta: Int, resumeVoiceRecognition: Boolean = false) {
        val session = _state.value.session ?: return
        val current = _state.value.ayah ?: return
        val next = current.ayahNumber + delta
        if (next !in session.startAyah..session.endAyah) {
            if (delta > 0) finishSession()
            return
        }
        viewModelScope.launch {
            hideAyahJob?.cancel()
            retryRecognitionJob?.cancel()
            retryRecognitionJob = null
            autoFinishRequestedFor = null
            recognizer.cancel()
            handledEndAyah = null
            runCatching {
                val newAyah = getAyah(session.surahId, next) ?: error("Ayah not found")
                _state.value = _state.value.copy(
                    session = session.copy(currentAyah = next),
                    ayah = newAyah,
                    ayahVisible = true,
                    hideLevel = 1,
                    revealedPart = 0,
                    recognition = RecitationRecognitionState.Idle,
                    matchedWords = 0,
                    lastAttemptTranscript = null,
                    totalWords = if (session.mode == MemorizationMode.VOICE_RECALL) {
                        RecitationMatcher.wordProgress(newAyah.textUthmani, "").totalWords
                    } else 0,
                    mismatchWordIndex = null,
                    error = null,
                    retryingRecognition = false,
                )
                observeProgressJob?.cancel()
                observeProgressJob = launch {
                    observeProgress(newAyah.id).collectLatest { p -> _state.value = _state.value.copy(progress = p) }
                }
                if (resumeVoiceRecognition && session.mode == MemorizationMode.VOICE_RECALL) {
                    if (_state.value.onlineRecognition) startOnlineRecognition() else startVoiceRecognition()
                }
                viewModelScope.launch {
                    runCatching {
                        updatePosition(sessionId, next)
                        completeDailyGoal(current.id, LocalDate.now())
                    }
                }
            }.onFailure { _state.value = _state.value.copy(error = it.message) }
        }
    }

    private fun finishSession() {
        viewModelScope.launch {
            _state.value = _state.value.copy(complete = true)
            completeSession(sessionId)
            _state.value.ayah?.let { completeDailyGoal(it.id, LocalDate.now()) }
        }
    }

    private suspend fun loadSession() {
        runCatching {
            val session = getSession(sessionId) ?: error("Session not found")
            val ayah = getAyah(session.surahId, session.currentAyah) ?: error("Ayah not found")
            val surahName = getSurahs().firstOrNull { it.id == session.surahId }?.nameArabic.orEmpty()
            _state.value = HifdhSessionState(loading = false, session = session, surahName = surahName, ayah = ayah)
            observeProgressJob = viewModelScope.launch {
                observeProgress(ayah.id).collectLatest { p -> _state.value = _state.value.copy(progress = p) }
            }
        }.onFailure { _state.value = _state.value.copy(loading = false, error = it.message) }
    }

    override fun onCleared() {
        retryRecognitionJob?.cancel()
        recognizer.cancel()
        super.onCleared()
    }

    private companion object {
        const val RECOGNITION_RETRY_DELAY_MS = 2_000L
    }
}
