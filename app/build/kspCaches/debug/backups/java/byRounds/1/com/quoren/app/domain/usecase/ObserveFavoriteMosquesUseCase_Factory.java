package com.quoren.app.domain.usecase;

import com.quoren.app.domain.repository.MosqueRepository;
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
public final class ObserveFavoriteMosquesUseCase_Factory implements Factory<ObserveFavoriteMosquesUseCase> {
  private final Provider<MosqueRepository> mosquesProvider;

  public ObserveFavoriteMosquesUseCase_Factory(Provider<MosqueRepository> mosquesProvider) {
    this.mosquesProvider = mosquesProvider;
  }

  @Override
  public ObserveFavoriteMosquesUseCase get() {
    return newInstance(mosquesProvider.get());
  }

  public static ObserveFavoriteMosquesUseCase_Factory create(
      Provider<MosqueRepository> mosquesProvider) {
    return new ObserveFavoriteMosquesUseCase_Factory(mosquesProvider);
  }

  public static ObserveFavoriteMosquesUseCase newInstance(MosqueRepository mosques) {
    return new ObserveFavoriteMosquesUseCase(mosques);
  }
}
