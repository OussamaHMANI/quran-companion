package com.quoren.app.domain.audio;

import com.quoren.app.domain.repository.AudioPlayerManager;
import com.quoren.app.domain.repository.QuranRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
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
public final class AyahSynchronizationEngine_Factory implements Factory<AyahSynchronizationEngine> {
  private final Provider<AudioPlayerManager> playerProvider;

  private final Provider<QuranRepository> quranProvider;

  public AyahSynchronizationEngine_Factory(Provider<AudioPlayerManager> playerProvider,
      Provider<QuranRepository> quranProvider) {
    this.playerProvider = playerProvider;
    this.quranProvider = quranProvider;
  }

  @Override
  public AyahSynchronizationEngine get() {
    return newInstance(playerProvider.get(), quranProvider.get());
  }

  public static AyahSynchronizationEngine_Factory create(
      Provider<AudioPlayerManager> playerProvider, Provider<QuranRepository> quranProvider) {
    return new AyahSynchronizationEngine_Factory(playerProvider, quranProvider);
  }

  public static AyahSynchronizationEngine newInstance(AudioPlayerManager player,
      QuranRepository quran) {
    return new AyahSynchronizationEngine(player, quran);
  }
}
