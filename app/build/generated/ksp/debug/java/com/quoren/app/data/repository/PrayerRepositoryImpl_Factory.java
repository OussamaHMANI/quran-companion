package com.quoren.app.data.repository;

import android.content.Context;
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
public final class PrayerRepositoryImpl_Factory implements Factory<PrayerRepositoryImpl> {
  private final Provider<Context> contextProvider;

  public PrayerRepositoryImpl_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PrayerRepositoryImpl get() {
    return newInstance(contextProvider.get());
  }

  public static PrayerRepositoryImpl_Factory create(Provider<Context> contextProvider) {
    return new PrayerRepositoryImpl_Factory(contextProvider);
  }

  public static PrayerRepositoryImpl newInstance(Context context) {
    return new PrayerRepositoryImpl(context);
  }
}
