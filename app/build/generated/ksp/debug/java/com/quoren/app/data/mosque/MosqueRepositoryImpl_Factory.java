package com.quoren.app.data.mosque;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

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
public final class MosqueRepositoryImpl_Factory implements Factory<MosqueRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<OkHttpClient> httpProvider;

  public MosqueRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<OkHttpClient> httpProvider) {
    this.contextProvider = contextProvider;
    this.httpProvider = httpProvider;
  }

  @Override
  public MosqueRepositoryImpl get() {
    return newInstance(contextProvider.get(), httpProvider.get());
  }

  public static MosqueRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<OkHttpClient> httpProvider) {
    return new MosqueRepositoryImpl_Factory(contextProvider, httpProvider);
  }

  public static MosqueRepositoryImpl newInstance(Context context, OkHttpClient http) {
    return new MosqueRepositoryImpl(context, http);
  }
}
