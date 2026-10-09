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
public final class ObserveMemorizationProgressUseCase_Factory implements Factory<ObserveMemorizationProgressUseCase> {
  private final Provider<MemorizationRepository> repositoryProvider;

  public ObserveMemorizationProgressUseCase_Factory(
      Provider<MemorizationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ObserveMemorizationProgressUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ObserveMemorizationProgressUseCase_Factory create(
      Provider<MemorizationRepository> repositoryProvider) {
    return new ObserveMemorizationProgressUseCase_Factory(repositoryProvider);
  }

  public static ObserveMemorizationProgressUseCase newInstance(MemorizationRepository repository) {
    return new ObserveMemorizationProgressUseCase(repository);
  }
}
