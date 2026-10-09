package com.quoren.app.feature.bookmarks;

import com.quoren.app.domain.usecase.ObserveBookmarksUseCase;
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
public final class BookmarksViewModel_Factory implements Factory<BookmarksViewModel> {
  private final Provider<ObserveBookmarksUseCase> observeBookmarksProvider;

  public BookmarksViewModel_Factory(Provider<ObserveBookmarksUseCase> observeBookmarksProvider) {
    this.observeBookmarksProvider = observeBookmarksProvider;
  }

  @Override
  public BookmarksViewModel get() {
    return newInstance(observeBookmarksProvider.get());
  }

  public static BookmarksViewModel_Factory create(
      Provider<ObserveBookmarksUseCase> observeBookmarksProvider) {
    return new BookmarksViewModel_Factory(observeBookmarksProvider);
  }

  public static BookmarksViewModel newInstance(ObserveBookmarksUseCase observeBookmarks) {
    return new BookmarksViewModel(observeBookmarks);
  }
}
