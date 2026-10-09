package com.quoren.app.di;

import android.content.Context;
import androidx.media3.datasource.cache.SimpleCache;
import androidx.media3.exoplayer.ExoPlayer;
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
public final class AudioModule_ExoPlayerFactory implements Factory<ExoPlayer> {
  private final Provider<Context> contextProvider;

  private final Provider<SimpleCache> cacheProvider;

  public AudioModule_ExoPlayerFactory(Provider<Context> contextProvider,
      Provider<SimpleCache> cacheProvider) {
    this.contextProvider = contextProvider;
    this.cacheProvider = cacheProvider;
  }

  @Override
  public ExoPlayer get() {
    return exoPlayer(contextProvider.get(), cacheProvider.get());
  }

  public static AudioModule_ExoPlayerFactory create(Provider<Context> contextProvider,
      Provider<SimpleCache> cacheProvider) {
    return new AudioModule_ExoPlayerFactory(contextProvider, cacheProvider);
  }

  public static ExoPlayer exoPlayer(Context context, SimpleCache cache) {
    return Preconditions.checkNotNullFromProvides(AudioModule.INSTANCE.exoPlayer(context, cache));
  }
}
