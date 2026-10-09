package com.quoren.app.data.download

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.quoren.app.data.audio.AudioFileStore
import com.quoren.app.data.audio.ReciterCatalog
import com.quoren.app.data.local.DownloadDao
import com.quoren.app.data.local.QuranDatabase
import com.quoren.app.domain.model.AudioError
import com.quoren.app.domain.model.DownloadStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.coroutineContext

class DownloadFailure(val error: AudioError, cause: Throwable? = null) : IOException(error.name, cause)

/** Downloads every ayah file of one surah, skipping files that already exist. */
@HiltWorker
class SurahDownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val db: QuranDatabase,
    private val files: AudioFileStore,
    private val http: OkHttpClient,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val reciterId = inputData.getString(KEY_RECITER) ?: return@withContext Result.failure()
        val surahId = inputData.getInt(KEY_SURAH, -1)
        val total = inputData.getInt(KEY_TOTAL, -1)
        val reciter = ReciterCatalog.all.firstOrNull { it.id == reciterId }
        if (reciter == null || surahId < 1 || total < 1) return@withContext Result.failure()

        val dao = db.downloadDao()
        val dir = files.surahDir(reciterId, surahId).also { it.mkdirs() }
        var bytes = dir.listFiles { f -> f.name.endsWith(".mp3") }?.sumOf { it.length() } ?: 0L
        var done = 0
        try {
            dir.listFiles { f -> f.name.endsWith(".part") }?.forEach { it.delete() }
            for (n in 1..total) {
                coroutineContext.ensureActive()
                val target = files.ayahFile(reciterId, surahId, n)
                if (!(target.isFile && target.length() > 0)) {
                    bytes += fetch(ReciterCatalog.ayahUrl(reciter.audioBaseUrl, surahId, n), target)
                }
                done++
                dao.updateProgress(reciterId, surahId, DownloadStatus.DOWNLOADING.name, done, bytes, System.currentTimeMillis())
            }
            dao.updateProgress(reciterId, surahId, DownloadStatus.COMPLETED.name, total, bytes, System.currentTimeMillis())
            Result.success()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            fail(dao, reciterId, surahId, classify(e))
            Result.failure(workDataOf(KEY_ERROR to classify(e).name))
        }
    }

    private suspend fun fail(dao: DownloadDao, reciterId: String, surahId: Int, error: AudioError) {
        dao.finishActive(reciterId, surahId, DownloadStatus.FAILED.name, error.name, System.currentTimeMillis())
    }

    private fun fetch(url: String, target: File): Long {
        val part = File(target.parentFile, target.name + ".part")
        try {
            http.newCall(Request.Builder().url(url).build()).execute().use { response ->
                if (!response.isSuccessful) throw DownloadFailure(AudioError.SERVER_UNAVAILABLE)
                val body = response.body ?: throw DownloadFailure(AudioError.SERVER_UNAVAILABLE)
                val expected = body.contentLength()
                var written = 0L
                part.outputStream().use { out ->
                    body.byteStream().use { input ->
                        val buffer = ByteArray(16 * 1024)
                        while (true) {
                            val read = input.read(buffer)
                            if (read < 0) break
                            out.write(buffer, 0, read)
                            written += read
                        }
                    }
                }
                if (written == 0L || (expected >= 0 && written != expected)) throw DownloadFailure(AudioError.CORRUPTED_FILE)
                if (!part.renameTo(target)) throw DownloadFailure(AudioError.DOWNLOAD_FAILED)
                return written
            }
        } finally {
            if (part.exists()) part.delete()
        }
    }

    companion object {
        const val KEY_RECITER = "reciter"
        const val KEY_SURAH = "surah"
        const val KEY_TOTAL = "total"
        const val KEY_ERROR = "error"

        fun classify(e: Throwable): AudioError = when {
            e is DownloadFailure -> e.error
            e is UnknownHostException || e is ConnectException -> AudioError.NO_INTERNET
            e is SocketTimeoutException -> AudioError.TIMEOUT
            e is IOException && e.message?.contains("ENOSPC", ignoreCase = true) == true -> AudioError.STORAGE_FULL
            e is IOException && e.message?.contains("No space left", ignoreCase = true) == true -> AudioError.STORAGE_FULL
            else -> AudioError.DOWNLOAD_FAILED
        }
    }
}
