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
public final class ObservePlaybackPositionUseCase_Factory implements Factory<ObservePlaybackPositionUseCase> {
  private final Provider<AudioPlayerManager> playerProvider;

  public ObservePlaybackPositionUseCase_Factory(Provider<AudioPlayerManager> playerProvider) {
    this.playerProvider = playerProvider;
  }

  @Override
  public ObservePlaybackPositionUseCase get() {
    return newInstance(playerProvider.get());
  }

  public static ObservePlaybackPositionUseCase_Factory create(
      Provider<AudioPlayerManager> playerProvider) {
    return new ObservePlaybackPositionUseCase_Factory(playerProvider);
  }

  public static ObservePlaybackPositionUseCase newInstance(AudioPlayerManager player) {
    return new ObservePlaybackPositionUseCase(player);
  }
}
