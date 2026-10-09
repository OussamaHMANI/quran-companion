package com.quoren.app.domain.usecase;

import com.quoren.app.domain.repository.PrayerCitySearchRepository;
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
public final class SearchPrayerCitiesUseCase_Factory implements Factory<SearchPrayerCitiesUseCase> {
  private final Provider<PrayerCitySearchRepository> citiesProvider;

  public SearchPrayerCitiesUseCase_Factory(Provider<PrayerCitySearchRepository> citiesProvider) {
    this.citiesProvider = citiesProvider;
  }

  @Override
  public SearchPrayerCitiesUseCase get() {
    return newInstance(citiesProvider.get());
  }

  public static SearchPrayerCitiesUseCase_Factory create(
      Provider<PrayerCitySearchRepository> citiesProvider) {
    return new SearchPrayerCitiesUseCase_Factory(citiesProvider);
  }

  public static SearchPrayerCitiesUseCase newInstance(PrayerCitySearchRepository cities) {
    return new SearchPrayerCitiesUseCase(cities);
  }
}
