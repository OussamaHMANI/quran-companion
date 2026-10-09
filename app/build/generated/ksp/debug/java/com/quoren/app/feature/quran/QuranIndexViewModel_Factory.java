package com.quoren.app.feature.quran;

import com.quoren.app.domain.usecase.GetJuzStartsUseCase;
import com.quoren.app.domain.usecase.GetSurahsUseCase;
import com.quoren.app.domain.usecase.ObserveReadingProgressUseCase;
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
public final class QuranIndexViewModel_Factory implements Factory<QuranIndexViewModel> {
  private final Provider<GetSurahsUseCase> getSurahsProvider;

  private final Provider<GetJuzStartsUseCase> getJuzStartsProvider;

  private final Provider<ObserveReadingProgressUseCase> observeProgressProvider;

  public QuranIndexViewModel_Factory(Provider<GetSurahsUseCase> getSurahsProvider,
      Provider<GetJuzStartsUseCase> getJuzStartsProvider,
      Provider<ObserveReadingProgressUseCase> observeProgressProvider) {
    this.getSurahsProvider = getSurahsProvider;
    this.getJuzStartsProvider = getJuzStartsProvider;
    this.observeProgressProvider = observeProgressProvider;
  }

  @Override
  public QuranIndexViewModel get() {
    return newInstance(getSurahsProvider.get(), getJuzStartsProvider.get(), observeProgressProvider.get());
  }

  public static QuranIndexViewModel_Factory create(Provider<GetSurahsUseCase> getSurahsProvider,
      Provider<GetJuzStartsUseCase> getJuzStartsProvider,
      Provider<ObserveReadingProgressUseCase> observeProgressProvider) {
    return new QuranIndexViewModel_Factory(getSurahsProvider, getJuzStartsProvider, observeProgressProvider);
  }

  public static QuranIndexViewModel newInstance(GetSurahsUseCase getSurahs,
      GetJuzStartsUseCase getJuzStarts, ObserveReadingProgressUseCase observeProgress) {
    return new QuranIndexViewModel(getSurahs, getJuzStarts, observeProgress);
  }
}
