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
public final class UpdatePrayerSettingsUseCase_Factory implements Factory<UpdatePrayerSettingsUseCase> {
  private final Provider<PrayerRepository> prayersProvider;

  public UpdatePrayerSettingsUseCase_Factory(Provider<PrayerRepository> prayersProvider) {
    this.prayersProvider = prayersProvider;
  }

  @Override
  public UpdatePrayerSettingsUseCase get() {
    return newInstance(prayersProvider.get());
  }

  public static UpdatePrayerSettingsUseCase_Factory create(
      Provider<PrayerRepository> prayersProvider) {
    return new UpdatePrayerSettingsUseCase_Factory(prayersProvider);
  }

  public static UpdatePrayerSettingsUseCase newInstance(PrayerRepository prayers) {
    return new UpdatePrayerSettingsUseCase(prayers);
  }
}
