package com.quoren.app.data.download;

import android.content.Context;
import com.quoren.app.data.audio.AudioFileStore;
import com.quoren.app.data.local.QuranDatabase;
import com.quoren.app.domain.repository.QuranRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class DownloadRepositoryImpl_Factory implements Factory<DownloadRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<QuranDatabase> dbProvider;

  private final Provider<AudioFileStore> filesProvider;

  private final Provider<QuranRepository> quranProvider;

  public DownloadRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<QuranDatabase> dbProvider, Provider<AudioFileStore> filesProvider,
      Provider<QuranRepository> quranProvider) {
    this.contextProvider = contextProvider;
    this.dbProvider = dbProvider;
    this.filesProvider = filesProvider;
    this.quranProvider = quranProvider;
  }

  @Override
  public DownloadRepositoryImpl get() {
    return newInstance(contextProvider.get(), dbProvider.get(), filesProvider.get(), quranProvider.get());
  }

  public static DownloadRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<QuranDatabase> dbProvider, Provider<AudioFileStore> filesProvider,
      Provider<QuranRepository> quranProvider) {
    return new DownloadRepositoryImpl_Factory(contextProvider, dbProvider, filesProvider, quranProvider);
  }

  public static DownloadRepositoryImpl newInstance(Context context, QuranDatabase db,
      AudioFileStore files, QuranRepository quran) {
    return new DownloadRepositoryImpl(context, db, files, quran);
  }
}
