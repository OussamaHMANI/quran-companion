package com.quoren.app.domain.usecase;

import com.quoren.app.domain.repository.PrayerRepository;
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
public final class GetCurrentDeviceLocationUseCase_Factory implements Factory<GetCurrentDeviceLocationUseCase> {
  private final Provider<PrayerRepository> prayersProvider;

  public GetCurrentDeviceLocationUseCase_Factory(Provider<PrayerRepository> prayersProvider) {
    this.prayersProvider = prayersProvider;
  }

  @Override
  public GetCurrentDeviceLocationUseCase get() {
    return newInstance(prayersProvider.get());
  }

  public static GetCurrentDeviceLocationUseCase_Factory create(
      Provider<PrayerRepository> prayersProvider) {
    return new GetCurrentDeviceLocationUseCase_Factory(prayersProvider);
  }

  public static GetCurrentDeviceLocationUseCase newInstance(PrayerRepository prayers) {
    return new GetCurrentDeviceLocationUseCase(prayers);
  }
}
