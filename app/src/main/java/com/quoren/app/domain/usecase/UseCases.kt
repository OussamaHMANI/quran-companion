package com.quoren.app.domain.usecase

import com.quoren.app.domain.model.Ayah
import com.quoren.app.domain.model.Bookmark
import com.quoren.app.domain.model.BookmarkedAyah
import com.quoren.app.domain.model.JuzStart
import com.quoren.app.domain.model.ReaderSettings
import com.quoren.app.domain.model.ReadingProgress
import com.quoren.app.domain.model.SearchResult
import com.quoren.app.domain.model.Surah
import com.quoren.app.domain.repository.BookmarkRepository
import com.quoren.app.domain.repository.QuranRepository
import com.quoren.app.domain.repository.ReaderSettingsRepository
import com.quoren.app.domain.repository.ReadingProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSurahsUseCase @Inject constructor(private val quran: QuranRepository) {
    suspend operator fun invoke(): List<Surah> = quran.getSurahs()
}

class GetJuzStartsUseCase @Inject constructor(private val quran: QuranRepository) {
    suspend operator fun invoke(): List<JuzStart> = quran.getJuzStarts()
}

class GetPageUseCase @Inject constructor(private val quran: QuranRepository) {
    suspend operator fun invoke(page: Int): List<Ayah> = quran.getPage(page)
}

class GetAyahUseCase @Inject constructor(private val quran: QuranRepository) {
    suspend operator fun invoke(ayahId: Int): Ayah? = quran.getAyah(ayahId)
}

class GetAyahByReferenceUseCase @Inject constructor(private val quran: QuranRepository) {
    suspend operator fun invoke(surahId: Int, ayahNumber: Int): Ayah? {
        val surahs = quran.getSurahs()
        val target = surahs.firstOrNull { it.id == surahId } ?: return null
        if (ayahNumber !in 1..target.numberOfAyahs) return null
        val firstAyahId = surahs.asSequence().takeWhile { it.id < surahId }.sumOf { it.numberOfAyahs } + 1
        return quran.getAyah(firstAyahId + ayahNumber - 1)
    }
}

class GetBasmalaUseCase @Inject constructor(private val quran: QuranRepository) {
    suspend operator fun invoke(): String = quran.getBasmala()
}

class SearchQuranUseCase @Inject constructor(private val quran: QuranRepository) {
    suspend operator fun invoke(query: String): List<SearchResult> {
        val ayahs = quran.search(query)
        if (ayahs.isEmpty()) return emptyList()
        val surahs = quran.getSurahs().associateBy { it.id }
        return ayahs.mapNotNull { ayah -> surahs[ayah.surahId]?.let { SearchResult(ayah, it) } }
    }
}

class ObserveReadingProgressUseCase @Inject constructor(private val progress: ReadingProgressRepository) {
    operator fun invoke(): Flow<ReadingProgress?> = progress.observe()
}

class SaveReadingProgressUseCase @Inject constructor(private val progress: ReadingProgressRepository) {
    suspend operator fun invoke(surahId: Int, ayahId: Int, page: Int) =
        progress.save(ReadingProgress(surahId, ayahId, page, System.currentTimeMillis()))
}

class ObserveBookmarkedAyahIdsUseCase @Inject constructor(private val bookmarks: BookmarkRepository) {
    operator fun invoke(): Flow<Set<Int>> = bookmarks.observeBookmarkedAyahIds()
}

class ToggleBookmarkUseCase @Inject constructor(private val bookmarks: BookmarkRepository) {
    suspend operator fun invoke(ayahId: Int) = bookmarks.toggle(ayahId)
}

class ObserveBookmarksUseCase @Inject constructor(
    private val bookmarks: BookmarkRepository,
    private val quran: QuranRepository,
) {
    operator fun invoke(): Flow<List<BookmarkedAyah>> = bookmarks.observeBookmarks().map { list ->
        val surahs = quran.getSurahs().associateBy { it.id }
        list.mapNotNull { bookmark: Bookmark ->
            val ayah = quran.getAyah(bookmark.ayahId) ?: return@mapNotNull null
            val surah = surahs[ayah.surahId] ?: return@mapNotNull null
            BookmarkedAyah(bookmark, ayah, surah)
        }
    }
}

class ObserveReaderSettingsUseCase @Inject constructor(private val settings: ReaderSettingsRepository) {
    operator fun invoke(): Flow<ReaderSettings> = settings.observe()
}

class UpdateReaderSettingsUseCase @Inject constructor(private val settings: ReaderSettingsRepository) {
    suspend operator fun invoke(transform: (ReaderSettings) -> ReaderSettings) = settings.update(transform)
}
