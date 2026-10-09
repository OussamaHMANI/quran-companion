package com.quoren.app.domain.usecase;

import com.quoren.app.domain.repository.LocalBackupRepository;
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
public final class ExportUserBackupUseCase_Factory implements Factory<ExportUserBackupUseCase> {
  private final Provider<LocalBackupRepository> backupsProvider;

  public ExportUserBackupUseCase_Factory(Provider<LocalBackupRepository> backupsProvider) {
    this.backupsProvider = backupsProvider;
  }

  @Override
  public ExportUserBackupUseCase get() {
    return newInstance(backupsProvider.get());
  }

  public static ExportUserBackupUseCase_Factory create(
      Provider<LocalBackupRepository> backupsProvider) {
    return new ExportUserBackupUseCase_Factory(backupsProvider);
  }

  public static ExportUserBackupUseCase newInstance(LocalBackupRepository backups) {
    return new ExportUserBackupUseCase(backups);
  }
}
