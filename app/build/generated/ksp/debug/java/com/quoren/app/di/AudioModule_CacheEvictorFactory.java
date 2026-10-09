package com.quoren.app.di;

import com.quoren.app.data.audio.ConfigurableLruCacheEvictor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class AudioModule_CacheEvictorFactory implements Factory<ConfigurableLruCacheEvictor> {
  @Override
  public ConfigurableLruCacheEvictor get() {
    return cacheEvictor();
  }

  public static AudioModule_CacheEvictorFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ConfigurableLruCacheEvictor cacheEvictor() {
    return Preconditions.checkNotNullFromProvides(AudioModule.INSTANCE.cacheEvictor());
  }

  private static final class InstanceHolder {
    private static final AudioModule_CacheEvictorFactory INSTANCE = new AudioModule_CacheEvictorFactory();
  }
}
