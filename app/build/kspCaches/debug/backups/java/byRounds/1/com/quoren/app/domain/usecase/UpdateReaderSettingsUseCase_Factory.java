package com.quoren.app.domain.usecase;

import com.quoren.app.domain.repository.ReaderSettingsRepository;
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
public final class UpdateReaderSettingsUseCase_Factory implements Factory<UpdateReaderSettingsUseCase> {
  private final Provider<ReaderSettingsRepository> settingsProvider;

  public UpdateReaderSettingsUseCase_Factory(Provider<ReaderSettingsRepository> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public UpdateReaderSettingsUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static UpdateReaderSettingsUseCase_Factory create(
      Provider<ReaderSettingsRepository> settingsProvider) {
    return new UpdateReaderSettingsUseCase_Factory(settingsProvider);
  }

  public static UpdateReaderSettingsUseCase newInstance(ReaderSettingsRepository settings) {
    return new UpdateReaderSettingsUseCase(settings);
  }
}
