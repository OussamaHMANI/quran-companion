package com.quoren.app.di;

import android.content.Context;
import androidx.media3.datasource.cache.SimpleCache;
import com.quoren.app.data.audio.ConfigurableLruCacheEvictor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AudioModule_SimpleCacheFactory implements Factory<SimpleCache> {
  private final Provider<Context> contextProvider;

  private final Provider<ConfigurableLruCacheEvictor> evictorProvider;

  public AudioModule_SimpleCacheFactory(Provider<Context> contextProvider,
      Provider<ConfigurableLruCacheEvictor> evictorProvider) {
    this.contextProvider = contextProvider;
    this.evictorProvider = evictorProvider;
  }

  @Override
  public SimpleCache get() {
    return simpleCache(contextProvider.get(), evictorProvider.get());
  }

  public static AudioModule_SimpleCacheFactory create(Provider<Context> contextProvider,
      Provider<ConfigurableLruCacheEvictor> evictorProvider) {
    return new AudioModule_SimpleCacheFactory(contextProvider, evictorProvider);
  }

  public static SimpleCache simpleCache(Context context, ConfigurableLruCacheEvictor evictor) {
    return Preconditions.checkNotNullFromProvides(AudioModule.INSTANCE.simpleCache(context, evictor));
  }
}
