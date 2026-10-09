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
public final class GetBasmalaUseCase_Factory implements Factory<GetBasmalaUseCase> {
  private final Provider<QuranRepository> quranProvider;

  public GetBasmalaUseCase_Factory(Provider<QuranRepository> quranProvider) {
    this.quranProvider = quranProvider;
  }

  @Override
  public GetBasmalaUseCase get() {
    return newInstance(quranProvider.get());
  }

  public static GetBasmalaUseCase_Factory create(Provider<QuranRepository> quranProvider) {
    return new GetBasmalaUseCase_Factory(quranProvider);
  }

  public static GetBasmalaUseCase newInstance(QuranRepository quran) {
    return new GetBasmalaUseCase(quran);
  }
}
