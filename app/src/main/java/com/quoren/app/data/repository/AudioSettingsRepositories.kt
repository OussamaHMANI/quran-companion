package com.quoren.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.quoren.app.data.audio.ReciterCatalog
import com.quoren.app.domain.model.AudioSettings
import com.quoren.app.domain.model.RepeatCount
import com.quoren.app.domain.model.Reciter
import com.quoren.app.domain.model.StreamingQuality
import com.quoren.app.domain.repository.AudioSettingsRepository
import com.quoren.app.domain.repository.ReciterRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.audioDataStore: DataStore<Preferences> by preferencesDataStore("audio_settings")
private val defaultReciterKey = stringPreferencesKey("default_reciter")

internal fun MutablePreferences.setDefaultReciter(reciterId: String?) {
    if (reciterId == null) remove(defaultReciterKey) else this[defaultReciterKey] = reciterId
}

@Singleton
class AudioSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AudioSettingsRepository {

    private val ayahRepeatKey = stringPreferencesKey("ayah_repeat")
    private val rangeRepeatKey = stringPreferencesKey("range_repeat")
    private val delayKey = intPreferencesKey("repeat_delay_s")
    private val qualityKey = stringPreferencesKey("streaming_quality")
    private val autoDownloadKey = booleanPreferencesKey("auto_download")
    private val cacheKey = intPreferencesKey("cache_limit_mb")

    override fun observe(): Flow<AudioSettings> = context.audioDataStore.data.map { it.toSettings() }

    override suspend fun update(transform: (AudioSettings) -> AudioSettings) {
        context.audioDataStore.edit { p ->
            val next = transform(p.toSettings())
            p.setDefaultReciter(next.defaultReciterId)
            p[ayahRepeatKey] = next.ayahRepeat.name
            p[rangeRepeatKey] = next.rangeRepeat.name
            p[delayKey] = next.repeatDelaySeconds
            p[qualityKey] = next.streamingQuality.name
            p[autoDownloadKey] = next.autoDownload
            p[cacheKey] = next.cacheLimitMb
        }
    }

    private fun Preferences.toSettings() = AudioSettings(
        defaultReciterId = this[defaultReciterKey],
        ayahRepeat = enumOrDefault(this[ayahRepeatKey], RepeatCount.ONE),
        rangeRepeat = enumOrDefault(this[rangeRepeatKey], RepeatCount.ONE),
        repeatDelaySeconds = (this[delayKey] ?: 0).takeIf { it in AudioSettings.DELAY_OPTIONS_SECONDS } ?: 0,
        streamingQuality = enumOrDefault(this[qualityKey], StreamingQuality.HIGH),
        autoDownload = this[autoDownloadKey] ?: false,
        cacheLimitMb = (this[cacheKey] ?: AudioSettings.DEFAULT_CACHE_LIMIT_MB).coerceIn(64, 4096),
    )

    private inline fun <reified E : Enum<E>> enumOrDefault(name: String?, default: E): E =
        name?.let { runCatching { enumValueOf<E>(it) }.getOrNull() } ?: default
}

@Singleton
class ReciterRepositoryImpl @Inject constructor(
    private val settings: AudioSettingsRepository,
) : ReciterRepository {

    override suspend fun getAllReciters(): List<Reciter> = ReciterCatalog.all

    override suspend fun getReciterById(id: String): Reciter? = ReciterCatalog.all.firstOrNull { it.id == id }

    override suspend fun getDefaultReciter(): Reciter = resolve(settings.observe().first().defaultReciterId)

    override suspend fun setDefaultReciter(id: String) {
        require(ReciterCatalog.all.any { it.id == id }) { "Unknown reciter" }
        settings.update { it.copy(defaultReciterId = id) }
    }

    override fun observeDefaultReciter(): Flow<Reciter> = settings.observe().map { resolve(it.defaultReciterId) }

    private fun resolve(id: String?): Reciter =
        ReciterCatalog.all.firstOrNull { it.id == id } ?: ReciterCatalog.all.first { it.id == ReciterCatalog.DEFAULT_ID }
}
