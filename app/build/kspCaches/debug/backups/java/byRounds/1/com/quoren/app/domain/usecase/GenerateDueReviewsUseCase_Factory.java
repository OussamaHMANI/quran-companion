package com.quoren.app.domain.usecase;

import com.quoren.app.domain.memorization.SpacedRepetitionEngine;
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
public final class GenerateDueReviewsUseCase_Factory implements Factory<GenerateDueReviewsUseCase> {
  private final Provider<MemorizationRepository> repositoryProvider;

  private final Provider<SpacedRepetitionEngine> engineProvider;

  public GenerateDueReviewsUseCase_Factory(Provider<MemorizationRepository> repositoryProvider,
      Provider<SpacedRepetitionEngine> engineProvider) {
    this.repositoryProvider = repositoryProvider;
    this.engineProvider = engineProvider;
  }

  @Override
  public GenerateDueReviewsUseCase get() {
    return newInstance(repositoryProvider.get(), engineProvider.get());
  }

  public static GenerateDueReviewsUseCase_Factory create(
      Provider<MemorizationRepository> repositoryProvider,
      Provider<SpacedRepetitionEngine> engineProvider) {
    return new GenerateDueReviewsUseCase_Factory(repositoryProvider, engineProvider);
  }

  public static GenerateDueReviewsUseCase newInstance(MemorizationRepository repository,
      SpacedRepetitionEngine engine) {
    return new GenerateDueReviewsUseCase(repository, engine);
  }
}
