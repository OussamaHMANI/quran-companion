package com.quoren.app.data.repository;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ReaderSettingsRepositoryImpl_Factory implements Factory<ReaderSettingsRepositoryImpl> {
  private final Provider<Context> contextProvider;

  public ReaderSettingsRepositoryImpl_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ReaderSettingsRepositoryImpl get() {
    return newInstance(contextProvider.get());
  }

  public static ReaderSettingsRepositoryImpl_Factory create(Provider<Context> contextProvider) {
    return new ReaderSettingsRepositoryImpl_Factory(contextProvider);
  }

  public static ReaderSettingsRepositoryImpl newInstance(Context context) {
    return new ReaderSettingsRepositoryImpl(context);
  }
}
