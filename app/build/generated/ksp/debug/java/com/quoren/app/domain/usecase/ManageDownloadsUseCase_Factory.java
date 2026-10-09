package com.quoren.app.domain.usecase;

import com.quoren.app.domain.repository.DownloadRepository;
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
public final class ManageDownloadsUseCase_Factory implements Factory<ManageDownloadsUseCase> {
  private final Provider<DownloadRepository> downloadsProvider;

  private final Provider<ReciterRepository> recitersProvider;

  public ManageDownloadsUseCase_Factory(Provider<DownloadRepository> downloadsProvider,
      Provider<ReciterRepository> recitersProvider) {
    this.downloadsProvider = downloadsProvider;
    this.recitersProvider = recitersProvider;
  }

  @Override
  public ManageDownloadsUseCase get() {
    return newInstance(downloadsProvider.get(), recitersProvider.get());
  }

  public static ManageDownloadsUseCase_Factory create(
      Provider<DownloadRepository> downloadsProvider,
      Provider<ReciterRepository> recitersProvider) {
    return new ManageDownloadsUseCase_Factory(downloadsProvider, recitersProvider);
  }

  public static ManageDownloadsUseCase newInstance(DownloadRepository downloads,
      ReciterRepository reciters) {
    return new ManageDownloadsUseCase(downloads, reciters);
  }
}
