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
public final class GetRecitersUseCase_Factory implements Factory<GetRecitersUseCase> {
  private final Provider<ReciterRepository> recitersProvider;

  public GetRecitersUseCase_Factory(Provider<ReciterRepository> recitersProvider) {
    this.recitersProvider = recitersProvider;
  }

  @Override
  public GetRecitersUseCase get() {
    return newInstance(recitersProvider.get());
  }

  public static GetRecitersUseCase_Factory create(Provider<ReciterRepository> recitersProvider) {
    return new GetRecitersUseCase_Factory(recitersProvider);
  }

  public static GetRecitersUseCase newInstance(ReciterRepository reciters) {
    return new GetRecitersUseCase(reciters);
  }
}
