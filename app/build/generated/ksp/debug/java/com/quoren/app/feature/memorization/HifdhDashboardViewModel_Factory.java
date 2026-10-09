package com.quoren.app.feature.memorization;

import com.quoren.app.domain.usecase.CreateMemorizationSessionUseCase;
import com.quoren.app.domain.usecase.GenerateDueReviewsUseCase;
import com.quoren.app.domain.usecase.GetAyahUseCase;
import com.quoren.app.domain.usecase.GetRecitersUseCase;
import com.quoren.app.domain.usecase.GetSurahsUseCase;
import com.quoren.app.domain.usecase.ObserveDailyGoalUseCase;
import com.quoren.app.domain.usecase.ObserveDefaultReciterUseCase;
import com.quoren.app.domain.usecase.ObserveMemorizationCountsUseCase;
import com.quoren.app.domain.usecase.ObserveMemorizationProgressUseCase;
import com.quoren.app.domain.usecase.ObserveMemorizationSessionsUseCase;
import com.quoren.app.domain.usecase.UpdateDailyGoalUseCase;
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
public final class HifdhDashboardViewModel_Factory implements Factory<HifdhDashboardViewModel> {
  private final Provider<ObserveMemorizationSessionsUseCase> observeSessionsProvider;

  private final Provider<ObserveMemorizationProgressUseCase> observeProgressProvider;

  private final Provider<ObserveMemorizationCountsUseCase> observeCountsProvider;

  private final Provider<ObserveDailyGoalUseCase> observeGoalProvider;

  private final Provider<GetSurahsUseCase> getSurahsProvider;

  private final Provider<GetRecitersUseCase> getRecitersProvider;

  private final Provider<ObserveDefaultReciterUseCase> defaultReciterProvider;

  private final Provider<CreateMemorizationSessionUseCase> createSessionProvider;

  private final Provider<GenerateDueReviewsUseCase> generateDueProvider;

  private final Provider<GetAyahUseCase> getAyahProvider;

  private final Provider<UpdateDailyGoalUseCase> updateGoalProvider;

  public HifdhDashboardViewModel_Factory(
      Provider<ObserveMemorizationSessionsUseCase> observeSessionsProvider,
      Provider<ObserveMemorizationProgressUseCase> observeProgressProvider,
      Provider<ObserveMemorizationCountsUseCase> observeCountsProvider,
      Provider<ObserveDailyGoalUseCase> observeGoalProvider,
      Provider<GetSurahsUseCase> getSurahsProvider,
      Provider<GetRecitersUseCase> getRecitersProvider,
      Provider<ObserveDefaultReciterUseCase> defaultReciterProvider,
      Provider<CreateMemorizationSessionUseCase> createSessionProvider,
      Provider<GenerateDueReviewsUseCase> generateDueProvider,
      Provider<GetAyahUseCase> getAyahProvider,
      Provider<UpdateDailyGoalUseCase> updateGoalProvider) {
    this.observeSessionsProvider = observeSessionsProvider;
    this.observeProgressProvider = observeProgressProvider;
    this.observeCountsProvider = observeCountsProvider;
    this.observeGoalProvider = observeGoalProvider;
    this.getSurahsProvider = getSurahsProvider;
    this.getRecitersProvider = getRecitersProvider;
    this.defaultReciterProvider = defaultReciterProvider;
    this.createSessionProvider = createSessionProvider;
    this.generateDueProvider = generateDueProvider;
    this.getAyahProvider = getAyahProvider;
    this.updateGoalProvider = updateGoalProvider;
  }

  @Override
  public HifdhDashboardViewModel get() {
    return newInstance(observeSessionsProvider.get(), observeProgressProvider.get(), observeCountsProvider.get(), observeGoalProvider.get(), getSurahsProvider.get(), getRecitersProvider.get(), defaultReciterProvider.get(), createSessionProvider.get(), generateDueProvider.get(), getAyahProvider.get(), updateGoalProvider.get());
  }

  public static HifdhDashboardViewModel_Factory create(
      Provider<ObserveMemorizationSessionsUseCase> observeSessionsProvider,
      Provider<ObserveMemorizationProgressUseCase> observeProgressProvider,
      Provider<ObserveMemorizationCountsUseCase> observeCountsProvider,
      Provider<ObserveDailyGoalUseCase> observeGoalProvider,
      Provider<GetSurahsUseCase> getSurahsProvider,
      Provider<GetRecitersUseCase> getRecitersProvider,
      Provider<ObserveDefaultReciterUseCase> defaultReciterProvider,
      Provider<CreateMemorizationSessionUseCase> createSessionProvider,
      Provider<GenerateDueReviewsUseCase> generateDueProvider,
      Provider<GetAyahUseCase> getAyahProvider,
      Provider<UpdateDailyGoalUseCase> updateGoalProvider) {
    return new HifdhDashboardViewModel_Factory(observeSessionsProvider, observeProgressProvider, observeCountsProvider, observeGoalProvider, getSurahsProvider, getRecitersProvider, defaultReciterProvider, createSessionProvider, generateDueProvider, getAyahProvider, updateGoalProvider);
  }

  public static HifdhDashboardViewModel newInstance(
      ObserveMemorizationSessionsUseCase observeSessions,
      ObserveMemorizationProgressUseCase observeProgress,
      ObserveMemorizationCountsUseCase observeCounts, ObserveDailyGoalUseCase observeGoal,
      GetSurahsUseCase getSurahs, GetRecitersUseCase getReciters,
      ObserveDefaultReciterUseCase defaultReciter, CreateMemorizationSessionUseCase createSession,
      GenerateDueReviewsUseCase generateDue, GetAyahUseCase getAyah,
      UpdateDailyGoalUseCase updateGoal) {
    return new HifdhDashboardViewModel(observeSessions, observeProgress, observeCounts, observeGoal, getSurahs, getReciters, defaultReciter, createSession, generateDue, getAyah, updateGoal);
  }
}
