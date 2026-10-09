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
public final class ObserveReviewHistoryUseCase_Factory implements Factory<ObserveReviewHistoryUseCase> {
  private final Provider<MemorizationRepository> repositoryProvider;

  public ObserveReviewHistoryUseCase_Factory(Provider<MemorizationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ObserveReviewHistoryUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ObserveReviewHistoryUseCase_Factory create(
      Provider<MemorizationRepository> repositoryProvider) {
    return new ObserveReviewHistoryUseCase_Factory(repositoryProvider);
  }

  public static ObserveReviewHistoryUseCase newInstance(MemorizationRepository repository) {
    return new ObserveReviewHistoryUseCase(repository);
  }
}
