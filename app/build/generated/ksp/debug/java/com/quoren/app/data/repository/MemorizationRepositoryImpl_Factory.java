package com.quoren.app.data.repository;

import com.quoren.app.data.local.QuranDatabase;
import com.quoren.app.domain.memorization.SpacedRepetitionEngine;
import com.quoren.app.domain.repository.QuranRepository;
import com.quoren.app.domain.repository.ReciterRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MemorizationRepositoryImpl_Factory implements Factory<MemorizationRepositoryImpl> {
  private final Provider<QuranDatabase> dbProvider;

  private final Provider<SpacedRepetitionEngine> plannerProvider;

  private final Provider<QuranRepository> quranProvider;

  private final Provider<ReciterRepository> recitersProvider;

  public MemorizationRepositoryImpl_Factory(Provider<QuranDatabase> dbProvider,
      Provider<SpacedRepetitionEngine> plannerProvider, Provider<QuranRepository> quranProvider,
      Provider<ReciterRepository> recitersProvider) {
    this.dbProvider = dbProvider;
    this.plannerProvider = plannerProvider;
    this.quranProvider = quranProvider;
    this.recitersProvider = recitersProvider;
  }

  @Override
  public MemorizationRepositoryImpl get() {
    return newInstance(dbProvider.get(), plannerProvider.get(), quranProvider.get(), recitersProvider.get());
  }

  public static MemorizationRepositoryImpl_Factory create(Provider<QuranDatabase> dbProvider,
      Provider<SpacedRepetitionEngine> plannerProvider, Provider<QuranRepository> quranProvider,
      Provider<ReciterRepository> recitersProvider) {
    return new MemorizationRepositoryImpl_Factory(dbProvider, plannerProvider, quranProvider, recitersProvider);
  }

  public static MemorizationRepositoryImpl newInstance(QuranDatabase db,
      SpacedRepetitionEngine planner, QuranRepository quran, ReciterRepository reciters) {
    return new MemorizationRepositoryImpl(db, planner, quran, reciters);
  }
}
