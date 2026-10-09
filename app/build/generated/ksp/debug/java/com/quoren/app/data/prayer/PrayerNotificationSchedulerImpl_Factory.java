package com.quoren.app.data.prayer;

import android.content.Context;
import com.quoren.app.domain.prayer.PrayerAlertPlanner;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class PrayerNotificationSchedulerImpl_Factory implements Factory<PrayerNotificationSchedulerImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<PrayerAlertPlanner> plannerProvider;

  public PrayerNotificationSchedulerImpl_Factory(Provider<Context> contextProvider,
      Provider<PrayerAlertPlanner> plannerProvider) {
    this.contextProvider = contextProvider;
    this.plannerProvider = plannerProvider;
  }

  @Override
  public PrayerNotificationSchedulerImpl get() {
    return newInstance(contextProvider.get(), plannerProvider.get());
  }

  public static PrayerNotificationSchedulerImpl_Factory create(Provider<Context> contextProvider,
      Provider<PrayerAlertPlanner> plannerProvider) {
    return new PrayerNotificationSchedulerImpl_Factory(contextProvider, plannerProvider);
  }

  public static PrayerNotificationSchedulerImpl newInstance(Context context,
      PrayerAlertPlanner planner) {
    return new PrayerNotificationSchedulerImpl(context, planner);
  }
}
