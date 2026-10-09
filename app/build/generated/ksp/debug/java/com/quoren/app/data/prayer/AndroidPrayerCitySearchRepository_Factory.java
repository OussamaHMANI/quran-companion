package com.quoren.app.data.prayer;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

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
public final class AndroidPrayerCitySearchRepository_Factory implements Factory<AndroidPrayerCitySearchRepository> {
  private final Provider<Context> contextProvider;

  private final Provider<OkHttpClient> httpProvider;

  public AndroidPrayerCitySearchRepository_Factory(Provider<Context> contextProvider,
      Provider<OkHttpClient> httpProvider) {
    this.contextProvider = contextProvider;
    this.httpProvider = httpProvider;
  }

  @Override
  public AndroidPrayerCitySearchRepository get() {
    return newInstance(contextProvider.get(), httpProvider.get());
  }

  public static AndroidPrayerCitySearchRepository_Factory create(Provider<Context> contextProvider,
      Provider<OkHttpClient> httpProvider) {
    return new AndroidPrayerCitySearchRepository_Factory(contextProvider, httpProvider);
  }

  public static AndroidPrayerCitySearchRepository newInstance(Context context, OkHttpClient http) {
    return new AndroidPrayerCitySearchRepository(context, http);
  }
}
