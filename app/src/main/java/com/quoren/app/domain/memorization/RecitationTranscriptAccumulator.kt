package com.quoren.app.domain.memorization

/** Joins Vosk endpoint segments and its changing partial hypothesis into one utterance. */
class RecitationTranscriptAccumulator {
    private val segments = mutableListOf<String>()
    private var partial = ""

    fun reset() {
        segments.clear()
        partial = ""
    }

    fun acceptPartial(text: String): String {
        partial = text.trim()
        return currentText()
    }

    fun acceptSegment(text: String): String {
        val segment = text.trim()
        if (segment.isEmpty()) return currentText()
        segments += segment
        partial = ""
        return currentText()
    }

    fun finish(finalSegment: String): String {
        val finalText = finalSegment.trim().ifBlank { partial }
        if (finalText.isNotBlank() && segments.lastOrNull() != finalText) segments += finalText
        partial = ""
        return segments.joinToString(" ").trim()
    }

    fun currentText(): String = (segments + listOf(partial).filter(String::isNotBlank)).joinToString(" ").trim()
}
