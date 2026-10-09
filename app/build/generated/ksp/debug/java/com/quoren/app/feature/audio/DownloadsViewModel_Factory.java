package com.quoren.app.feature.audio;

import com.quoren.app.domain.usecase.GetRecitersUseCase;
import com.quoren.app.domain.usecase.GetSurahsUseCase;
import com.quoren.app.domain.usecase.ManageDownloadsUseCase;
import com.quoren.app.domain.usecase.ObserveDefaultReciterUseCase;
import com.quoren.app.domain.usecase.ObserveDownloadsUseCase;
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
public final class DownloadsViewModel_Factory implements Factory<DownloadsViewModel> {
  private final Provider<ObserveDownloadsUseCase> observeDownloadsProvider;

  private final Provider<ObserveDefaultReciterUseCase> observeDefaultReciterProvider;

  private final Provider<GetSurahsUseCase> getSurahsProvider;

  private final Provider<GetRecitersUseCase> getRecitersProvider;

  private final Provider<ManageDownloadsUseCase> manageProvider;

  public DownloadsViewModel_Factory(Provider<ObserveDownloadsUseCase> observeDownloadsProvider,
      Provider<ObserveDefaultReciterUseCase> observeDefaultReciterProvider,
      Provider<GetSurahsUseCase> getSurahsProvider,
      Provider<GetRecitersUseCase> getRecitersProvider,
      Provider<ManageDownloadsUseCase> manageProvider) {
    this.observeDownloadsProvider = observeDownloadsProvider;
    this.observeDefaultReciterProvider = observeDefaultReciterProvider;
    this.getSurahsProvider = getSurahsProvider;
    this.getRecitersProvider = getRecitersProvider;
    this.manageProvider = manageProvider;
  }

  @Override
  public DownloadsViewModel get() {
    return newInstance(observeDownloadsProvider.get(), observeDefaultReciterProvider.get(), getSurahsProvider.get(), getRecitersProvider.get(), manageProvider.get());
  }

  public static DownloadsViewModel_Factory create(
      Provider<ObserveDownloadsUseCase> observeDownloadsProvider,
      Provider<ObserveDefaultReciterUseCase> observeDefaultReciterProvider,
      Provider<GetSurahsUseCase> getSurahsProvider,
      Provider<GetRecitersUseCase> getRecitersProvider,
      Provider<ManageDownloadsUseCase> manageProvider) {
    return new DownloadsViewModel_Factory(observeDownloadsProvider, observeDefaultReciterProvider, getSurahsProvider, getRecitersProvider, manageProvider);
  }

  public static DownloadsViewModel newInstance(ObserveDownloadsUseCase observeDownloads,
      ObserveDefaultReciterUseCase observeDefaultReciter, GetSurahsUseCase getSurahs,
      GetRecitersUseCase getReciters, ManageDownloadsUseCase manage) {
    return new DownloadsViewModel(observeDownloads, observeDefaultReciter, getSurahs, getReciters, manage);
  }
}
