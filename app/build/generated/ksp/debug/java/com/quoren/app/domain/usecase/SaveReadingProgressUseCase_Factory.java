package com.quoren.app.domain.usecase;

import com.quoren.app.domain.repository.ReadingProgressRepository;
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
public final class SaveReadingProgressUseCase_Factory implements Factory<SaveReadingProgressUseCase> {
  private final Provider<ReadingProgressRepository> progressProvider;

  public SaveReadingProgressUseCase_Factory(Provider<ReadingProgressRepository> progressProvider) {
    this.progressProvider = progressProvider;
  }

  @Override
  public SaveReadingProgressUseCase get() {
    return newInstance(progressProvider.get());
  }

  public static SaveReadingProgressUseCase_Factory create(
      Provider<ReadingProgressRepository> progressProvider) {
    return new SaveReadingProgressUseCase_Factory(progressProvider);
  }

  public static SaveReadingProgressUseCase newInstance(ReadingProgressRepository progress) {
    return new SaveReadingProgressUseCase(progress);
  }
}
