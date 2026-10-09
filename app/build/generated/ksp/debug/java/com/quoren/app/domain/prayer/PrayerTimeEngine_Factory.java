package com.quoren.app.domain.prayer;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class PrayerTimeEngine_Factory implements Factory<PrayerTimeEngine> {
  @Override
  public PrayerTimeEngine get() {
    return newInstance();
  }

  public static PrayerTimeEngine_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static PrayerTimeEngine newInstance() {
    return new PrayerTimeEngine();
  }

  private static final class InstanceHolder {
    private static final PrayerTimeEngine_Factory INSTANCE = new PrayerTimeEngine_Factory();
  }
}
