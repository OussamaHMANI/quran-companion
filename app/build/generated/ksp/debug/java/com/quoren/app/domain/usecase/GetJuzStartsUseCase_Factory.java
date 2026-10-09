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
public final class GetJuzStartsUseCase_Factory implements Factory<GetJuzStartsUseCase> {
  private final Provider<QuranRepository> quranProvider;

  public GetJuzStartsUseCase_Factory(Provider<QuranRepository> quranProvider) {
    this.quranProvider = quranProvider;
  }

  @Override
  public GetJuzStartsUseCase get() {
    return newInstance(quranProvider.get());
  }

  public static GetJuzStartsUseCase_Factory create(Provider<QuranRepository> quranProvider) {
    return new GetJuzStartsUseCase_Factory(quranProvider);
  }

  public static GetJuzStartsUseCase newInstance(QuranRepository quran) {
    return new GetJuzStartsUseCase(quran);
  }
}
