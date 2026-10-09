package com.quoren.app.data.importer

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class QuranParsingTest {

    private fun ayah(s: Int, a: Int) = RawAyah(s, a, "x")

    @Test
    fun assignsLastMarkerAtOrBeforeAyah() {
        val ayahs = listOf(ayah(1, 1), ayah(1, 2), ayah(2, 1), ayah(2, 2), ayah(2, 3))
        val markers = listOf(Marker(1, 1, 1), Marker(2, 2, 1), Marker(3, 2, 3))
        assertArrayEquals(intArrayOf(1, 1, 2, 2, 3), MarkerAssigner.assign(ayahs, markers))
    }

    @Test
    fun rejectsMarkersStartingAfterFirstAyah() {
        assertThrows(DatasetIntegrityException::class.java) {
            MarkerAssigner.assign(listOf(ayah(1, 1)), listOf(Marker(1, 1, 2)))
        }
    }

    @Test
    fun rejectsMalformedLines() {
        assertThrows(DatasetIntegrityException::class.java) { TanzilTextParser.parse("1|1") }
    }

    @Test
    fun rejectsMissingBasmalaPrefix() {
        assertThrows(DatasetIntegrityException::class.java) { TanzilTextParser.parse("1|1|ب ب ب ب\n2|1|ا\n") }
    }

    @Test
    fun splitsBasmalaPrefixWithoutChangingRemainder() {
        val parsed = TanzilTextParser.parse("# c\n1|1|ب ب\n2|1|ب ب ج\n9|1|د\n")
        assertArrayEquals(arrayOf("ب ب", "ج", "د"), parsed.ayahs.map { it.text }.toTypedArray())
    }
}
