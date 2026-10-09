package com.quoren.app.domain.qibla;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class QiblaEngine_Factory implements Factory<QiblaEngine> {
  @Override
  public QiblaEngine get() {
    return newInstance();
  }

  public static QiblaEngine_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static QiblaEngine newInstance() {
    return new QiblaEngine();
  }

  private static final class InstanceHolder {
    private static final QiblaEngine_Factory INSTANCE = new QiblaEngine_Factory();
  }
}
