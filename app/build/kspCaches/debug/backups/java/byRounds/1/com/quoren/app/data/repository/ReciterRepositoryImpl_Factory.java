package com.quoren.app.data.repository;

import com.quoren.app.domain.repository.AudioSettingsRepository;
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
public final class ReciterRepositoryImpl_Factory implements Factory<ReciterRepositoryImpl> {
  private final Provider<AudioSettingsRepository> settingsProvider;

  public ReciterRepositoryImpl_Factory(Provider<AudioSettingsRepository> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public ReciterRepositoryImpl get() {
    return newInstance(settingsProvider.get());
  }

  public static ReciterRepositoryImpl_Factory create(
      Provider<AudioSettingsRepository> settingsProvider) {
    return new ReciterRepositoryImpl_Factory(settingsProvider);
  }

  public static ReciterRepositoryImpl newInstance(AudioSettingsRepository settings) {
    return new ReciterRepositoryImpl(settings);
  }
}
