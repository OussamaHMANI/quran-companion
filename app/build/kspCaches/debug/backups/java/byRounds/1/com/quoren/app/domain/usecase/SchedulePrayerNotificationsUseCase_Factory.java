package com.quoren.app.domain.usecase;

import com.quoren.app.domain.repository.PrayerNotificationScheduler;
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
public final class SchedulePrayerNotificationsUseCase_Factory implements Factory<SchedulePrayerNotificationsUseCase> {
  private final Provider<PrayerNotificationScheduler> schedulerProvider;

  public SchedulePrayerNotificationsUseCase_Factory(
      Provider<PrayerNotificationScheduler> schedulerProvider) {
    this.schedulerProvider = schedulerProvider;
  }

  @Override
  public SchedulePrayerNotificationsUseCase get() {
    return newInstance(schedulerProvider.get());
  }

  public static SchedulePrayerNotificationsUseCase_Factory create(
      Provider<PrayerNotificationScheduler> schedulerProvider) {
    return new SchedulePrayerNotificationsUseCase_Factory(schedulerProvider);
  }

  public static SchedulePrayerNotificationsUseCase newInstance(
      PrayerNotificationScheduler scheduler) {
    return new SchedulePrayerNotificationsUseCase(scheduler);
  }
}
