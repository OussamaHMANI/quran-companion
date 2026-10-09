package com.quoren.app.domain.memorization

import kotlinx.coroutines.flow.StateFlow

sealed interface RecitationRecognitionState {
    data object Idle : RecitationRecognitionState
    data object Listening : RecitationRecognitionState
    data class PartialTranscript(val transcript: String) : RecitationRecognitionState
    data class Result(val transcript: String, val confidence: Float?) : RecitationRecognitionState
    data object OfflineArabicUnavailable : RecitationRecognitionState
    data object OfflineModelMissing : RecitationRecognitionState
    data object PermissionRequired : RecitationRecognitionState
    data object NoSpeech : RecitationRecognitionState
    data object OnlineServiceUnavailable : RecitationRecognitionState
    data object NetworkUnavailable : RecitationRecognitionState
    data object Error : RecitationRecognitionState
}

enum class OfflineModelFailure { NETWORK, STORAGE, INVALID_MODEL, UNKNOWN }

sealed interface OfflineAsrModelState {
    data object Missing : OfflineAsrModelState
    data class Downloading(val progress: Int?) : OfflineAsrModelState
    data object Ready : OfflineAsrModelState
    data class Failed(val reason: OfflineModelFailure) : OfflineAsrModelState
}

interface RecitationRecognizer {
    val state: StateFlow<RecitationRecognitionState>
    val modelState: StateFlow<OfflineAsrModelState>
    fun start()
    fun downloadOfflineModel()
    fun startOnline()
    fun finish()
    fun cancel()
}

enum class RecitationMatch { CORRECT, INCORRECT, UNCERTAIN }

object RecitationMatchEvaluator {
    const val MIN_CONFIDENCE = 0.70f

    fun evaluate(canonicalAyah: String, transcript: String, confidence: Float?): RecitationMatch {
        if (transcript.isBlank()) return RecitationMatch.UNCERTAIN
        if (!RecitationMatcher.matches(canonicalAyah, transcript)) return RecitationMatch.INCORRECT
        return if (confidence != null && confidence < MIN_CONFIDENCE) RecitationMatch.UNCERTAIN else RecitationMatch.CORRECT
    }
}
