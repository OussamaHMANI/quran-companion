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
public final class GetAyahByReferenceUseCase_Factory implements Factory<GetAyahByReferenceUseCase> {
  private final Provider<QuranRepository> quranProvider;

  public GetAyahByReferenceUseCase_Factory(Provider<QuranRepository> quranProvider) {
    this.quranProvider = quranProvider;
  }

  @Override
  public GetAyahByReferenceUseCase get() {
    return newInstance(quranProvider.get());
  }

  public static GetAyahByReferenceUseCase_Factory create(Provider<QuranRepository> quranProvider) {
    return new GetAyahByReferenceUseCase_Factory(quranProvider);
  }

  public static GetAyahByReferenceUseCase newInstance(QuranRepository quran) {
    return new GetAyahByReferenceUseCase(quran);
  }
}
