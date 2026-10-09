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
public final class ImportUserBackupUseCase_Factory implements Factory<ImportUserBackupUseCase> {
  private final Provider<LocalBackupRepository> backupsProvider;

  public ImportUserBackupUseCase_Factory(Provider<LocalBackupRepository> backupsProvider) {
    this.backupsProvider = backupsProvider;
  }

  @Override
  public ImportUserBackupUseCase get() {
    return newInstance(backupsProvider.get());
  }

  public static ImportUserBackupUseCase_Factory create(
      Provider<LocalBackupRepository> backupsProvider) {
    return new ImportUserBackupUseCase_Factory(backupsProvider);
  }

  public static ImportUserBackupUseCase newInstance(LocalBackupRepository backups) {
    return new ImportUserBackupUseCase(backups);
  }
}
