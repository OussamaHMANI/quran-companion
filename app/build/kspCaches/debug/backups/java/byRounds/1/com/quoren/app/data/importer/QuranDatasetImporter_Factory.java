package com.quoren.app.data.importer;

import android.content.Context;
import com.quoren.app.data.local.QuranDatabase;
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
public final class QuranDatasetImporter_Factory implements Factory<QuranDatasetImporter> {
  private final Provider<Context> contextProvider;

  private final Provider<QuranDatabase> dbProvider;

  public QuranDatasetImporter_Factory(Provider<Context> contextProvider,
      Provider<QuranDatabase> dbProvider) {
    this.contextProvider = contextProvider;
    this.dbProvider = dbProvider;
  }

  @Override
  public QuranDatasetImporter get() {
    return newInstance(contextProvider.get(), dbProvider.get());
  }

  public static QuranDatasetImporter_Factory create(Provider<Context> contextProvider,
      Provider<QuranDatabase> dbProvider) {
    return new QuranDatasetImporter_Factory(contextProvider, dbProvider);
  }

  public static QuranDatasetImporter newInstance(Context context, QuranDatabase db) {
    return new QuranDatasetImporter(context, db);
  }
}
