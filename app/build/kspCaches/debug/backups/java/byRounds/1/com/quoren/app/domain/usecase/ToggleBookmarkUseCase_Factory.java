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
public final class ToggleBookmarkUseCase_Factory implements Factory<ToggleBookmarkUseCase> {
  private final Provider<BookmarkRepository> bookmarksProvider;

  public ToggleBookmarkUseCase_Factory(Provider<BookmarkRepository> bookmarksProvider) {
    this.bookmarksProvider = bookmarksProvider;
  }

  @Override
  public ToggleBookmarkUseCase get() {
    return newInstance(bookmarksProvider.get());
  }

  public static ToggleBookmarkUseCase_Factory create(
      Provider<BookmarkRepository> bookmarksProvider) {
    return new ToggleBookmarkUseCase_Factory(bookmarksProvider);
  }

  public static ToggleBookmarkUseCase newInstance(BookmarkRepository bookmarks) {
    return new ToggleBookmarkUseCase(bookmarks);
  }
}
