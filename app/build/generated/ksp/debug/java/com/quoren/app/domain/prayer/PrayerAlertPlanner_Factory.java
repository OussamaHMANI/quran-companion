package com.quoren.app.domain.prayer;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class PrayerAlertPlanner_Factory implements Factory<PrayerAlertPlanner> {
  private final Provider<PrayerTimeEngine> engineProvider;

  public PrayerAlertPlanner_Factory(Provider<PrayerTimeEngine> engineProvider) {
    this.engineProvider = engineProvider;
  }

  @Override
  public PrayerAlertPlanner get() {
    return newInstance(engineProvider.get());
  }

  public static PrayerAlertPlanner_Factory create(Provider<PrayerTimeEngine> engineProvider) {
    return new PrayerAlertPlanner_Factory(engineProvider);
  }

  public static PrayerAlertPlanner newInstance(PrayerTimeEngine engine) {
    return new PrayerAlertPlanner(engine);
  }
}
