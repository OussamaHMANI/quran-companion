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
public final class GetMemorizationSessionUseCase_Factory implements Factory<GetMemorizationSessionUseCase> {
  private final Provider<MemorizationRepository> repositoryProvider;

  public GetMemorizationSessionUseCase_Factory(
      Provider<MemorizationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetMemorizationSessionUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetMemorizationSessionUseCase_Factory create(
      Provider<MemorizationRepository> repositoryProvider) {
    return new GetMemorizationSessionUseCase_Factory(repositoryProvider);
  }

  public static GetMemorizationSessionUseCase newInstance(MemorizationRepository repository) {
    return new GetMemorizationSessionUseCase(repository);
  }
}
