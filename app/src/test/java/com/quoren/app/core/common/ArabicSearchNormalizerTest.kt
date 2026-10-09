package com.quoren.app.core.common

import org.junit.Assert.assertEquals
import org.junit.Test

class ArabicSearchNormalizerTest {

    @Test
    fun stripsDiacriticsAndUnifiesAlef() {
        val input = "\u0671\u0644\u0652\u062D\u064E\u0645\u0652\u062F\u064F"
        assertEquals("\u0627\u0644\u062D\u0645\u062F", ArabicSearchNormalizer.normalize(input))
    }

    @Test
    fun mapsAlefMaksuraToYeh() {
        assertEquals("\u0645\u0648\u0633\u064A", ArabicSearchNormalizer.normalize("\u0645\u0648\u0633\u0649"))
    }

    @Test
    fun collapsesWhitespace() {
        assertEquals("\u0627 \u0628", ArabicSearchNormalizer.normalize("  \u0627   \u0628 "))
    }

    @Test
    fun doesNotMutateInput() {
        val input = "\u0628\u0650\u0633\u0652\u0645\u0650"
        val copy = String(input.toCharArray())
        ArabicSearchNormalizer.normalize(input)
        assertEquals(copy, input)
    }
}
