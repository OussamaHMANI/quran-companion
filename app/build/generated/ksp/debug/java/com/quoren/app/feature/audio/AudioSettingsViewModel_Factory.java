package com.quoren.app.feature.audio;

import com.quoren.app.domain.usecase.AudioCacheUseCase;
import com.quoren.app.domain.usecase.GetRecitersUseCase;
import com.quoren.app.domain.usecase.ObserveAudioSettingsUseCase;
import com.quoren.app.domain.usecase.ObservePlaybackStateUseCase;
import com.quoren.app.domain.usecase.PlayAudioUseCase;
import com.quoren.app.domain.usecase.PlaybackControlsUseCase;
import com.quoren.app.domain.usecase.SetDefaultReciterUseCase;
import com.quoren.app.domain.usecase.UpdateAudioSettingsUseCase;
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
public final class AudioSettingsViewModel_Factory implements Factory<AudioSettingsViewModel> {
  private final Provider<ObserveAudioSettingsUseCase> observeSettingsProvider;

  private final Provider<GetRecitersUseCase> getRecitersProvider;

  private final Provider<SetDefaultReciterUseCase> setDefaultReciterProvider;

  private final Provider<UpdateAudioSettingsUseCase> updateSettingsProvider;

  private final Provider<AudioCacheUseCase> cacheProvider;

  private final Provider<ObservePlaybackStateUseCase> observePlaybackProvider;

  private final Provider<PlayAudioUseCase> playAudioProvider;

  private final Provider<PlaybackControlsUseCase> playbackControlsProvider;

  public AudioSettingsViewModel_Factory(
      Provider<ObserveAudioSettingsUseCase> observeSettingsProvider,
      Provider<GetRecitersUseCase> getRecitersProvider,
      Provider<SetDefaultReciterUseCase> setDefaultReciterProvider,
      Provider<UpdateAudioSettingsUseCase> updateSettingsProvider,
      Provider<AudioCacheUseCase> cacheProvider,
      Provider<ObservePlaybackStateUseCase> observePlaybackProvider,
      Provider<PlayAudioUseCase> playAudioProvider,
      Provider<PlaybackControlsUseCase> playbackControlsProvider) {
    this.observeSettingsProvider = observeSettingsProvider;
    this.getRecitersProvider = getRecitersProvider;
    this.setDefaultReciterProvider = setDefaultReciterProvider;
    this.updateSettingsProvider = updateSettingsProvider;
    this.cacheProvider = cacheProvider;
    this.observePlaybackProvider = observePlaybackProvider;
    this.playAudioProvider = playAudioProvider;
    this.playbackControlsProvider = playbackControlsProvider;
  }

  @Override
  public AudioSettingsViewModel get() {
    return newInstance(observeSettingsProvider.get(), getRecitersProvider.get(), setDefaultReciterProvider.get(), updateSettingsProvider.get(), cacheProvider.get(), observePlaybackProvider.get(), playAudioProvider.get(), playbackControlsProvider.get());
  }

  public static AudioSettingsViewModel_Factory create(
      Provider<ObserveAudioSettingsUseCase> observeSettingsProvider,
      Provider<GetRecitersUseCase> getRecitersProvider,
      Provider<SetDefaultReciterUseCase> setDefaultReciterProvider,
      Provider<UpdateAudioSettingsUseCase> updateSettingsProvider,
      Provider<AudioCacheUseCase> cacheProvider,
      Provider<ObservePlaybackStateUseCase> observePlaybackProvider,
      Provider<PlayAudioUseCase> playAudioProvider,
      Provider<PlaybackControlsUseCase> playbackControlsProvider) {
    return new AudioSettingsViewModel_Factory(observeSettingsProvider, getRecitersProvider, setDefaultReciterProvider, updateSettingsProvider, cacheProvider, observePlaybackProvider, playAudioProvider, playbackControlsProvider);
  }

  public static AudioSettingsViewModel newInstance(ObserveAudioSettingsUseCase observeSettings,
      GetRecitersUseCase getReciters, SetDefaultReciterUseCase setDefaultReciter,
      UpdateAudioSettingsUseCase updateSettings, AudioCacheUseCase cache,
      ObservePlaybackStateUseCase observePlayback, PlayAudioUseCase playAudio,
      PlaybackControlsUseCase playbackControls) {
    return new AudioSettingsViewModel(observeSettings, getReciters, setDefaultReciter, updateSettings, cache, observePlayback, playAudio, playbackControls);
  }
}
