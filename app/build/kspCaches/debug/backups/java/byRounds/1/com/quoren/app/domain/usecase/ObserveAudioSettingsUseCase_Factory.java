package com.quoren.app.domain.usecase;

import com.quoren.app.domain.repository.AudioSettingsRepository;
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
public final class ObserveAudioSettingsUseCase_Factory implements Factory<ObserveAudioSettingsUseCase> {
  private final Provider<AudioSettingsRepository> settingsProvider;

  public ObserveAudioSettingsUseCase_Factory(Provider<AudioSettingsRepository> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public ObserveAudioSettingsUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static ObserveAudioSettingsUseCase_Factory create(
      Provider<AudioSettingsRepository> settingsProvider) {
    return new ObserveAudioSettingsUseCase_Factory(settingsProvider);
  }

  public static ObserveAudioSettingsUseCase newInstance(AudioSettingsRepository settings) {
    return new ObserveAudioSettingsUseCase(settings);
  }
}
