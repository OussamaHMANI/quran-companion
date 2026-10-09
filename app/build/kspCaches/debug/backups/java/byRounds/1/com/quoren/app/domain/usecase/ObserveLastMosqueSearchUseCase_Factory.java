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
public final class ObserveLastMosqueSearchUseCase_Factory implements Factory<ObserveLastMosqueSearchUseCase> {
  private final Provider<MosqueRepository> mosquesProvider;

  public ObserveLastMosqueSearchUseCase_Factory(Provider<MosqueRepository> mosquesProvider) {
    this.mosquesProvider = mosquesProvider;
  }

  @Override
  public ObserveLastMosqueSearchUseCase get() {
    return newInstance(mosquesProvider.get());
  }

  public static ObserveLastMosqueSearchUseCase_Factory create(
      Provider<MosqueRepository> mosquesProvider) {
    return new ObserveLastMosqueSearchUseCase_Factory(mosquesProvider);
  }

  public static ObserveLastMosqueSearchUseCase newInstance(MosqueRepository mosques) {
    return new ObserveLastMosqueSearchUseCase(mosques);
  }
}
