package com.quoren.app.data.importer

import com.quoren.app.core.common.ArabicSearchNormalizer
import com.quoren.app.domain.model.QuranStructure
import com.quoren.app.domain.model.RevelationType
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

class DatasetIntegrityException(message: String) : IllegalStateException(message)

/** Pinned SHA-256 digests of the bundled Tanzil files. Changing the files requires updating these deliberately. */
object QuranDatasetManifest {
    const val TEXT_ASSET = "quran-uthmani.txt"
    const val TEXT_SHA256 = "BF4F57B968D03F4131C070B1E285DA9BE0E0A108A21C910E872801CA273312C8"
    const val METADATA_ASSET = "quran-data.xml"
    const val METADATA_SHA256 = "8867C1D88191472ADEC9DB694B3CD9F135B1A2EF580574D32CF888DCB22C5C7A"
}

data class RawAyah(val surah: Int, val ayah: Int, val text: String)

/**
 * [ayahs] hold canonical text with the dataset's per-surah basmala prefix separated out.
 * [prefixes] keeps each removed prefix verbatim (surah id -> prefix) so the split is provably lossless.
 */
data class ParsedQuranText(val basmala: String, val ayahs: List<RawAyah>, val prefixes: Map<Int, String>)

data class SurahMeta(
    val index: Int,
    val ayahCount: Int,
    val startOffset: Int,
    val nameArabic: String,
    val nameTransliteration: String,
    val nameEnglish: String,
    val type: RevelationType,
)

data class Marker(val index: Int, val surah: Int, val ayah: Int)

data class QuranMetadata(
    val surahs: List<SurahMeta>,
    val juzs: List<Marker>,
    val quarters: List<Marker>,
    val pages: List<Marker>,
)

object TanzilTextParser {

    /**
     * Tanzil prefixes the basmala to the first ayah of every surah except Al-Fatiha and At-Tawba.
     * That prefix is split off structurally (never edited) and the split is verified to be lossless.
     */
    fun parse(content: String): ParsedQuranText {
        val raw = ArrayList<RawAyah>(QuranStructure.AYAH_COUNT)
        for (line in content.removePrefix("\uFEFF").split('\n')) {
            val l = line.removeSuffix("\r")
            if (l.isBlank() || l.startsWith('#')) continue
            val parts = l.split('|', limit = 3)
            if (parts.size != 3) throw DatasetIntegrityException("Malformed line: ${l.take(20)}")
            val surah = parts[0].toIntOrNull() ?: throw DatasetIntegrityException("Bad surah in line")
            val ayah = parts[1].toIntOrNull() ?: throw DatasetIntegrityException("Bad ayah in line")
            raw += RawAyah(surah, ayah, parts[2])
        }
        val first = raw.firstOrNull() ?: throw DatasetIntegrityException("Empty dataset")
        if (first.surah != 1 || first.ayah != 1) throw DatasetIntegrityException("Dataset must start at 1:1")
        val basmala = first.text
        val basmalaKey = ArabicSearchNormalizer.normalize(basmala)
        val basmalaWords = basmala.split(' ').size
        val prefixes = LinkedHashMap<Int, String>()
        val ayahs = raw.map { r ->
            if (r.ayah == 1 && r.surah != 1 && r.surah != 9) {
                // Some surahs carry a differently-voweled copy of the basmala, so match by letters, not bytes.
                val words = r.text.split(' ', limit = basmalaWords + 1)
                if (words.size != basmalaWords + 1 || words.last().isEmpty()) {
                    throw DatasetIntegrityException("Surah ${r.surah} lacks the expected basmala prefix")
                }
                val prefix = words.take(basmalaWords).joinToString(" ")
                if (ArabicSearchNormalizer.normalize(prefix) != basmalaKey) {
                    throw DatasetIntegrityException("Surah ${r.surah} prefix is not a basmala")
                }
                prefixes[r.surah] = prefix
                r.copy(text = r.text.substring(prefix.length + 1))
            } else {
                if (r.surah == 9 && r.ayah == 1 && ArabicSearchNormalizer.normalize(r.text).startsWith(basmalaKey)) {
                    throw DatasetIntegrityException("Unexpected basmala prefix at 9:1")
                }
                r
            }
        }
        return ParsedQuranText(basmala, ayahs, prefixes)
    }
}

