package com.quoren.app.feature.mosque;

import com.quoren.app.domain.usecase.GetCurrentDeviceLocationUseCase;
import com.quoren.app.domain.usecase.ObserveFavoriteMosquesUseCase;
import com.quoren.app.domain.usecase.ObserveLastMosqueSearchUseCase;
import com.quoren.app.domain.usecase.ObservePrayerConfigurationUseCase;
import com.quoren.app.domain.usecase.SearchNearbyMosquesUseCase;
import com.quoren.app.domain.usecase.SearchPrayerCitiesUseCase;
import com.quoren.app.domain.usecase.ToggleMosqueFavoriteUseCase;
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
public final class MosqueViewModel_Factory implements Factory<MosqueViewModel> {
  private final Provider<ObservePrayerConfigurationUseCase> observePrayerConfigurationProvider;

  private final Provider<GetCurrentDeviceLocationUseCase> getCurrentLocationProvider;

  private final Provider<SearchPrayerCitiesUseCase> searchCitiesProvider;

  private final Provider<SearchNearbyMosquesUseCase> searchNearbyMosquesProvider;

  private final Provider<ObserveFavoriteMosquesUseCase> observeFavoritesProvider;

  private final Provider<ObserveLastMosqueSearchUseCase> observeLastSearchProvider;

  private final Provider<ToggleMosqueFavoriteUseCase> toggleFavoriteProvider;

  public MosqueViewModel_Factory(
      Provider<ObservePrayerConfigurationUseCase> observePrayerConfigurationProvider,
      Provider<GetCurrentDeviceLocationUseCase> getCurrentLocationProvider,
      Provider<SearchPrayerCitiesUseCase> searchCitiesProvider,
      Provider<SearchNearbyMosquesUseCase> searchNearbyMosquesProvider,
      Provider<ObserveFavoriteMosquesUseCase> observeFavoritesProvider,
      Provider<ObserveLastMosqueSearchUseCase> observeLastSearchProvider,
      Provider<ToggleMosqueFavoriteUseCase> toggleFavoriteProvider) {
    this.observePrayerConfigurationProvider = observePrayerConfigurationProvider;
    this.getCurrentLocationProvider = getCurrentLocationProvider;
    this.searchCitiesProvider = searchCitiesProvider;
    this.searchNearbyMosquesProvider = searchNearbyMosquesProvider;
    this.observeFavoritesProvider = observeFavoritesProvider;
    this.observeLastSearchProvider = observeLastSearchProvider;
    this.toggleFavoriteProvider = toggleFavoriteProvider;
  }

  @Override
  public MosqueViewModel get() {
    return newInstance(observePrayerConfigurationProvider.get(), getCurrentLocationProvider.get(), searchCitiesProvider.get(), searchNearbyMosquesProvider.get(), observeFavoritesProvider.get(), observeLastSearchProvider.get(), toggleFavoriteProvider.get());
  }

  public static MosqueViewModel_Factory create(
      Provider<ObservePrayerConfigurationUseCase> observePrayerConfigurationProvider,
      Provider<GetCurrentDeviceLocationUseCase> getCurrentLocationProvider,
      Provider<SearchPrayerCitiesUseCase> searchCitiesProvider,
      Provider<SearchNearbyMosquesUseCase> searchNearbyMosquesProvider,
      Provider<ObserveFavoriteMosquesUseCase> observeFavoritesProvider,
      Provider<ObserveLastMosqueSearchUseCase> observeLastSearchProvider,
      Provider<ToggleMosqueFavoriteUseCase> toggleFavoriteProvider) {
    return new MosqueViewModel_Factory(observePrayerConfigurationProvider, getCurrentLocationProvider, searchCitiesProvider, searchNearbyMosquesProvider, observeFavoritesProvider, observeLastSearchProvider, toggleFavoriteProvider);
  }

  public static MosqueViewModel newInstance(
      ObservePrayerConfigurationUseCase observePrayerConfiguration,
      GetCurrentDeviceLocationUseCase getCurrentLocation, SearchPrayerCitiesUseCase searchCities,
      SearchNearbyMosquesUseCase searchNearbyMosques,
      ObserveFavoriteMosquesUseCase observeFavorites,
      ObserveLastMosqueSearchUseCase observeLastSearch,
      ToggleMosqueFavoriteUseCase toggleFavorite) {
    return new MosqueViewModel(observePrayerConfiguration, getCurrentLocation, searchCities, searchNearbyMosques, observeFavorites, observeLastSearch, toggleFavorite);
  }
}
