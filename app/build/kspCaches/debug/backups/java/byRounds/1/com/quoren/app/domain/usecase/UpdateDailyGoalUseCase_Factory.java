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
public final class UpdateDailyGoalUseCase_Factory implements Factory<UpdateDailyGoalUseCase> {
  private final Provider<MemorizationRepository> repositoryProvider;

  public UpdateDailyGoalUseCase_Factory(Provider<MemorizationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UpdateDailyGoalUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static UpdateDailyGoalUseCase_Factory create(
      Provider<MemorizationRepository> repositoryProvider) {
    return new UpdateDailyGoalUseCase_Factory(repositoryProvider);
  }

  public static UpdateDailyGoalUseCase newInstance(MemorizationRepository repository) {
    return new UpdateDailyGoalUseCase(repository);
  }
}
