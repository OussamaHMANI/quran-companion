package com.quoren.app.data.prayer;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class PrayerReminderRefreshWorker_AssistedFactory_Impl implements PrayerReminderRefreshWorker_AssistedFactory {
  private final PrayerReminderRefreshWorker_Factory delegateFactory;

  PrayerReminderRefreshWorker_AssistedFactory_Impl(
      PrayerReminderRefreshWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public PrayerReminderRefreshWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<PrayerReminderRefreshWorker_AssistedFactory> create(
      PrayerReminderRefreshWorker_Factory delegateFactory) {
    return InstanceFactory.create(new PrayerReminderRefreshWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<PrayerReminderRefreshWorker_AssistedFactory> createFactoryProvider(
      PrayerReminderRefreshWorker_Factory delegateFactory) {
    return InstanceFactory.create(new PrayerReminderRefreshWorker_AssistedFactory_Impl(delegateFactory));
  }
}
