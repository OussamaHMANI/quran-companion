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
public final class SavePrayerLocationUseCase_Factory implements Factory<SavePrayerLocationUseCase> {
  private final Provider<PrayerRepository> prayersProvider;

  public SavePrayerLocationUseCase_Factory(Provider<PrayerRepository> prayersProvider) {
    this.prayersProvider = prayersProvider;
  }

  @Override
  public SavePrayerLocationUseCase get() {
    return newInstance(prayersProvider.get());
  }

  public static SavePrayerLocationUseCase_Factory create(
      Provider<PrayerRepository> prayersProvider) {
    return new SavePrayerLocationUseCase_Factory(prayersProvider);
  }

  public static SavePrayerLocationUseCase newInstance(PrayerRepository prayers) {
    return new SavePrayerLocationUseCase(prayers);
  }
}
