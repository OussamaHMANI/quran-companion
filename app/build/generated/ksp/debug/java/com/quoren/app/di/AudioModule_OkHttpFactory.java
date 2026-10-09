package com.quoren.app.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

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
public final class AudioModule_OkHttpFactory implements Factory<OkHttpClient> {
  @Override
  public OkHttpClient get() {
    return okHttp();
  }

  public static AudioModule_OkHttpFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static OkHttpClient okHttp() {
    return Preconditions.checkNotNullFromProvides(AudioModule.INSTANCE.okHttp());
  }

  private static final class InstanceHolder {
    private static final AudioModule_OkHttpFactory INSTANCE = new AudioModule_OkHttpFactory();
  }
}
