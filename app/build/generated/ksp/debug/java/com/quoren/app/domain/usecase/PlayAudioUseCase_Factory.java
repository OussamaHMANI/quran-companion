package com.quoren.app.domain.usecase;

import com.quoren.app.domain.repository.AudioPlayerManager;
import com.quoren.app.domain.repository.AudioSettingsRepository;
import com.quoren.app.domain.repository.DownloadRepository;
import com.quoren.app.domain.repository.ReciterRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class PlayAudioUseCase_Factory implements Factory<PlayAudioUseCase> {
  private final Provider<AudioPlayerManager> playerProvider;

  private final Provider<AudioSettingsRepository> settingsProvider;

  private final Provider<ReciterRepository> recitersProvider;

  private final Provider<DownloadRepository> downloadsProvider;

  public PlayAudioUseCase_Factory(Provider<AudioPlayerManager> playerProvider,
      Provider<AudioSettingsRepository> settingsProvider,
      Provider<ReciterRepository> recitersProvider,
      Provider<DownloadRepository> downloadsProvider) {
    this.playerProvider = playerProvider;
    this.settingsProvider = settingsProvider;
    this.recitersProvider = recitersProvider;
    this.downloadsProvider = downloadsProvider;
  }

  @Override
  public PlayAudioUseCase get() {
    return newInstance(playerProvider.get(), settingsProvider.get(), recitersProvider.get(), downloadsProvider.get());
  }

  public static PlayAudioUseCase_Factory create(Provider<AudioPlayerManager> playerProvider,
      Provider<AudioSettingsRepository> settingsProvider,
      Provider<ReciterRepository> recitersProvider,
      Provider<DownloadRepository> downloadsProvider) {
    return new PlayAudioUseCase_Factory(playerProvider, settingsProvider, recitersProvider, downloadsProvider);
  }

  public static PlayAudioUseCase newInstance(AudioPlayerManager player,
      AudioSettingsRepository settings, ReciterRepository reciters, DownloadRepository downloads) {
    return new PlayAudioUseCase(player, settings, reciters, downloads);
  }
}
