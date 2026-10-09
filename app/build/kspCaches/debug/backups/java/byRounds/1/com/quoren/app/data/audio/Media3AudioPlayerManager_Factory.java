package com.quoren.app.data.audio;

import android.content.Context;
import androidx.media3.exoplayer.ExoPlayer;
import com.quoren.app.data.local.QuranDatabase;
import com.quoren.app.domain.repository.AudioSettingsRepository;
import com.quoren.app.domain.repository.AudioSourceProvider;
import com.quoren.app.domain.repository.QuranRepository;
import com.quoren.app.domain.repository.ReciterRepository;
import dagger.Lazy;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class Media3AudioPlayerManager_Factory implements Factory<Media3AudioPlayerManager> {
  private final Provider<Context> contextProvider;

  private final Provider<ExoPlayer> playerProvider;

  private final Provider<ConfigurableLruCacheEvictor> cacheEvictorProvider;

  private final Provider<ReciterRepository> recitersProvider;

  private final Provider<AudioSettingsRepository> settingsProvider;

  private final Provider<AudioSourceProvider> sourcesProvider;

  private final Provider<QuranRepository> quranProvider;

  private final Provider<QuranDatabase> dbProvider;

  public Media3AudioPlayerManager_Factory(Provider<Context> contextProvider,
      Provider<ExoPlayer> playerProvider,
      Provider<ConfigurableLruCacheEvictor> cacheEvictorProvider,
      Provider<ReciterRepository> recitersProvider,
      Provider<AudioSettingsRepository> settingsProvider,
      Provider<AudioSourceProvider> sourcesProvider, Provider<QuranRepository> quranProvider,
      Provider<QuranDatabase> dbProvider) {
    this.contextProvider = contextProvider;
    this.playerProvider = playerProvider;
    this.cacheEvictorProvider = cacheEvictorProvider;
    this.recitersProvider = recitersProvider;
    this.settingsProvider = settingsProvider;
    this.sourcesProvider = sourcesProvider;
    this.quranProvider = quranProvider;
    this.dbProvider = dbProvider;
  }

  @Override
  public Media3AudioPlayerManager get() {
    return newInstance(contextProvider.get(), DoubleCheck.lazy(playerProvider), cacheEvictorProvider.get(), recitersProvider.get(), settingsProvider.get(), sourcesProvider.get(), quranProvider.get(), dbProvider.get());
  }

  public static Media3AudioPlayerManager_Factory create(Provider<Context> contextProvider,
      Provider<ExoPlayer> playerProvider,
      Provider<ConfigurableLruCacheEvictor> cacheEvictorProvider,
      Provider<ReciterRepository> recitersProvider,
      Provider<AudioSettingsRepository> settingsProvider,
      Provider<AudioSourceProvider> sourcesProvider, Provider<QuranRepository> quranProvider,
      Provider<QuranDatabase> dbProvider) {
    return new Media3AudioPlayerManager_Factory(contextProvider, playerProvider, cacheEvictorProvider, recitersProvider, settingsProvider, sourcesProvider, quranProvider, dbProvider);
  }

  public static Media3AudioPlayerManager newInstance(Context context,
      Lazy<ExoPlayer> playerProvider, ConfigurableLruCacheEvictor cacheEvictor,
      ReciterRepository reciters, AudioSettingsRepository settings, AudioSourceProvider sources,
      QuranRepository quran, QuranDatabase db) {
    return new Media3AudioPlayerManager(context, playerProvider, cacheEvictor, reciters, settings, sources, quran, db);
  }
}
