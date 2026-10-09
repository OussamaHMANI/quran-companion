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
public final class BookmarkRepositoryImpl_Factory implements Factory<BookmarkRepositoryImpl> {
  private final Provider<QuranDatabase> dbProvider;

  public BookmarkRepositoryImpl_Factory(Provider<QuranDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BookmarkRepositoryImpl get() {
    return newInstance(dbProvider.get());
  }

  public static BookmarkRepositoryImpl_Factory create(Provider<QuranDatabase> dbProvider) {
    return new BookmarkRepositoryImpl_Factory(dbProvider);
  }

  public static BookmarkRepositoryImpl newInstance(QuranDatabase db) {
    return new BookmarkRepositoryImpl(db);
  }
}
