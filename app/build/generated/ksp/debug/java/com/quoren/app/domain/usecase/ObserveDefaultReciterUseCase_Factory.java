package com.quoren.app.domain.usecase;

import com.quoren.app.domain.repository.ReciterRepository;
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
public final class ObserveDefaultReciterUseCase_Factory implements Factory<ObserveDefaultReciterUseCase> {
  private final Provider<ReciterRepository> recitersProvider;

  public ObserveDefaultReciterUseCase_Factory(Provider<ReciterRepository> recitersProvider) {
    this.recitersProvider = recitersProvider;
  }

  @Override
  public ObserveDefaultReciterUseCase get() {
    return newInstance(recitersProvider.get());
  }

  public static ObserveDefaultReciterUseCase_Factory create(
      Provider<ReciterRepository> recitersProvider) {
    return new ObserveDefaultReciterUseCase_Factory(recitersProvider);
  }

  public static ObserveDefaultReciterUseCase newInstance(ReciterRepository reciters) {
    return new ObserveDefaultReciterUseCase(reciters);
  }
}
