package com.quoren.app.domain.usecase;

import com.quoren.app.domain.repository.AudioCacheManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class AudioCacheUseCase_Factory implements Factory<AudioCacheUseCase> {
  private final Provider<AudioCacheManager> cacheProvider;

  public AudioCacheUseCase_Factory(Provider<AudioCacheManager> cacheProvider) {
    this.cacheProvider = cacheProvider;
  }

  @Override
  public AudioCacheUseCase get() {
    return newInstance(cacheProvider.get());
  }

  public static AudioCacheUseCase_Factory create(Provider<AudioCacheManager> cacheProvider) {
    return new AudioCacheUseCase_Factory(cacheProvider);
  }

  public static AudioCacheUseCase newInstance(AudioCacheManager cache) {
    return new AudioCacheUseCase(cache);
  }
}
