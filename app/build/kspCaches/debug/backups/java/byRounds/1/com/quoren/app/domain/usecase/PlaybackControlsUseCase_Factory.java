package com.quoren.app.domain.usecase;

import com.quoren.app.domain.repository.AudioPlayerManager;
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
public final class PlaybackControlsUseCase_Factory implements Factory<PlaybackControlsUseCase> {
  private final Provider<AudioPlayerManager> playerProvider;

  public PlaybackControlsUseCase_Factory(Provider<AudioPlayerManager> playerProvider) {
    this.playerProvider = playerProvider;
  }

  @Override
  public PlaybackControlsUseCase get() {
    return newInstance(playerProvider.get());
  }

  public static PlaybackControlsUseCase_Factory create(
      Provider<AudioPlayerManager> playerProvider) {
    return new PlaybackControlsUseCase_Factory(playerProvider);
  }

  public static PlaybackControlsUseCase newInstance(AudioPlayerManager player) {
    return new PlaybackControlsUseCase(player);
  }
}
