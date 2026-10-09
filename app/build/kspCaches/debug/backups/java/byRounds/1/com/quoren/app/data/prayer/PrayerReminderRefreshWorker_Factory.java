package com.quoren.app.data.prayer;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.quoren.app.domain.repository.PrayerNotificationScheduler;
import com.quoren.app.domain.usecase.ObservePrayerConfigurationUseCase;
import dagger.internal.DaggerGenerated;
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
public final class PrayerReminderRefreshWorker_Factory {
  private final Provider<ObservePrayerConfigurationUseCase> observeConfigurationProvider;

  private final Provider<PrayerNotificationScheduler> schedulerProvider;

  public PrayerReminderRefreshWorker_Factory(
      Provider<ObservePrayerConfigurationUseCase> observeConfigurationProvider,
      Provider<PrayerNotificationScheduler> schedulerProvider) {
    this.observeConfigurationProvider = observeConfigurationProvider;
    this.schedulerProvider = schedulerProvider;
  }

  public PrayerReminderRefreshWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, observeConfigurationProvider.get(), schedulerProvider.get());
  }

  public static PrayerReminderRefreshWorker_Factory create(
      Provider<ObservePrayerConfigurationUseCase> observeConfigurationProvider,
      Provider<PrayerNotificationScheduler> schedulerProvider) {
    return new PrayerReminderRefreshWorker_Factory(observeConfigurationProvider, schedulerProvider);
  }

  public static PrayerReminderRefreshWorker newInstance(Context context, WorkerParameters params,
      ObservePrayerConfigurationUseCase observeConfiguration,
      PrayerNotificationScheduler scheduler) {
    return new PrayerReminderRefreshWorker(context, params, observeConfiguration, scheduler);
  }
}
