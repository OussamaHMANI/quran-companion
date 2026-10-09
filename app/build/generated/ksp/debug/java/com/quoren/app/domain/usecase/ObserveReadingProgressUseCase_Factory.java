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
public final class ObserveReadingProgressUseCase_Factory implements Factory<ObserveReadingProgressUseCase> {
  private final Provider<ReadingProgressRepository> progressProvider;

  public ObserveReadingProgressUseCase_Factory(
      Provider<ReadingProgressRepository> progressProvider) {
    this.progressProvider = progressProvider;
  }

  @Override
  public ObserveReadingProgressUseCase get() {
    return newInstance(progressProvider.get());
  }

  public static ObserveReadingProgressUseCase_Factory create(
      Provider<ReadingProgressRepository> progressProvider) {
    return new ObserveReadingProgressUseCase_Factory(progressProvider);
  }

  public static ObserveReadingProgressUseCase newInstance(ReadingProgressRepository progress) {
    return new ObserveReadingProgressUseCase(progress);
  }
}
