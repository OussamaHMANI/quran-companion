package com.quoren.app.domain.usecase;

import com.quoren.app.domain.repository.BookmarkRepository;
import com.quoren.app.domain.repository.QuranRepository;
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
public final class ObserveBookmarksUseCase_Factory implements Factory<ObserveBookmarksUseCase> {
  private final Provider<BookmarkRepository> bookmarksProvider;

  private final Provider<QuranRepository> quranProvider;

  public ObserveBookmarksUseCase_Factory(Provider<BookmarkRepository> bookmarksProvider,
      Provider<QuranRepository> quranProvider) {
    this.bookmarksProvider = bookmarksProvider;
    this.quranProvider = quranProvider;
  }

  @Override
  public ObserveBookmarksUseCase get() {
    return newInstance(bookmarksProvider.get(), quranProvider.get());
  }

  public static ObserveBookmarksUseCase_Factory create(
      Provider<BookmarkRepository> bookmarksProvider, Provider<QuranRepository> quranProvider) {
    return new ObserveBookmarksUseCase_Factory(bookmarksProvider, quranProvider);
  }

  public static ObserveBookmarksUseCase newInstance(BookmarkRepository bookmarks,
      QuranRepository quran) {
    return new ObserveBookmarksUseCase(bookmarks, quran);
  }
}
