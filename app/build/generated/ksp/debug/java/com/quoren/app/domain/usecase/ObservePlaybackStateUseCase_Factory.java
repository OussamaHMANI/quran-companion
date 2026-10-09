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
public final class ObservePlaybackStateUseCase_Factory implements Factory<ObservePlaybackStateUseCase> {
  private final Provider<AudioPlayerManager> playerProvider;

  public ObservePlaybackStateUseCase_Factory(Provider<AudioPlayerManager> playerProvider) {
    this.playerProvider = playerProvider;
  }

  @Override
  public ObservePlaybackStateUseCase get() {
    return newInstance(playerProvider.get());
  }

  public static ObservePlaybackStateUseCase_Factory create(
      Provider<AudioPlayerManager> playerProvider) {
    return new ObservePlaybackStateUseCase_Factory(playerProvider);
  }

  public static ObservePlaybackStateUseCase newInstance(AudioPlayerManager player) {
    return new ObservePlaybackStateUseCase(player);
  }
}
