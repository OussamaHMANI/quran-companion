package com.quoren.app.data.repository

import com.quoren.app.core.common.ArabicSearchNormalizer
import com.quoren.app.data.importer.QuranDatasetImporter
import com.quoren.app.data.local.AyahEntity
import com.quoren.app.data.local.QuranDatabase
import com.quoren.app.domain.model.Ayah
import com.quoren.app.domain.model.JuzStart
import com.quoren.app.domain.model.RevelationType
import com.quoren.app.domain.model.Surah
import com.quoren.app.domain.repository.QuranRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuranRepositoryImpl @Inject constructor(
    db: QuranDatabase,
    private val importer: QuranDatasetImporter,
) : QuranRepository {

    private val dao = db.quranDao()

    @Volatile
    private var surahCache: List<Surah>? = null

    override suspend fun getSurahs(): List<Surah> {
        surahCache?.let { return it }
        importer.ensureReady()
        return dao.surahs().map {
            Surah(
                id = it.id,
                nameArabic = it.nameArabic,
                nameTransliteration = it.nameTransliteration,
                nameEnglish = it.nameEnglish,
                numberOfAyahs = it.numberOfAyahs,
                revelationType = RevelationType.valueOf(it.revelationType),
                firstPage = it.firstPage,
            )
        }.also { surahCache = it }
    }

    override suspend fun getJuzStarts(): List<JuzStart> {
        importer.ensureReady()
        return dao.juzStarts().map { JuzStart(it.juz, it.id, it.surahId, it.ayahNumber, it.page) }
    }

    override suspend fun getPage(page: Int): List<Ayah> {
        importer.ensureReady()
        return dao.ayahsByPage(page).map { it.toDomain() }
    }

    override suspend fun getAyah(ayahId: Int): Ayah? {
        importer.ensureReady()
        return dao.ayah(ayahId)?.toDomain()
    }

    override suspend fun getBasmala(): String {
        importer.ensureReady()
        return checkNotNull(dao.meta(QuranDatasetImporter.META_BASMALA)) { "Basmala missing" }
    }

    override suspend fun search(query: String, limit: Int): List<Ayah> {
        importer.ensureReady()
        val key = ArabicSearchNormalizer.normalize(query).filterNot { it == '%' || it == '_' || it == '\\' }
        if (key.length < 2) return emptyList()
        return dao.search(key, limit).map { it.toDomain() }
    }

    private fun AyahEntity.toDomain() = Ayah(id, surahId, ayahNumber, textUthmani, page, juz, hizb)
}
