package com.quoren.app;

import com.quoren.app.domain.usecase.ObservePlaybackStateUseCase;
import com.quoren.app.domain.usecase.ObserveReaderSettingsUseCase;
import com.quoren.app.domain.usecase.UpdateReaderSettingsUseCase;
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
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<ObserveReaderSettingsUseCase> observeSettingsProvider;

  private final Provider<ObservePlaybackStateUseCase> observePlaybackProvider;

  private final Provider<UpdateReaderSettingsUseCase> updateSettingsProvider;

  public MainViewModel_Factory(Provider<ObserveReaderSettingsUseCase> observeSettingsProvider,
      Provider<ObservePlaybackStateUseCase> observePlaybackProvider,
      Provider<UpdateReaderSettingsUseCase> updateSettingsProvider) {
    this.observeSettingsProvider = observeSettingsProvider;
    this.observePlaybackProvider = observePlaybackProvider;
    this.updateSettingsProvider = updateSettingsProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(observeSettingsProvider.get(), observePlaybackProvider.get(), updateSettingsProvider.get());
  }

  public static MainViewModel_Factory create(
      Provider<ObserveReaderSettingsUseCase> observeSettingsProvider,
      Provider<ObservePlaybackStateUseCase> observePlaybackProvider,
      Provider<UpdateReaderSettingsUseCase> updateSettingsProvider) {
    return new MainViewModel_Factory(observeSettingsProvider, observePlaybackProvider, updateSettingsProvider);
  }

  public static MainViewModel newInstance(ObserveReaderSettingsUseCase observeSettings,
      ObservePlaybackStateUseCase observePlayback, UpdateReaderSettingsUseCase updateSettings) {
    return new MainViewModel(observeSettings, observePlayback, updateSettings);
  }
}
