package com.quoren.app.data.importer

import android.content.Context
import androidx.room.withTransaction
import com.quoren.app.core.common.ArabicSearchNormalizer
import com.quoren.app.data.local.AyahEntity
import com.quoren.app.data.local.AyahSearchEntity
import com.quoren.app.data.local.DatasetMetaEntity
import com.quoren.app.data.local.QuranDatabase
import com.quoren.app.data.local.SurahEntity
import com.quoren.app.domain.model.QuranStructure
import com.quoren.app.domain.model.RevelationType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/** Imports the verified bundled dataset into Room once, and re-imports if the DB diverges from the pinned digests. */
@Singleton
class QuranDatasetImporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: QuranDatabase,
) {
    private val mutex = Mutex()

    @Volatile
    private var ready = false

    suspend fun ensureReady() {
        if (ready) return
        mutex.withLock {
            if (ready) return
            withContext(Dispatchers.IO) { importIfNeeded() }
            ready = true
        }
    }

    private suspend fun importIfNeeded() {
        val dao = db.quranDao()
        val upToDate = dao.meta(META_TEXT_SHA) == QuranDatasetManifest.TEXT_SHA256 &&
            dao.meta(META_METADATA_SHA) == QuranDatasetManifest.METADATA_SHA256 &&
            dao.ayahCount() == QuranStructure.AYAH_COUNT
        if (upToDate) return

        val textBytes = context.assets.open(QuranDatasetManifest.TEXT_ASSET).use { it.readBytes() }
        val metaBytes = context.assets.open(QuranDatasetManifest.METADATA_ASSET).use { it.readBytes() }
        verifyDigest(textBytes, QuranDatasetManifest.TEXT_SHA256, QuranDatasetManifest.TEXT_ASSET)
        verifyDigest(metaBytes, QuranDatasetManifest.METADATA_SHA256, QuranDatasetManifest.METADATA_ASSET)

        val parsed = TanzilTextParser.parse(String(textBytes, Charsets.UTF_8))
        val meta = TanzilMetadataParser.parse(metaBytes)
        QuranDatasetValidator.validate(parsed, meta)

        val pageOf = MarkerAssigner.assign(parsed.ayahs, meta.pages)
        val juzOf = MarkerAssigner.assign(parsed.ayahs, meta.juzs)
        val quarterOf = MarkerAssigner.assign(parsed.ayahs, meta.quarters)

        val ayahs = parsed.ayahs.mapIndexed { i, a ->
            AyahEntity(
                id = i + 1,
                surahId = a.surah,
                ayahNumber = a.ayah,
                textUthmani = a.text,
                page = pageOf[i],
                juz = juzOf[i],
                hizb = (quarterOf[i] - 1) / 4 + 1,
            )
        }
        val search = ayahs.map { AyahSearchEntity(it.id, ArabicSearchNormalizer.normalize(it.textUthmani)) }
        val surahs = meta.surahs.sortedBy { it.index }.map { s ->
            SurahEntity(
                id = s.index,
                nameArabic = s.nameArabic,
                nameTransliteration = s.nameTransliteration,
                nameEnglish = s.nameEnglish,
                numberOfAyahs = s.ayahCount,
                revelationType = if (s.type == RevelationType.MECCAN) "MECCAN" else "MEDINAN",
                firstPage = ayahs[s.startOffset].page,
            )
        }

        val import = db.importDao()
        db.withTransaction {
            import.clearAyahs()
            import.clearSearch()
            import.clearSurahs()
            import.clearMeta()
            import.insertSurahs(surahs)
            import.insertAyahs(ayahs)
            import.insertSearch(search)
            import.putMeta(DatasetMetaEntity(META_BASMALA, parsed.basmala))
            import.putMeta(DatasetMetaEntity(META_METADATA_SHA, QuranDatasetManifest.METADATA_SHA256))
            import.putMeta(DatasetMetaEntity(META_TEXT_SHA, QuranDatasetManifest.TEXT_SHA256))
        }
    }

    private fun verifyDigest(bytes: ByteArray, expected: String, name: String) {
        val actual = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02X".format(it) }
        if (!actual.equals(expected, ignoreCase = true)) {
            throw DatasetIntegrityException("Checksum mismatch for $name")
        }
    }

    companion object {
        const val META_TEXT_SHA = "text_sha256"
        const val META_METADATA_SHA = "metadata_sha256"
        const val META_BASMALA = "basmala"
    }
}
