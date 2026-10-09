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
public final class ToggleMosqueFavoriteUseCase_Factory implements Factory<ToggleMosqueFavoriteUseCase> {
  private final Provider<MosqueRepository> mosquesProvider;

  public ToggleMosqueFavoriteUseCase_Factory(Provider<MosqueRepository> mosquesProvider) {
    this.mosquesProvider = mosquesProvider;
  }

  @Override
  public ToggleMosqueFavoriteUseCase get() {
    return newInstance(mosquesProvider.get());
  }

  public static ToggleMosqueFavoriteUseCase_Factory create(
      Provider<MosqueRepository> mosquesProvider) {
    return new ToggleMosqueFavoriteUseCase_Factory(mosquesProvider);
  }

  public static ToggleMosqueFavoriteUseCase newInstance(MosqueRepository mosques) {
    return new ToggleMosqueFavoriteUseCase(mosques);
  }
}
