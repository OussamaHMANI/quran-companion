package com.quoren.app.feature.prayer;

import com.quoren.app.domain.prayer.PrayerTimeEngine;
import com.quoren.app.domain.usecase.GetCurrentDeviceLocationUseCase;
import com.quoren.app.domain.usecase.ObservePrayerConfigurationUseCase;
import com.quoren.app.domain.usecase.SavePrayerLocationUseCase;
import com.quoren.app.domain.usecase.SchedulePrayerNotificationsUseCase;
import com.quoren.app.domain.usecase.SearchPrayerCitiesUseCase;
import com.quoren.app.domain.usecase.UpdatePrayerSettingsUseCase;
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
public final class PrayerViewModel_Factory implements Factory<PrayerViewModel> {
  private final Provider<ObservePrayerConfigurationUseCase> observeConfigurationProvider;

  private final Provider<SavePrayerLocationUseCase> saveLocationProvider;

  private final Provider<UpdatePrayerSettingsUseCase> updateSettingsProvider;

  private final Provider<GetCurrentDeviceLocationUseCase> getCurrentLocationProvider;

  private final Provider<SchedulePrayerNotificationsUseCase> scheduleNotificationsProvider;

  private final Provider<SearchPrayerCitiesUseCase> searchPrayerCitiesProvider;

  private final Provider<PrayerTimeEngine> engineProvider;

  public PrayerViewModel_Factory(
      Provider<ObservePrayerConfigurationUseCase> observeConfigurationProvider,
      Provider<SavePrayerLocationUseCase> saveLocationProvider,
      Provider<UpdatePrayerSettingsUseCase> updateSettingsProvider,
      Provider<GetCurrentDeviceLocationUseCase> getCurrentLocationProvider,
      Provider<SchedulePrayerNotificationsUseCase> scheduleNotificationsProvider,
      Provider<SearchPrayerCitiesUseCase> searchPrayerCitiesProvider,
      Provider<PrayerTimeEngine> engineProvider) {
    this.observeConfigurationProvider = observeConfigurationProvider;
    this.saveLocationProvider = saveLocationProvider;
    this.updateSettingsProvider = updateSettingsProvider;
    this.getCurrentLocationProvider = getCurrentLocationProvider;
    this.scheduleNotificationsProvider = scheduleNotificationsProvider;
    this.searchPrayerCitiesProvider = searchPrayerCitiesProvider;
    this.engineProvider = engineProvider;
  }

  @Override
  public PrayerViewModel get() {
    return newInstance(observeConfigurationProvider.get(), saveLocationProvider.get(), updateSettingsProvider.get(), getCurrentLocationProvider.get(), scheduleNotificationsProvider.get(), searchPrayerCitiesProvider.get(), engineProvider.get());
  }

  public static PrayerViewModel_Factory create(
      Provider<ObservePrayerConfigurationUseCase> observeConfigurationProvider,
      Provider<SavePrayerLocationUseCase> saveLocationProvider,
      Provider<UpdatePrayerSettingsUseCase> updateSettingsProvider,
      Provider<GetCurrentDeviceLocationUseCase> getCurrentLocationProvider,
      Provider<SchedulePrayerNotificationsUseCase> scheduleNotificationsProvider,
      Provider<SearchPrayerCitiesUseCase> searchPrayerCitiesProvider,
      Provider<PrayerTimeEngine> engineProvider) {
    return new PrayerViewModel_Factory(observeConfigurationProvider, saveLocationProvider, updateSettingsProvider, getCurrentLocationProvider, scheduleNotificationsProvider, searchPrayerCitiesProvider, engineProvider);
  }

  public static PrayerViewModel newInstance(ObservePrayerConfigurationUseCase observeConfiguration,
      SavePrayerLocationUseCase saveLocation, UpdatePrayerSettingsUseCase updateSettings,
      GetCurrentDeviceLocationUseCase getCurrentLocation,
      SchedulePrayerNotificationsUseCase scheduleNotifications,
      SearchPrayerCitiesUseCase searchPrayerCities, PrayerTimeEngine engine) {
    return new PrayerViewModel(observeConfiguration, saveLocation, updateSettings, getCurrentLocation, scheduleNotifications, searchPrayerCities, engine);
  }
}