object TanzilMetadataParser {

    fun parse(bytes: ByteArray): QuranMetadata {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bytes.inputStream())
        fun elements(tag: String): List<Element> {
            val nodes = doc.getElementsByTagName(tag)
            return (0 until nodes.length).map { nodes.item(it) as Element }
        }
        fun Element.int(name: String) = getAttribute(name).toInt()
        fun markers(tag: String) = elements(tag).map { Marker(it.int("index"), it.int("sura"), it.int("aya")) }

        val surahs = elements("sura").map {
            SurahMeta(
                index = it.int("index"),
                ayahCount = it.int("ayas"),
                startOffset = it.int("start"),
                nameArabic = it.getAttribute("name"),
                nameTransliteration = it.getAttribute("tname"),
                nameEnglish = it.getAttribute("ename"),
                type = if (it.getAttribute("type") == "Meccan") RevelationType.MECCAN else RevelationType.MEDINAN,
            )
        }
        return QuranMetadata(surahs, markers("juz"), markers("quarter"), markers("page"))
    }
}

object MarkerAssigner {

    /** For each ayah returns the index of the last marker starting at or before it. */
    fun assign(ayahs: List<RawAyah>, markers: List<Marker>): IntArray {
        val sorted = markers.sortedBy { it.index }
        if (sorted.isEmpty()) throw DatasetIntegrityException("No markers")
        val result = IntArray(ayahs.size)
        var m = 0
        for ((i, a) in ayahs.withIndex()) {
            if (isAfter(sorted[0], a)) throw DatasetIntegrityException("Marker starts after first ayah")
            while (m + 1 < sorted.size && !isAfter(sorted[m + 1], a)) m++
            result[i] = sorted[m].index
        }
        return result
    }

    private fun isAfter(marker: Marker, ayah: RawAyah): Boolean =
        marker.surah > ayah.surah || (marker.surah == ayah.surah && marker.ayah > ayah.ayah)
}

object QuranDatasetValidator {

    fun validate(parsed: ParsedQuranText, meta: QuranMetadata) {
        val ayahs = parsed.ayahs
        check(ayahs.size == QuranStructure.AYAH_COUNT) { "Expected ${QuranStructure.AYAH_COUNT} ayahs, found ${ayahs.size}" }
        check(meta.surahs.size == QuranStructure.SURAH_COUNT) { "Expected 114 surahs" }
        check(meta.pages.size == QuranStructure.PAGE_COUNT) { "Expected 604 pages" }
        check(meta.juzs.size == QuranStructure.JUZ_COUNT) { "Expected 30 juz" }
        check(meta.quarters.size == QuranStructure.QUARTER_COUNT) { "Expected 240 quarters" }

        var offset = 0
        for (s in meta.surahs.sortedBy { it.index }) {
            check(s.startOffset == offset) { "Surah ${s.index} offset mismatch" }
            for (n in 1..s.ayahCount) {
                val a = ayahs[offset + n - 1]
                check(a.surah == s.index && a.ayah == n) { "Sequence break at ${s.index}:$n" }
                check(a.text.isNotEmpty()) { "Empty text at ${s.index}:$n" }
            }
            offset += s.ayahCount
        }
        check(offset == ayahs.size) { "Surah counts do not cover the dataset" }
        for (markers in listOf(meta.pages, meta.juzs, meta.quarters)) {
            check(markers.map { it.index } == (1..markers.size).toList()) { "Non-contiguous marker indexes" }
        }
    }
}
