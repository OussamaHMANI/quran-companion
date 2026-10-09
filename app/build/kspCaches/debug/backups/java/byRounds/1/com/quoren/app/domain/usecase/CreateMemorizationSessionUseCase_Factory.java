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
public final class CreateMemorizationSessionUseCase_Factory implements Factory<CreateMemorizationSessionUseCase> {
  private final Provider<MemorizationRepository> repositoryProvider;

  public CreateMemorizationSessionUseCase_Factory(
      Provider<MemorizationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public CreateMemorizationSessionUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static CreateMemorizationSessionUseCase_Factory create(
      Provider<MemorizationRepository> repositoryProvider) {
    return new CreateMemorizationSessionUseCase_Factory(repositoryProvider);
  }

  public static CreateMemorizationSessionUseCase newInstance(MemorizationRepository repository) {
    return new CreateMemorizationSessionUseCase(repository);
  }
}
