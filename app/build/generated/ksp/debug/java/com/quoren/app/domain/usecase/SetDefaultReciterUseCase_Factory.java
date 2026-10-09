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
public final class SetDefaultReciterUseCase_Factory implements Factory<SetDefaultReciterUseCase> {
  private final Provider<ReciterRepository> recitersProvider;

  public SetDefaultReciterUseCase_Factory(Provider<ReciterRepository> recitersProvider) {
    this.recitersProvider = recitersProvider;
  }

  @Override
  public SetDefaultReciterUseCase get() {
    return newInstance(recitersProvider.get());
  }

  public static SetDefaultReciterUseCase_Factory create(
      Provider<ReciterRepository> recitersProvider) {
    return new SetDefaultReciterUseCase_Factory(recitersProvider);
  }

  public static SetDefaultReciterUseCase newInstance(ReciterRepository reciters) {
    return new SetDefaultReciterUseCase(reciters);
  }
}
