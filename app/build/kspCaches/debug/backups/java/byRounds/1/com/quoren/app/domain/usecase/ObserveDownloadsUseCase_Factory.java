package com.quoren.app.domain.usecase;

import com.quoren.app.domain.repository.DownloadRepository;
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
public final class ObserveDownloadsUseCase_Factory implements Factory<ObserveDownloadsUseCase> {
  private final Provider<DownloadRepository> downloadsProvider;

  public ObserveDownloadsUseCase_Factory(Provider<DownloadRepository> downloadsProvider) {
    this.downloadsProvider = downloadsProvider;
  }

  @Override
  public ObserveDownloadsUseCase get() {
    return newInstance(downloadsProvider.get());
  }

  public static ObserveDownloadsUseCase_Factory create(
      Provider<DownloadRepository> downloadsProvider) {
    return new ObserveDownloadsUseCase_Factory(downloadsProvider);
  }

  public static ObserveDownloadsUseCase newInstance(DownloadRepository downloads) {
    return new ObserveDownloadsUseCase(downloads);
  }
}
