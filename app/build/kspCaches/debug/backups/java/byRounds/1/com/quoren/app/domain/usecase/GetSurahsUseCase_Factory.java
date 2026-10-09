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
public final class GetSurahsUseCase_Factory implements Factory<GetSurahsUseCase> {
  private final Provider<QuranRepository> quranProvider;

  public GetSurahsUseCase_Factory(Provider<QuranRepository> quranProvider) {
    this.quranProvider = quranProvider;
  }

  @Override
  public GetSurahsUseCase get() {
    return newInstance(quranProvider.get());
  }

  public static GetSurahsUseCase_Factory create(Provider<QuranRepository> quranProvider) {
    return new GetSurahsUseCase_Factory(quranProvider);
  }

  public static GetSurahsUseCase newInstance(QuranRepository quran) {
    return new GetSurahsUseCase(quran);
  }
}
