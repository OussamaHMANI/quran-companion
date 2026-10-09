package com.quoren.app.data.download;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.quoren.app.data.audio.AudioFileStore;
import com.quoren.app.data.local.QuranDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

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
public final class SurahDownloadWorker_Factory {
  private final Provider<QuranDatabase> dbProvider;

  private final Provider<AudioFileStore> filesProvider;

  private final Provider<OkHttpClient> httpProvider;

  public SurahDownloadWorker_Factory(Provider<QuranDatabase> dbProvider,
      Provider<AudioFileStore> filesProvider, Provider<OkHttpClient> httpProvider) {
    this.dbProvider = dbProvider;
    this.filesProvider = filesProvider;
    this.httpProvider = httpProvider;
  }

  public SurahDownloadWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, dbProvider.get(), filesProvider.get(), httpProvider.get());
  }

  public static SurahDownloadWorker_Factory create(Provider<QuranDatabase> dbProvider,
      Provider<AudioFileStore> filesProvider, Provider<OkHttpClient> httpProvider) {
    return new SurahDownloadWorker_Factory(dbProvider, filesProvider, httpProvider);
  }

  public static SurahDownloadWorker newInstance(Context context, WorkerParameters params,
      QuranDatabase db, AudioFileStore files, OkHttpClient http) {
    return new SurahDownloadWorker(context, params, db, files, http);
  }
}
