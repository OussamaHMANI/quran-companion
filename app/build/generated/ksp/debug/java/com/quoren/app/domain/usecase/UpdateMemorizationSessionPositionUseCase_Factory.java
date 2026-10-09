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
public final class UpdateMemorizationSessionPositionUseCase_Factory implements Factory<UpdateMemorizationSessionPositionUseCase> {
  private final Provider<MemorizationRepository> repositoryProvider;

  public UpdateMemorizationSessionPositionUseCase_Factory(
      Provider<MemorizationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UpdateMemorizationSessionPositionUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static UpdateMemorizationSessionPositionUseCase_Factory create(
      Provider<MemorizationRepository> repositoryProvider) {
    return new UpdateMemorizationSessionPositionUseCase_Factory(repositoryProvider);
  }

  public static UpdateMemorizationSessionPositionUseCase newInstance(
      MemorizationRepository repository) {
    return new UpdateMemorizationSessionPositionUseCase(repository);
  }
}
