package com.quoren.app.domain.usecase;

import com.quoren.app.domain.repository.MemorizationRepository;
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
public final class ObserveAyahMemorizationProgressUseCase_Factory implements Factory<ObserveAyahMemorizationProgressUseCase> {
  private final Provider<MemorizationRepository> repositoryProvider;

  public ObserveAyahMemorizationProgressUseCase_Factory(
      Provider<MemorizationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ObserveAyahMemorizationProgressUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ObserveAyahMemorizationProgressUseCase_Factory create(
      Provider<MemorizationRepository> repositoryProvider) {
    return new ObserveAyahMemorizationProgressUseCase_Factory(repositoryProvider);
  }

  public static ObserveAyahMemorizationProgressUseCase newInstance(
      MemorizationRepository repository) {
    return new ObserveAyahMemorizationProgressUseCase(repository);
  }
}
