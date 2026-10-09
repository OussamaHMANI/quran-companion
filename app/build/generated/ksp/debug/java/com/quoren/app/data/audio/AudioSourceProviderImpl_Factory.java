package com.quoren.app.data.audio;

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
public final class AudioSourceProviderImpl_Factory implements Factory<AudioSourceProviderImpl> {
  private final Provider<AudioFileStore> filesProvider;

  public AudioSourceProviderImpl_Factory(Provider<AudioFileStore> filesProvider) {
    this.filesProvider = filesProvider;
  }

  @Override
  public AudioSourceProviderImpl get() {
    return newInstance(filesProvider.get());
  }

  public static AudioSourceProviderImpl_Factory create(Provider<AudioFileStore> filesProvider) {
    return new AudioSourceProviderImpl_Factory(filesProvider);
  }

  public static AudioSourceProviderImpl newInstance(AudioFileStore files) {
    return new AudioSourceProviderImpl(files);
  }
}
