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
public final class ObserveMemorizationCountsUseCase_Factory implements Factory<ObserveMemorizationCountsUseCase> {
  private final Provider<MemorizationRepository> repositoryProvider;

  public ObserveMemorizationCountsUseCase_Factory(
      Provider<MemorizationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ObserveMemorizationCountsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ObserveMemorizationCountsUseCase_Factory create(
      Provider<MemorizationRepository> repositoryProvider) {
    return new ObserveMemorizationCountsUseCase_Factory(repositoryProvider);
  }

  public static ObserveMemorizationCountsUseCase newInstance(MemorizationRepository repository) {
    return new ObserveMemorizationCountsUseCase(repository);
  }
}
