package com.quoren.app.feature.search;

import com.quoren.app.domain.usecase.SearchQuranUseCase;
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
public final class SearchViewModel_Factory implements Factory<SearchViewModel> {
  private final Provider<SearchQuranUseCase> searchProvider;

  public SearchViewModel_Factory(Provider<SearchQuranUseCase> searchProvider) {
    this.searchProvider = searchProvider;
  }

  @Override
  public SearchViewModel get() {
    return newInstance(searchProvider.get());
  }

  public static SearchViewModel_Factory create(Provider<SearchQuranUseCase> searchProvider) {
    return new SearchViewModel_Factory(searchProvider);
  }

  public static SearchViewModel newInstance(SearchQuranUseCase search) {
    return new SearchViewModel(search);
  }
}
