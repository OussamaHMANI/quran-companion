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
public final class CompleteDailyGoalAyahUseCase_Factory implements Factory<CompleteDailyGoalAyahUseCase> {
  private final Provider<MemorizationRepository> repositoryProvider;

  public CompleteDailyGoalAyahUseCase_Factory(Provider<MemorizationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public CompleteDailyGoalAyahUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static CompleteDailyGoalAyahUseCase_Factory create(
      Provider<MemorizationRepository> repositoryProvider) {
    return new CompleteDailyGoalAyahUseCase_Factory(repositoryProvider);
  }

  public static CompleteDailyGoalAyahUseCase newInstance(MemorizationRepository repository) {
    return new CompleteDailyGoalAyahUseCase(repository);
  }
}
