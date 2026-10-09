package com.quoren.app.domain.repository

import com.quoren.app.domain.model.Ayah
import com.quoren.app.domain.model.Bookmark
import com.quoren.app.domain.model.JuzStart
import com.quoren.app.domain.model.ReaderSettings
import com.quoren.app.domain.model.ReadingProgress
import com.quoren.app.domain.model.Surah
import kotlinx.coroutines.flow.Flow

/** Read-only access to the canonical Quran dataset. */
interface QuranRepository {
    suspend fun getSurahs(): List<Surah>
    suspend fun getJuzStarts(): List<JuzStart>
    suspend fun getPage(page: Int): List<Ayah>
    suspend fun getAyah(ayahId: Int): Ayah?

    /** Canonical basmala, taken verbatim from the dataset (Al-Fatiha 1:1). */
    suspend fun getBasmala(): String
    suspend fun search(query: String, limit: Int = 100): List<Ayah>
}

interface BookmarkRepository {
    fun observeBookmarks(): Flow<List<Bookmark>>
    fun observeBookmarkedAyahIds(): Flow<Set<Int>>
    suspend fun toggle(ayahId: Int)
}

interface ReadingProgressRepository {
    fun observe(): Flow<ReadingProgress?>
    suspend fun save(progress: ReadingProgress)
}

interface ReaderSettingsRepository {
    fun observe(): Flow<ReaderSettings>
    suspend fun update(transform: (ReaderSettings) -> ReaderSettings)
}
