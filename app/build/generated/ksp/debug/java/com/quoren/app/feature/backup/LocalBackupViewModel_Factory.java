package com.quoren.app.feature.backup;

import android.content.Context;
import com.quoren.app.domain.usecase.ExportUserBackupUseCase;
import com.quoren.app.domain.usecase.ImportUserBackupUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class LocalBackupViewModel_Factory implements Factory<LocalBackupViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<ExportUserBackupUseCase> exportBackupProvider;

  private final Provider<ImportUserBackupUseCase> importBackupProvider;

  public LocalBackupViewModel_Factory(Provider<Context> contextProvider,
      Provider<ExportUserBackupUseCase> exportBackupProvider,
      Provider<ImportUserBackupUseCase> importBackupProvider) {
    this.contextProvider = contextProvider;
    this.exportBackupProvider = exportBackupProvider;
    this.importBackupProvider = importBackupProvider;
  }

  @Override
  public LocalBackupViewModel get() {
    return newInstance(contextProvider.get(), exportBackupProvider.get(), importBackupProvider.get());
  }

  public static LocalBackupViewModel_Factory create(Provider<Context> contextProvider,
      Provider<ExportUserBackupUseCase> exportBackupProvider,
      Provider<ImportUserBackupUseCase> importBackupProvider) {
    return new LocalBackupViewModel_Factory(contextProvider, exportBackupProvider, importBackupProvider);
  }

  public static LocalBackupViewModel newInstance(Context context,
      ExportUserBackupUseCase exportBackup, ImportUserBackupUseCase importBackup) {
    return new LocalBackupViewModel(context, exportBackup, importBackup);
  }
}
