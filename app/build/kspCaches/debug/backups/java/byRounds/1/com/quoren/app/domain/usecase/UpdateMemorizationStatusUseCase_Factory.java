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
public final class UpdateMemorizationStatusUseCase_Factory implements Factory<UpdateMemorizationStatusUseCase> {
  private final Provider<MemorizationRepository> repositoryProvider;

  public UpdateMemorizationStatusUseCase_Factory(
      Provider<MemorizationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UpdateMemorizationStatusUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static UpdateMemorizationStatusUseCase_Factory create(
      Provider<MemorizationRepository> repositoryProvider) {
    return new UpdateMemorizationStatusUseCase_Factory(repositoryProvider);
  }

  public static UpdateMemorizationStatusUseCase newInstance(MemorizationRepository repository) {
    return new UpdateMemorizationStatusUseCase(repository);
  }
}
