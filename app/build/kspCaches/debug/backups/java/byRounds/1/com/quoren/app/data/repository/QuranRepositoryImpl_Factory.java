package com.quoren.app.data.repository;

import com.quoren.app.data.importer.QuranDatasetImporter;
import com.quoren.app.data.local.QuranDatabase;
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
public final class QuranRepositoryImpl_Factory implements Factory<QuranRepositoryImpl> {
  private final Provider<QuranDatabase> dbProvider;

  private final Provider<QuranDatasetImporter> importerProvider;

  public QuranRepositoryImpl_Factory(Provider<QuranDatabase> dbProvider,
      Provider<QuranDatasetImporter> importerProvider) {
    this.dbProvider = dbProvider;
    this.importerProvider = importerProvider;
  }

  @Override
  public QuranRepositoryImpl get() {
    return newInstance(dbProvider.get(), importerProvider.get());
  }

  public static QuranRepositoryImpl_Factory create(Provider<QuranDatabase> dbProvider,
      Provider<QuranDatasetImporter> importerProvider) {
    return new QuranRepositoryImpl_Factory(dbProvider, importerProvider);
  }

  public static QuranRepositoryImpl newInstance(QuranDatabase db, QuranDatasetImporter importer) {
    return new QuranRepositoryImpl(db, importer);
  }
}
