package com.quoren.app.domain.model

enum class RevelationType { MECCAN, MEDINAN }

enum class ThemeMode { SYSTEM, LIGHT, DARK }

object QuranStructure {
    const val SURAH_COUNT = 114
    const val AYAH_COUNT = 6236
    const val PAGE_COUNT = 604
    const val JUZ_COUNT = 30
    const val QUARTER_COUNT = 240
}

data class Surah(
    val id: Int,
    val nameArabic: String,
    val nameTransliteration: String,
    val nameEnglish: String,
    val numberOfAyahs: Int,
    val revelationType: RevelationType,
    val firstPage: Int,
)

/** Canonical Quran verse. [textUthmani] is exactly the bundled dataset text and must never be transformed. */
data class Ayah(
    val id: Int,
    val surahId: Int,
    val ayahNumber: Int,
    val textUthmani: String,
    val page: Int,
    val juz: Int,
    val hizb: Int,
)

data class JuzStart(
    val juz: Int,
    val ayahId: Int,
    val surahId: Int,
    val ayahNumber: Int,
    val page: Int,
)

data class SearchResult(val ayah: Ayah, val surah: Surah)

data class Bookmark(
    val id: Long,
    val ayahId: Int,
    val createdAt: Long,
    val note: String?,
)

data class BookmarkedAyah(val bookmark: Bookmark, val ayah: Ayah, val surah: Surah)

data class ReadingProgress(
    val surahId: Int,
    val ayahId: Int,
    val page: Int,
    val updatedAt: Long,
)

data class ReaderSettings(
    val fontSizeSp: Float = DEFAULT_FONT_SIZE_SP,
    val lineSpacing: Float = DEFAULT_LINE_SPACING,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
) {
    companion object {
        const val MIN_FONT_SIZE_SP = 20f
        const val MAX_FONT_SIZE_SP = 56f
        const val DEFAULT_FONT_SIZE_SP = 30f
        const val MIN_LINE_SPACING = 1.3f
        const val MAX_LINE_SPACING = 2.6f
        const val DEFAULT_LINE_SPACING = 1.9f
    }
}
