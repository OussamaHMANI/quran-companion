package com.quoren.app.data.download;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = SurahDownloadWorker.class
)
public interface SurahDownloadWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.quoren.app.data.download.SurahDownloadWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(
      SurahDownloadWorker_AssistedFactory factory);
}
