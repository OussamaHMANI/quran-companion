package com.quoren.app.domain.usecase;

import com.quoren.app.domain.repository.BookmarkRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class ObserveBookmarkedAyahIdsUseCase_Factory implements Factory<ObserveBookmarkedAyahIdsUseCase> {
  private final Provider<BookmarkRepository> bookmarksProvider;

  public ObserveBookmarkedAyahIdsUseCase_Factory(Provider<BookmarkRepository> bookmarksProvider) {
    this.bookmarksProvider = bookmarksProvider;
  }

  @Override
  public ObserveBookmarkedAyahIdsUseCase get() {
    return newInstance(bookmarksProvider.get());
  }

  public static ObserveBookmarkedAyahIdsUseCase_Factory create(
      Provider<BookmarkRepository> bookmarksProvider) {
    return new ObserveBookmarkedAyahIdsUseCase_Factory(bookmarksProvider);
  }

  public static ObserveBookmarkedAyahIdsUseCase newInstance(BookmarkRepository bookmarks) {
    return new ObserveBookmarkedAyahIdsUseCase(bookmarks);
  }
}
