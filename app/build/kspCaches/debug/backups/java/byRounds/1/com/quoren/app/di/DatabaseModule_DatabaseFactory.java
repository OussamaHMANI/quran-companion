package com.quoren.app.di;

import android.content.Context;
import com.quoren.app.data.local.QuranDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_DatabaseFactory implements Factory<QuranDatabase> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_DatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public QuranDatabase get() {
    return database(contextProvider.get());
  }

  public static DatabaseModule_DatabaseFactory create(Provider<Context> contextProvider) {
    return new DatabaseModule_DatabaseFactory(contextProvider);
  }

  public static QuranDatabase database(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.database(context));
  }
}
