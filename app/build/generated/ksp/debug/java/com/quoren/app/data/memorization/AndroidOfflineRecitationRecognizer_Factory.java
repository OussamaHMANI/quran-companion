package com.quoren.app.data.memorization;

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
public final class AndroidOfflineRecitationRecognizer_Factory implements Factory<AndroidOfflineRecitationRecognizer> {
  private final Provider<Context> contextProvider;

  private final Provider<OkHttpClient> httpProvider;

  public AndroidOfflineRecitationRecognizer_Factory(Provider<Context> contextProvider,
      Provider<OkHttpClient> httpProvider) {
    this.contextProvider = contextProvider;
    this.httpProvider = httpProvider;
  }

  @Override
  public AndroidOfflineRecitationRecognizer get() {
    return newInstance(contextProvider.get(), httpProvider.get());
  }

  public static AndroidOfflineRecitationRecognizer_Factory create(Provider<Context> contextProvider,
      Provider<OkHttpClient> httpProvider) {
    return new AndroidOfflineRecitationRecognizer_Factory(contextProvider, httpProvider);
  }

  public static AndroidOfflineRecitationRecognizer newInstance(Context context, OkHttpClient http) {
    return new AndroidOfflineRecitationRecognizer(context, http);
  }
}
