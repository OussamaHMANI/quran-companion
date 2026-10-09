package com.quoren.app.feature.qibla;

import com.quoren.app.domain.qibla.QiblaEngine;
import com.quoren.app.domain.repository.CompassRepository;
import com.quoren.app.domain.usecase.GetCurrentDeviceLocationUseCase;
import com.quoren.app.domain.usecase.ObservePrayerConfigurationUseCase;
import com.quoren.app.domain.usecase.SavePrayerLocationUseCase;
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
public final class QiblaViewModel_Factory implements Factory<QiblaViewModel> {
  private final Provider<ObservePrayerConfigurationUseCase> observeConfigurationProvider;

  private final Provider<SavePrayerLocationUseCase> saveLocationProvider;

  private final Provider<GetCurrentDeviceLocationUseCase> getCurrentLocationProvider;

  private final Provider<CompassRepository> compassProvider;

  private final Provider<QiblaEngine> engineProvider;

  public QiblaViewModel_Factory(
      Provider<ObservePrayerConfigurationUseCase> observeConfigurationProvider,
      Provider<SavePrayerLocationUseCase> saveLocationProvider,
      Provider<GetCurrentDeviceLocationUseCase> getCurrentLocationProvider,
      Provider<CompassRepository> compassProvider, Provider<QiblaEngine> engineProvider) {
    this.observeConfigurationProvider = observeConfigurationProvider;
    this.saveLocationProvider = saveLocationProvider;
    this.getCurrentLocationProvider = getCurrentLocationProvider;
    this.compassProvider = compassProvider;
    this.engineProvider = engineProvider;
  }

  @Override
  public QiblaViewModel get() {
    return newInstance(observeConfigurationProvider.get(), saveLocationProvider.get(), getCurrentLocationProvider.get(), compassProvider.get(), engineProvider.get());
  }

  public static QiblaViewModel_Factory create(
      Provider<ObservePrayerConfigurationUseCase> observeConfigurationProvider,
      Provider<SavePrayerLocationUseCase> saveLocationProvider,
      Provider<GetCurrentDeviceLocationUseCase> getCurrentLocationProvider,
      Provider<CompassRepository> compassProvider, Provider<QiblaEngine> engineProvider) {
    return new QiblaViewModel_Factory(observeConfigurationProvider, saveLocationProvider, getCurrentLocationProvider, compassProvider, engineProvider);
  }

  public static QiblaViewModel newInstance(ObservePrayerConfigurationUseCase observeConfiguration,
      SavePrayerLocationUseCase saveLocation, GetCurrentDeviceLocationUseCase getCurrentLocation,
      CompassRepository compass, QiblaEngine engine) {
    return new QiblaViewModel(observeConfiguration, saveLocation, getCurrentLocation, compass, engine);
  }
}
