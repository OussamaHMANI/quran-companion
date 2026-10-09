package com.quoren.app.data.backup;

import com.quoren.app.data.local.QuranDatabase;
import com.quoren.app.domain.repository.AudioSettingsRepository;
import com.quoren.app.domain.repository.MosqueRepository;
import com.quoren.app.domain.repository.PrayerRepository;
import com.quoren.app.domain.repository.ReaderSettingsRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("dagger.Reusable")
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
public final class LocalBackupRepositoryImpl_Factory implements Factory<LocalBackupRepositoryImpl> {
  private final Provider<QuranDatabase> dbProvider;

  private final Provider<ReaderSettingsRepository> readerSettingsProvider;

  private final Provider<AudioSettingsRepository> audioSettingsProvider;

  private final Provider<PrayerRepository> prayerRepositoryProvider;

  private final Provider<MosqueRepository> mosqueRepositoryProvider;

  public LocalBackupRepositoryImpl_Factory(Provider<QuranDatabase> dbProvider,
      Provider<ReaderSettingsRepository> readerSettingsProvider,
      Provider<AudioSettingsRepository> audioSettingsProvider,
      Provider<PrayerRepository> prayerRepositoryProvider,
      Provider<MosqueRepository> mosqueRepositoryProvider) {
    this.dbProvider = dbProvider;
    this.readerSettingsProvider = readerSettingsProvider;
    this.audioSettingsProvider = audioSettingsProvider;
    this.prayerRepositoryProvider = prayerRepositoryProvider;
    this.mosqueRepositoryProvider = mosqueRepositoryProvider;
  }

  @Override
  public LocalBackupRepositoryImpl get() {
    return newInstance(dbProvider.get(), readerSettingsProvider.get(), audioSettingsProvider.get(), prayerRepositoryProvider.get(), mosqueRepositoryProvider.get());
  }

  public static LocalBackupRepositoryImpl_Factory create(Provider<QuranDatabase> dbProvider,
      Provider<ReaderSettingsRepository> readerSettingsProvider,
      Provider<AudioSettingsRepository> audioSettingsProvider,
      Provider<PrayerRepository> prayerRepositoryProvider,
      Provider<MosqueRepository> mosqueRepositoryProvider) {
    return new LocalBackupRepositoryImpl_Factory(dbProvider, readerSettingsProvider, audioSettingsProvider, prayerRepositoryProvider, mosqueRepositoryProvider);
  }

  public static LocalBackupRepositoryImpl newInstance(QuranDatabase db,
      ReaderSettingsRepository readerSettings, AudioSettingsRepository audioSettings,
      PrayerRepository prayerRepository, MosqueRepository mosqueRepository) {
    return new LocalBackupRepositoryImpl(db, readerSettings, audioSettings, prayerRepository, mosqueRepository);
  }
}
