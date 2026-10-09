package com.quoren.app.data.audio

import android.content.Context
import com.quoren.app.domain.model.AudioSource
import com.quoren.app.domain.model.StreamingQuality
import com.quoren.app.domain.repository.AudioSourceProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioFileStore(private val root: File) {
    @Inject
    constructor(@ApplicationContext context: Context) : this(File(context.filesDir, "audio"))

    fun surahDir(reciterId: String, surahId: Int) = File(root, "$reciterId/${"%03d".format(surahId)}")

    fun ayahFile(reciterId: String, surahId: Int, ayahNumber: Int) =
        File(surahDir(reciterId, surahId), ReciterCatalog.ayahFileName(surahId, ayahNumber))

    /** Only fully downloaded files exist under their final name; partial ones use a .part suffix. */
    fun existingAyahFile(reciterId: String, surahId: Int, ayahNumber: Int): File? =
        ayahFile(reciterId, surahId, ayahNumber).takeIf { it.isFile && it.length() > 0 }
}

@Singleton
class AudioSourceProviderImpl @Inject constructor(private val files: AudioFileStore) : AudioSourceProvider {

    override suspend fun resolve(reciterId: String, surahId: Int, ayahNumber: Int, quality: StreamingQuality): AudioSource? {
        val reciter = ReciterCatalog.all.firstOrNull { it.id == reciterId } ?: return null
        val base = if (quality == StreamingQuality.LOW) reciter.audioBaseUrlLow ?: reciter.audioBaseUrl else reciter.audioBaseUrl
        val local = files.existingAyahFile(reciterId, surahId, ayahNumber)
        return AudioSource(ReciterCatalog.ayahUrl(base, surahId, ayahNumber), local?.absolutePath)
    }
}
