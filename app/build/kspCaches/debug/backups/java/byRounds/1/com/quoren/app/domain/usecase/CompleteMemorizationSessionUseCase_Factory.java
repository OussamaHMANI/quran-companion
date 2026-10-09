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
public final class CompleteMemorizationSessionUseCase_Factory implements Factory<CompleteMemorizationSessionUseCase> {
  private final Provider<MemorizationRepository> repositoryProvider;

  public CompleteMemorizationSessionUseCase_Factory(
      Provider<MemorizationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public CompleteMemorizationSessionUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static CompleteMemorizationSessionUseCase_Factory create(
      Provider<MemorizationRepository> repositoryProvider) {
    return new CompleteMemorizationSessionUseCase_Factory(repositoryProvider);
  }

  public static CompleteMemorizationSessionUseCase newInstance(MemorizationRepository repository) {
    return new CompleteMemorizationSessionUseCase(repository);
  }
}
