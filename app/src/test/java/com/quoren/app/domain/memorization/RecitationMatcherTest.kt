package com.quoren.app.domain.memorization

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RecitationMatcherTest {

    @Test
    fun acceptsTheSameAyahWithoutTashkeelOrWaqfPunctuation() {
        val canonical = "ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ"
        val transcript = "الحمد لله رب العالمين."
        assertTrue(RecitationMatcher.matches(canonical, transcript))
    }

    @Test
    fun rejectsOmittedOrChangedWordsRatherThanFuzzyAdvancing() {
        val canonical = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ"
        assertFalse(RecitationMatcher.matches(canonical, "الحمد لله العالمين"))
        assertFalse(RecitationMatcher.matches(canonical, "الحمد لله رب السماوات"))
    }

    @Test
    fun normalizesOnlyComparisonCopy() {
        val canonical = "بِسْمِ ٱللَّهِ"
        val normalized = RecitationMatcher.normalizeForComparison(canonical)
        assertEquals("بسم الله", normalized)
        assertEquals("بِسْمِ ٱللَّهِ", canonical)
    }

    @Test
    fun rejectsEmptyAndNonArabicSpeech() {
        assertFalse(RecitationMatcher.matches("آية", ""))
        assertFalse(RecitationMatcher.matches("آية", "unrelated words"))
        assertFalse(RecitationMatcher.matches("ءا", "ا"))
    }

    @Test
    fun ignoresFullTashkeelAndCommonUthmaniSpellingVariants() {
        val canonical = "بِسْمِ ٱللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
        assertTrue(RecitationMatcher.matches(canonical, "بسم الله الرحمن الرحيم"))
        assertTrue(RecitationMatcher.matches(canonical, "بسم الله الرحمان الرحيم"))
        assertTrue(RecitationMatcher.matches("إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "انا اعطيناك الكوثر"))
    }

    @Test
    fun reportsCorrectWordsIncrementallyAndStopsAtFirstMismatch() {
        val canonical = "ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ"
        val partial = RecitationMatcher.wordProgress(canonical, "الحمد لله")
        assertEquals(2, partial.matchedWords)
        assertEquals(4, partial.totalWords)
        assertFalse(partial.isComplete)

        val mismatch = RecitationMatcher.wordProgress(canonical, "الحمد لله ملك العالمين")
        assertEquals(2, mismatch.matchedWords)
        assertEquals(2, mismatch.mismatchIndex)
        assertFalse(mismatch.isComplete)

        val complete = RecitationMatcher.wordProgress(canonical, "الحمد لله رب العالمين")
        assertEquals(4, complete.matchedWords)
        assertTrue(complete.isComplete)
    }

    @Test
    fun keepsTheFailedRetryOnTheSameWordBoundary() {
        val canonical = "بِسْمِ ٱللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"

        val firstFailure = RecitationMatcher.wordProgress(canonical, "بسم الله ملك الرحيم")
        assertEquals(2, firstFailure.matchedWords)
        assertEquals(2, firstFailure.mismatchIndex)

        val secondFailure = RecitationMatcher.wordProgress(canonical, "بسم الله ملك")
        assertEquals(2, secondFailure.matchedWords)
        assertEquals(2, secondFailure.mismatchIndex)
        assertFalse(secondFailure.isComplete)
    }
}
