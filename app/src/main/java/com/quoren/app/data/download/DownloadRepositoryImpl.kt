package com.quoren.app.data.download

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.quoren.app.data.audio.AudioFileStore
import com.quoren.app.data.local.DownloadEntity
import com.quoren.app.data.local.QuranDatabase
import com.quoren.app.domain.model.AudioError
import com.quoren.app.domain.model.DownloadStatus
import com.quoren.app.domain.model.DownloadedAudio
import com.quoren.app.domain.repository.DownloadRepository
import com.quoren.app.domain.repository.QuranRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    db: QuranDatabase,
    private val files: AudioFileStore,
    private val quran: QuranRepository,
) : DownloadRepository {

    private val dao = db.downloadDao()
    private val work get() = WorkManager.getInstance(context)

    override fun observeDownloads(): Flow<List<DownloadedAudio>> = dao.observeAll().map { rows ->
        rows.map { e ->
            DownloadedAudio(
                reciterId = e.reciterId,
                surahId = e.surahId,
                localPath = files.surahDir(e.reciterId, e.surahId).absolutePath,
                fileSize = e.totalBytes,
                status = runCatching { DownloadStatus.valueOf(e.status) }.getOrDefault(DownloadStatus.FAILED),
                downloadedAyahs = e.downloadedAyahs,
                totalAyahs = e.totalAyahs,
                error = e.errorCode?.let { runCatching { AudioError.valueOf(it) }.getOrNull() },
            )
        }
    }

    override suspend fun download(reciterId: String, surahId: Int) {
        val total = quran.getSurahs().first { it.id == surahId }.numberOfAyahs
        val existing = dao.find(reciterId, surahId)
        if (existing?.status == DownloadStatus.COMPLETED.name) return
        dao.upsert(
            DownloadEntity(
                reciterId, surahId, DownloadStatus.QUEUED.name,
                existing?.downloadedAyahs ?: 0, total, existing?.totalBytes ?: 0L, null, System.currentTimeMillis(),
            ),
        )
        val request = OneTimeWorkRequestBuilder<SurahDownloadWorker>()
            .setInputData(
                workDataOf(
                    SurahDownloadWorker.KEY_RECITER to reciterId,
                    SurahDownloadWorker.KEY_SURAH to surahId,
                    SurahDownloadWorker.KEY_TOTAL to total,
                ),
            )
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        work.enqueueUniqueWork(workName(reciterId, surahId), ExistingWorkPolicy.REPLACE, request)
    }

    override suspend fun pause(reciterId: String, surahId: Int) {
        dao.setStatus(reciterId, surahId, DownloadStatus.PAUSED.name, System.currentTimeMillis())
        work.cancelUniqueWork(workName(reciterId, surahId))
    }

    override suspend fun resume(reciterId: String, surahId: Int) = download(reciterId, surahId)

    override suspend fun retry(reciterId: String, surahId: Int) = download(reciterId, surahId)

    override suspend fun delete(reciterId: String, surahId: Int) {
        work.cancelUniqueWork(workName(reciterId, surahId))
        dao.delete(reciterId, surahId)
        withContext(Dispatchers.IO) { files.surahDir(reciterId, surahId).deleteRecursively() }
    }

    private fun workName(reciterId: String, surahId: Int) = "download:$reciterId:$surahId"
}
