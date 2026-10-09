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
public final class SearchNearbyMosquesUseCase_Factory implements Factory<SearchNearbyMosquesUseCase> {
  private final Provider<MosqueRepository> mosquesProvider;

  public SearchNearbyMosquesUseCase_Factory(Provider<MosqueRepository> mosquesProvider) {
    this.mosquesProvider = mosquesProvider;
  }

  @Override
  public SearchNearbyMosquesUseCase get() {
    return newInstance(mosquesProvider.get());
  }

  public static SearchNearbyMosquesUseCase_Factory create(
      Provider<MosqueRepository> mosquesProvider) {
    return new SearchNearbyMosquesUseCase_Factory(mosquesProvider);
  }

  public static SearchNearbyMosquesUseCase newInstance(MosqueRepository mosques) {
    return new SearchNearbyMosquesUseCase(mosques);
  }
}
