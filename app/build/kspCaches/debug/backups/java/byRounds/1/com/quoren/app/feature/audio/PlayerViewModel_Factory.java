package com.quoren.app.feature.audio;

import com.quoren.app.domain.usecase.ObservePlaybackPositionUseCase;
import com.quoren.app.domain.usecase.ObservePlaybackStateUseCase;
import com.quoren.app.domain.usecase.PlaybackControlsUseCase;
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
public final class PlayerViewModel_Factory implements Factory<PlayerViewModel> {
  private final Provider<ObservePlaybackStateUseCase> observeStateProvider;

  private final Provider<ObservePlaybackPositionUseCase> observePositionProvider;

  private final Provider<PlaybackControlsUseCase> controlsProvider;

  public PlayerViewModel_Factory(Provider<ObservePlaybackStateUseCase> observeStateProvider,
      Provider<ObservePlaybackPositionUseCase> observePositionProvider,
      Provider<PlaybackControlsUseCase> controlsProvider) {
    this.observeStateProvider = observeStateProvider;
    this.observePositionProvider = observePositionProvider;
    this.controlsProvider = controlsProvider;
  }

  @Override
  public PlayerViewModel get() {
    return newInstance(observeStateProvider.get(), observePositionProvider.get(), controlsProvider.get());
  }

  public static PlayerViewModel_Factory create(
      Provider<ObservePlaybackStateUseCase> observeStateProvider,
      Provider<ObservePlaybackPositionUseCase> observePositionProvider,
      Provider<PlaybackControlsUseCase> controlsProvider) {
    return new PlayerViewModel_Factory(observeStateProvider, observePositionProvider, controlsProvider);
  }

  public static PlayerViewModel newInstance(ObservePlaybackStateUseCase observeState,
      ObservePlaybackPositionUseCase observePosition, PlaybackControlsUseCase controls) {
    return new PlayerViewModel(observeState, observePosition, controls);
  }
}
