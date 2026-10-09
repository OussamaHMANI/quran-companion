package com.quoren.app;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class QuoranApp_MembersInjector implements MembersInjector<QuoranApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public QuoranApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<QuoranApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new QuoranApp_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(QuoranApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.quoren.app.QuoranApp.workerFactory")
  public static void injectWorkerFactory(QuoranApp instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
