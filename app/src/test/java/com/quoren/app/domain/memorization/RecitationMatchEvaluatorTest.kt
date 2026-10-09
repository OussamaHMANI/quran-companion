package com.quoren.app.domain.memorization

import org.junit.Assert.assertEquals
import org.junit.Test

class RecitationMatchEvaluatorTest {

    private val ayah = "ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ"

    @Test
    fun onlyExactTextWithAdequateConfidenceAdvances() {
        assertEquals(RecitationMatch.CORRECT, RecitationMatchEvaluator.evaluate(ayah, "الحمد لله رب العالمين", 0.9f))
        assertEquals(RecitationMatch.INCORRECT, RecitationMatchEvaluator.evaluate(ayah, "الحمد لله رب", 0.99f))
    }

    @Test
    fun lowConfidenceNeverAdvancesButExactTranscriptCanPassWithoutConfidence() {
        assertEquals(RecitationMatch.UNCERTAIN, RecitationMatchEvaluator.evaluate(ayah, "الحمد لله رب العالمين", 0.4f))
        assertEquals(RecitationMatch.CORRECT, RecitationMatchEvaluator.evaluate(ayah, "الحمد لله رب العالمين", null))
        assertEquals(RecitationMatch.UNCERTAIN, RecitationMatchEvaluator.evaluate(ayah, "", 1f))
    }
}
