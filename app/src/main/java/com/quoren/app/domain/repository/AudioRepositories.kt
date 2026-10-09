package com.quoren.app.domain.repository

import com.quoren.app.domain.model.AudioSettings
import com.quoren.app.domain.model.AudioSource
import com.quoren.app.domain.model.DownloadedAudio
import com.quoren.app.domain.model.PlaybackPosition
import com.quoren.app.domain.model.PlaybackRequest
import com.quoren.app.domain.model.PlaybackState
import com.quoren.app.domain.model.Reciter
import com.quoren.app.domain.model.StreamingQuality
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ReciterRepository {
    suspend fun getAllReciters(): List<Reciter>
    suspend fun getReciterById(id: String): Reciter?
    suspend fun getDefaultReciter(): Reciter
    suspend fun setDefaultReciter(id: String)
    fun observeDefaultReciter(): Flow<Reciter>
}

interface AudioSettingsRepository {
    fun observe(): Flow<AudioSettings>
    suspend fun update(transform: (AudioSettings) -> AudioSettings)
}

interface AudioPlayerManager {
    val state: StateFlow<PlaybackState>

    /** Emits only while collected; polling stops with the collector. */
    fun positionFlow(): Flow<PlaybackPosition>
    suspend fun play(request: PlaybackRequest)
    fun pause()
    fun resume()
    fun stop()
    fun seekToProgress(progress: Float)
    fun next()
    fun previous()
}

interface AudioSourceProvider {
    suspend fun resolve(reciterId: String, surahId: Int, ayahNumber: Int, quality: StreamingQuality): AudioSource?
}

interface DownloadRepository {
    fun observeDownloads(): Flow<List<DownloadedAudio>>
    suspend fun download(reciterId: String, surahId: Int)
    suspend fun pause(reciterId: String, surahId: Int)
    suspend fun resume(reciterId: String, surahId: Int)
    suspend fun retry(reciterId: String, surahId: Int)
    suspend fun delete(reciterId: String, surahId: Int)
}

interface AudioCacheManager {
    suspend fun cacheSizeBytes(): Long
    suspend fun clear()
    fun setLimitMb(limitMb: Int)
}
