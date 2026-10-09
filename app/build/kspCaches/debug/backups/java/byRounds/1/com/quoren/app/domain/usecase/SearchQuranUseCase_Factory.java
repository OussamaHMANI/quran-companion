package com.quoren.app.domain.usecase;

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
public final class SearchQuranUseCase_Factory implements Factory<SearchQuranUseCase> {
  private final Provider<QuranRepository> quranProvider;

  public SearchQuranUseCase_Factory(Provider<QuranRepository> quranProvider) {
    this.quranProvider = quranProvider;
  }

  @Override
  public SearchQuranUseCase get() {
    return newInstance(quranProvider.get());
  }

  public static SearchQuranUseCase_Factory create(Provider<QuranRepository> quranProvider) {
    return new SearchQuranUseCase_Factory(quranProvider);
  }

  public static SearchQuranUseCase newInstance(QuranRepository quran) {
    return new SearchQuranUseCase(quran);
  }
}
