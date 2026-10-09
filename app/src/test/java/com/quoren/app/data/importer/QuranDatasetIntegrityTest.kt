package com.quoren.app.data.importer

import com.quoren.app.domain.model.QuranStructure
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest

/** Blocking integrity checks for the bundled canonical Quran dataset. */
class QuranDatasetIntegrityTest {

    private val assets = File("src/main/assets")
    private val textBytes by lazy { File(assets, QuranDatasetManifest.TEXT_ASSET).readBytes() }
    private val metaBytes by lazy { File(assets, QuranDatasetManifest.METADATA_ASSET).readBytes() }
    private val parsed by lazy { TanzilTextParser.parse(String(textBytes, Charsets.UTF_8)) }
    private val meta by lazy { TanzilMetadataParser.parse(metaBytes) }

    private fun sha256(bytes: ByteArray) =
        MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02X".format(it) }

    @Test
    fun textFileMatchesPinnedChecksum() {
        assertEquals(QuranDatasetManifest.TEXT_SHA256, sha256(textBytes))
    }

    @Test
    fun metadataFileMatchesPinnedChecksum() {
        assertEquals(QuranDatasetManifest.METADATA_SHA256, sha256(metaBytes))
    }

    @Test
    fun structureIsValid() {
        QuranDatasetValidator.validate(parsed, meta)
        assertEquals(QuranStructure.AYAH_COUNT, parsed.ayahs.size)
        assertEquals(QuranStructure.SURAH_COUNT, meta.surahs.size)
    }

    @Test
    fun perSurahAyahCountsMatchMetadata() {
        val counts = parsed.ayahs.groupingBy { it.surah }.eachCount()
        meta.surahs.forEach { assertEquals("surah ${it.index}", it.ayahCount, counts[it.index]) }
    }

    @Test
    fun splittingBasmalaIsLossless() {
        val raw = String(textBytes, Charsets.UTF_8).lines()
            .filter { it.isNotBlank() && !it.startsWith("#") }
            .map { it.trimEnd('\r').split('|', limit = 3) }
        assertEquals(parsed.ayahs.size, raw.size)
        raw.forEachIndexed { i, parts ->
            val a = parsed.ayahs[i]
            val rebuilt = parsed.prefixes[a.surah]?.takeIf { a.ayah == 1 }?.let { "$it ${a.text}" } ?: a.text
            assertEquals("${a.surah}:${a.ayah}", parts[2], rebuilt)
        }
    }

    @Test
    fun onlyKnownSurahsHaveBasmalaPrefixDifferingFromAlFatiha() {
        val differing = parsed.prefixes.filterValues { it != parsed.basmala }.keys
        assertEquals(setOf(95, 97), differing)
        // Known dataset variant: an extra shadda (U+0651) on the first letter; nothing else differs.
        differing.forEach { s ->
            assertEquals(parsed.basmala, parsed.prefixes.getValue(s).replaceFirst("\u0651", ""))
        }
        assertEquals(112, parsed.prefixes.size)
    }

    @Test
    fun surahNineHasNoBasmalaPrefix() {
        val first = parsed.ayahs.first { it.surah == 9 && it.ayah == 1 }
        assertFalse(first.text.startsWith(parsed.basmala))
    }

    @Test
    fun everyMarkerResolvesToAnAyahAndAssignmentIsMonotonic() {
        for (markers in listOf(meta.pages, meta.juzs, meta.quarters)) {
            val assigned = MarkerAssigner.assign(parsed.ayahs, markers)
            for (i in 1 until assigned.size) assertTrue(assigned[i] >= assigned[i - 1])
            assertEquals(markers.size, assigned.toSet().size)
            assertEquals(markers.size, assigned.last())
        }
    }

    @Test
    fun textContainsOnlyArabicScriptSpacesAndMarks() {
        val allowed = { c: Char ->
            c == ' ' || c.code in 0x0600..0x06FF || c.code in 0x0750..0x077F || c.code in 0x08A0..0x08FF
        }
        parsed.ayahs.forEach { a ->
            val bad = a.text.firstOrNull { !allowed(it) }
            assertTrue("${a.surah}:${a.ayah} has unexpected char ${bad?.code}", bad == null)
        }
    }

    @Test
    fun tamperedTextIsDetectedByChecksum() {
        val tampered = textBytes.copyOf().also { it[it.size / 2] = (it[it.size / 2] + 1).toByte() }
        assertNotEquals(QuranDatasetManifest.TEXT_SHA256, sha256(tampered))
    }
}
