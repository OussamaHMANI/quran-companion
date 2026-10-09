package com.quoren.app.di

import android.content.Context
import android.os.Looper
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.room.Room
import com.quoren.app.data.audio.AudioCacheManagerImpl
import com.quoren.app.data.audio.AudioSourceProviderImpl
import com.quoren.app.data.audio.ConfigurableLruCacheEvictor
import com.quoren.app.data.audio.Media3AudioPlayerManager
import com.quoren.app.data.download.DownloadRepositoryImpl
import com.quoren.app.data.memorization.AndroidOfflineRecitationRecognizer
import com.quoren.app.data.backup.LocalBackupRepositoryImpl
import com.quoren.app.data.local.MIGRATION_1_2
import com.quoren.app.data.local.MIGRATION_2_3
import com.quoren.app.data.local.QuranDatabase
import com.quoren.app.data.repository.AudioSettingsRepositoryImpl
import com.quoren.app.data.repository.MemorizationRepositoryImpl
import com.quoren.app.data.repository.ReciterRepositoryImpl
import com.quoren.app.domain.model.AudioSettings
import com.quoren.app.domain.repository.AudioCacheManager
import com.quoren.app.domain.repository.AudioPlayerManager
import com.quoren.app.domain.repository.AudioSettingsRepository
import com.quoren.app.domain.repository.AudioSourceProvider
import com.quoren.app.domain.repository.DownloadRepository
import com.quoren.app.domain.repository.ReciterRepository
import com.quoren.app.domain.repository.MemorizationRepository
import com.quoren.app.domain.memorization.RecitationRecognizer
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit
import com.quoren.app.data.repository.BookmarkRepositoryImpl
import com.quoren.app.data.repository.QuranRepositoryImpl
import com.quoren.app.data.repository.ReaderSettingsRepositoryImpl
import com.quoren.app.data.repository.ReadingProgressRepositoryImpl
import com.quoren.app.data.repository.PrayerRepositoryImpl
import com.quoren.app.data.prayer.PrayerNotificationSchedulerImpl
import com.quoren.app.data.prayer.AndroidPrayerCitySearchRepository
import com.quoren.app.data.qibla.AndroidCompassRepository
import com.quoren.app.data.mosque.MosqueRepositoryImpl
import com.quoren.app.domain.repository.BookmarkRepository
import com.quoren.app.domain.repository.QuranRepository
import com.quoren.app.domain.repository.ReaderSettingsRepository
import com.quoren.app.domain.repository.ReadingProgressRepository
import com.quoren.app.domain.repository.PrayerRepository
import com.quoren.app.domain.repository.PrayerNotificationScheduler
import com.quoren.app.domain.repository.PrayerCitySearchRepository
import com.quoren.app.domain.repository.CompassRepository
import com.quoren.app.domain.repository.MosqueRepository
import com.quoren.app.domain.repository.LocalBackupRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): QuranDatabase =
        Room.databaseBuilder(context, QuranDatabase::class.java, "quoren.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun quran(impl: QuranRepositoryImpl): QuranRepository
    @Binds abstract fun bookmarks(impl: BookmarkRepositoryImpl): BookmarkRepository
    @Binds abstract fun progress(impl: ReadingProgressRepositoryImpl): ReadingProgressRepository
    @Binds abstract fun settings(impl: ReaderSettingsRepositoryImpl): ReaderSettingsRepository
    @Binds abstract fun reciters(impl: ReciterRepositoryImpl): ReciterRepository
    @Binds abstract fun audioSettings(impl: AudioSettingsRepositoryImpl): AudioSettingsRepository
    @Binds abstract fun audioSources(impl: AudioSourceProviderImpl): AudioSourceProvider
    @Binds abstract fun downloads(impl: DownloadRepositoryImpl): DownloadRepository
    @Binds abstract fun audioCache(impl: AudioCacheManagerImpl): AudioCacheManager
    @Binds abstract fun player(impl: Media3AudioPlayerManager): AudioPlayerManager
    @Binds abstract fun memorization(impl: MemorizationRepositoryImpl): MemorizationRepository
    @Binds abstract fun recitationRecognizer(impl: AndroidOfflineRecitationRecognizer): RecitationRecognizer
    @Binds abstract fun prayers(impl: PrayerRepositoryImpl): PrayerRepository
    @Binds abstract fun prayerNotifications(impl: PrayerNotificationSchedulerImpl): PrayerNotificationScheduler
    @Binds abstract fun prayerCitySearch(impl: AndroidPrayerCitySearchRepository): PrayerCitySearchRepository
    @Binds abstract fun compass(impl: AndroidCompassRepository): CompassRepository
    @Binds abstract fun mosques(impl: MosqueRepositoryImpl): MosqueRepository
    @Binds abstract fun localBackup(impl: LocalBackupRepositoryImpl): LocalBackupRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AudioModule {
    private const val USER_AGENT = "Quoren"

    @Provides
    @Singleton
    fun cacheEvictor(): ConfigurableLruCacheEvictor =
        ConfigurableLruCacheEvictor(AudioSettings.DEFAULT_CACHE_LIMIT_MB.toLong() * 1024 * 1024)

    @Provides
    @Singleton
    fun simpleCache(@ApplicationContext context: Context, evictor: ConfigurableLruCacheEvictor): SimpleCache =
        SimpleCache(File(context.cacheDir, "audio_cache"), evictor, StandaloneDatabaseProvider(context))

    @Provides
    @Singleton
    fun okHttp(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun exoPlayer(@ApplicationContext context: Context, cache: SimpleCache): ExoPlayer {
        val http = DefaultHttpDataSource.Factory()
            .setUserAgent(USER_AGENT)
            .setConnectTimeoutMs(10_000)
            .setReadTimeoutMs(10_000)
        val cached = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context, http))
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        val attributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
            .build()
        return ExoPlayer.Builder(context)
            .setLooper(Looper.getMainLooper())
            .setMediaSourceFactory(DefaultMediaSourceFactory(cached))
            .setAudioAttributes(attributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .build()
    }
}
