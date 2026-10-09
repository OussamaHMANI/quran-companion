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
public final class GetAyahUseCase_Factory implements Factory<GetAyahUseCase> {
  private final Provider<QuranRepository> quranProvider;

  public GetAyahUseCase_Factory(Provider<QuranRepository> quranProvider) {
    this.quranProvider = quranProvider;
  }

  @Override
  public GetAyahUseCase get() {
    return newInstance(quranProvider.get());
  }

  public static GetAyahUseCase_Factory create(Provider<QuranRepository> quranProvider) {
    return new GetAyahUseCase_Factory(quranProvider);
  }

  public static GetAyahUseCase newInstance(QuranRepository quran) {
    return new GetAyahUseCase(quran);
  }
}
