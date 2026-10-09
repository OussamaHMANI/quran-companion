package com.quoren.app.data.audio;

import androidx.media3.datasource.cache.SimpleCache;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class AudioCacheManagerImpl_Factory implements Factory<AudioCacheManagerImpl> {
  private final Provider<SimpleCache> cacheProvider;

  private final Provider<ConfigurableLruCacheEvictor> evictorProvider;

  public AudioCacheManagerImpl_Factory(Provider<SimpleCache> cacheProvider,
      Provider<ConfigurableLruCacheEvictor> evictorProvider) {
    this.cacheProvider = cacheProvider;
    this.evictorProvider = evictorProvider;
  }

  @Override
  public AudioCacheManagerImpl get() {
    return newInstance(cacheProvider.get(), evictorProvider.get());
  }

  public static AudioCacheManagerImpl_Factory create(Provider<SimpleCache> cacheProvider,
      Provider<ConfigurableLruCacheEvictor> evictorProvider) {
    return new AudioCacheManagerImpl_Factory(cacheProvider, evictorProvider);
  }

  public static AudioCacheManagerImpl newInstance(SimpleCache cache,
      ConfigurableLruCacheEvictor evictor) {
    return new AudioCacheManagerImpl(cache, evictor);
  }
}
