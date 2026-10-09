package com.quoren.app.data.repository;

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
public final class ReadingProgressRepositoryImpl_Factory implements Factory<ReadingProgressRepositoryImpl> {
  private final Provider<QuranDatabase> dbProvider;

  public ReadingProgressRepositoryImpl_Factory(Provider<QuranDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ReadingProgressRepositoryImpl get() {
    return newInstance(dbProvider.get());
  }

  public static ReadingProgressRepositoryImpl_Factory create(Provider<QuranDatabase> dbProvider) {
    return new ReadingProgressRepositoryImpl_Factory(dbProvider);
  }

  public static ReadingProgressRepositoryImpl newInstance(QuranDatabase db) {
    return new ReadingProgressRepositoryImpl(db);
  }
}
