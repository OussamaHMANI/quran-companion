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
public final class ObserveMemorizationSessionsUseCase_Factory implements Factory<ObserveMemorizationSessionsUseCase> {
  private final Provider<MemorizationRepository> repositoryProvider;

  public ObserveMemorizationSessionsUseCase_Factory(
      Provider<MemorizationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ObserveMemorizationSessionsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ObserveMemorizationSessionsUseCase_Factory create(
      Provider<MemorizationRepository> repositoryProvider) {
    return new ObserveMemorizationSessionsUseCase_Factory(repositoryProvider);
  }

  public static ObserveMemorizationSessionsUseCase newInstance(MemorizationRepository repository) {
    return new ObserveMemorizationSessionsUseCase(repository);
  }
}
