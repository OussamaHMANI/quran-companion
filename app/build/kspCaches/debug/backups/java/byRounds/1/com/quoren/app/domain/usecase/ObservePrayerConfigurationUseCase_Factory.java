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
public final class ObservePrayerConfigurationUseCase_Factory implements Factory<ObservePrayerConfigurationUseCase> {
  private final Provider<PrayerRepository> prayersProvider;

  public ObservePrayerConfigurationUseCase_Factory(Provider<PrayerRepository> prayersProvider) {
    this.prayersProvider = prayersProvider;
  }

  @Override
  public ObservePrayerConfigurationUseCase get() {
    return newInstance(prayersProvider.get());
  }

  public static ObservePrayerConfigurationUseCase_Factory create(
      Provider<PrayerRepository> prayersProvider) {
    return new ObservePrayerConfigurationUseCase_Factory(prayersProvider);
  }

  public static ObservePrayerConfigurationUseCase newInstance(PrayerRepository prayers) {
    return new ObservePrayerConfigurationUseCase(prayers);
  }
}
