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
public final class GetPageUseCase_Factory implements Factory<GetPageUseCase> {
  private final Provider<QuranRepository> quranProvider;

  public GetPageUseCase_Factory(Provider<QuranRepository> quranProvider) {
    this.quranProvider = quranProvider;
  }

  @Override
  public GetPageUseCase get() {
    return newInstance(quranProvider.get());
  }

  public static GetPageUseCase_Factory create(Provider<QuranRepository> quranProvider) {
    return new GetPageUseCase_Factory(quranProvider);
  }

  public static GetPageUseCase newInstance(QuranRepository quran) {
    return new GetPageUseCase(quran);
  }
}
