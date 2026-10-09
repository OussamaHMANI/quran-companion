package com.quoren.app.data.download;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SurahDownloadWorker_AssistedFactory_Impl implements SurahDownloadWorker_AssistedFactory {
  private final SurahDownloadWorker_Factory delegateFactory;

  SurahDownloadWorker_AssistedFactory_Impl(SurahDownloadWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public SurahDownloadWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<SurahDownloadWorker_AssistedFactory> create(
      SurahDownloadWorker_Factory delegateFactory) {
    return InstanceFactory.create(new SurahDownloadWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<SurahDownloadWorker_AssistedFactory> createFactoryProvider(
      SurahDownloadWorker_Factory delegateFactory) {
    return InstanceFactory.create(new SurahDownloadWorker_AssistedFactory_Impl(delegateFactory));
  }
}
