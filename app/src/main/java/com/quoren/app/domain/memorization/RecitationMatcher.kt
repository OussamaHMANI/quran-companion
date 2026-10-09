package com.quoren.app.domain.memorization

/** Compares ASR output to canonical text without changing or storing the canonical verse. */
object RecitationMatcher {

    fun matches(canonicalAyah: String, recognizedSpeech: String): Boolean {
        val expected = normalizeForComparison(canonicalAyah)
        val actual = normalizeForComparison(recognizedSpeech)
        val expectedWithoutDaggerAlif = normalizeForComparison(canonicalAyah.replace(DAGGER_ALIF, ""))
        return expected.isNotEmpty() && (actual == expected || actual == expectedWithoutDaggerAlif)
    }

    fun normalizeForComparison(text: String): String {
        val out = StringBuilder(text.length)
        var previousWasSpace = true
        for (character in text.lowercase()) {
            val code = character.code
            if (code == 0x0670) {
                out.append('\u0627')
                previousWasSpace = false
                continue
            }
            if (isArabicMark(code) || code == TATWEEL) continue
            val mapped = when (character) {
                '\u0671', '\u0622', '\u0623', '\u0625' -> '\u0627'
                '\u0624' -> '\u0648'
                '\u0626', '\u0649' -> '\u064A'
                '\u0629' -> '\u0647'
                else -> character
            }
            when {
                mapped.isLetterOrDigit() -> {
                    out.append(mapped)
                    previousWasSpace = false
                }
                mapped.isWhitespace() || mapped.isPunctuation() -> {
                    if (!previousWasSpace) out.append(' ')
                    previousWasSpace = true
                }
            }
        }
        return out.toString().trim()
    }

    fun wordProgress(canonicalAyah: String, recognizedSpeech: String): WordMatchProgress {
        val expected = normalizeForComparison(canonicalAyah).split(' ').filter(String::isNotBlank)
        val expectedWithoutDaggerAlif = normalizeForComparison(canonicalAyah.replace(DAGGER_ALIF, ""))
            .split(' ').filter(String::isNotBlank)
        val actual = normalizeForComparison(recognizedSpeech).split(' ').filter(String::isNotBlank)
        var matched = 0
        while (matched < expected.size && matched < actual.size &&
            (expected[matched] == actual[matched] || expectedWithoutDaggerAlif.getOrNull(matched) == actual[matched])
        ) matched++
        val mismatch = if (matched < actual.size &&
            (matched >= expected.size ||
                (expected[matched] != actual[matched] && expectedWithoutDaggerAlif.getOrNull(matched) != actual[matched]))
        ) matched else null
        return WordMatchProgress(
            matchedWords = matched,
            totalWords = expected.size,
            mismatchIndex = mismatch,
            isComplete = expected.isNotEmpty() && matched == expected.size && actual.size == expected.size,
        )
    }

    private fun isArabicMark(code: Int): Boolean =
        code in 0x0610..0x061A || code in 0x064B..0x065F ||
            code in 0x06D6..0x06ED || code in 0x08D3..0x08FF

    private fun Char.isPunctuation(): Boolean =
        Character.getType(this).toInt() in setOf(
            Character.CONNECTOR_PUNCTUATION.toInt(),
            Character.DASH_PUNCTUATION.toInt(),
            Character.START_PUNCTUATION.toInt(),
            Character.END_PUNCTUATION.toInt(),
            Character.OTHER_PUNCTUATION.toInt(),
            Character.INITIAL_QUOTE_PUNCTUATION.toInt(),
            Character.FINAL_QUOTE_PUNCTUATION.toInt(),
        )

    private const val TATWEEL = 0x0640
    private const val DAGGER_ALIF = "\u0670"
}

data class WordMatchProgress(
    val matchedWords: Int,
    val totalWords: Int,
    val mismatchIndex: Int?,
    val isComplete: Boolean,
)
