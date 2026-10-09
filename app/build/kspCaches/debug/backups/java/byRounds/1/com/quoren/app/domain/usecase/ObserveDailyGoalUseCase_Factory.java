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
public final class ObserveDailyGoalUseCase_Factory implements Factory<ObserveDailyGoalUseCase> {
  private final Provider<MemorizationRepository> repositoryProvider;

  public ObserveDailyGoalUseCase_Factory(Provider<MemorizationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ObserveDailyGoalUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ObserveDailyGoalUseCase_Factory create(
      Provider<MemorizationRepository> repositoryProvider) {
    return new ObserveDailyGoalUseCase_Factory(repositoryProvider);
  }

  public static ObserveDailyGoalUseCase newInstance(MemorizationRepository repository) {
    return new ObserveDailyGoalUseCase(repository);
  }
}
