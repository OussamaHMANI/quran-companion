package com.quoren.app.domain.usecase;

import com.quoren.app.domain.repository.AudioCacheManager;
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
public final class UpdateAudioSettingsUseCase_Factory implements Factory<UpdateAudioSettingsUseCase> {
  private final Provider<AudioSettingsRepository> settingsProvider;

  private final Provider<AudioCacheManager> cacheProvider;

  public UpdateAudioSettingsUseCase_Factory(Provider<AudioSettingsRepository> settingsProvider,
      Provider<AudioCacheManager> cacheProvider) {
    this.settingsProvider = settingsProvider;
    this.cacheProvider = cacheProvider;
  }

  @Override
  public UpdateAudioSettingsUseCase get() {
    return newInstance(settingsProvider.get(), cacheProvider.get());
  }

  public static UpdateAudioSettingsUseCase_Factory create(
      Provider<AudioSettingsRepository> settingsProvider,
      Provider<AudioCacheManager> cacheProvider) {
    return new UpdateAudioSettingsUseCase_Factory(settingsProvider, cacheProvider);
  }

  public static UpdateAudioSettingsUseCase newInstance(AudioSettingsRepository settings,
      AudioCacheManager cache) {
    return new UpdateAudioSettingsUseCase(settings, cache);
  }
}
