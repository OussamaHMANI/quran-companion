package com.quoren.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.withTransaction
import com.quoren.app.data.local.BookmarkEntity
import com.quoren.app.data.local.QuranDatabase
import com.quoren.app.data.local.ReadingProgressEntity
import com.quoren.app.domain.model.Bookmark
import com.quoren.app.domain.model.ReaderSettings
import com.quoren.app.domain.model.ReadingProgress
import com.quoren.app.domain.model.ThemeMode
import com.quoren.app.domain.repository.BookmarkRepository
import com.quoren.app.domain.repository.ReaderSettingsRepository
import com.quoren.app.domain.repository.ReadingProgressRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepositoryImpl @Inject constructor(private val db: QuranDatabase) : BookmarkRepository {
    private val dao = db.bookmarkDao()

    override fun observeBookmarks(): Flow<List<Bookmark>> =
        dao.observeAll().map { list -> list.map { Bookmark(it.id, it.ayahId, it.createdAt, it.note) } }

    override fun observeBookmarkedAyahIds(): Flow<Set<Int>> = dao.observeAyahIds().map { it.toSet() }

    override suspend fun toggle(ayahId: Int) {
        db.withTransaction {
            if (dao.count(ayahId) > 0) {
                dao.deleteByAyahId(ayahId)
            } else {
                dao.insert(BookmarkEntity(ayahId = ayahId, createdAt = System.currentTimeMillis()))
            }
        }
    }
}

@Singleton
class ReadingProgressRepositoryImpl @Inject constructor(db: QuranDatabase) : ReadingProgressRepository {
    private val dao = db.progressDao()

    override fun observe(): Flow<ReadingProgress?> =
        dao.observe().map { e -> e?.let { ReadingProgress(it.surahId, it.ayahId, it.page, it.updatedAt) } }

    override suspend fun save(progress: ReadingProgress) {
        dao.upsert(ReadingProgressEntity(0, progress.surahId, progress.ayahId, progress.page, progress.updatedAt))
    }
}

private val Context.readerDataStore: DataStore<Preferences> by preferencesDataStore("reader_settings")

@Singleton
class ReaderSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ReaderSettingsRepository {

    private val fontKey = floatPreferencesKey("font_size_sp")
    private val spacingKey = floatPreferencesKey("line_spacing")
    private val themeKey = stringPreferencesKey("theme_mode")

    override fun observe(): Flow<ReaderSettings> = context.readerDataStore.data.map { p ->
        ReaderSettings(
            fontSizeSp = (p[fontKey] ?: ReaderSettings.DEFAULT_FONT_SIZE_SP)
                .coerceIn(ReaderSettings.MIN_FONT_SIZE_SP, ReaderSettings.MAX_FONT_SIZE_SP),
            lineSpacing = (p[spacingKey] ?: ReaderSettings.DEFAULT_LINE_SPACING)
                .coerceIn(ReaderSettings.MIN_LINE_SPACING, ReaderSettings.MAX_LINE_SPACING),
            themeMode = p[themeKey]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM,
        )
    }

    override suspend fun update(transform: (ReaderSettings) -> ReaderSettings) {
        context.readerDataStore.edit { p ->
            val current = ReaderSettings(
                fontSizeSp = p[fontKey] ?: ReaderSettings.DEFAULT_FONT_SIZE_SP,
                lineSpacing = p[spacingKey] ?: ReaderSettings.DEFAULT_LINE_SPACING,
                themeMode = p[themeKey]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM,
            )
            val next = transform(current)
            p[fontKey] = next.fontSizeSp.coerceIn(ReaderSettings.MIN_FONT_SIZE_SP, ReaderSettings.MAX_FONT_SIZE_SP)
            p[spacingKey] = next.lineSpacing.coerceIn(ReaderSettings.MIN_LINE_SPACING, ReaderSettings.MAX_LINE_SPACING)
            p[themeKey] = next.themeMode.name
        }
    }
}
