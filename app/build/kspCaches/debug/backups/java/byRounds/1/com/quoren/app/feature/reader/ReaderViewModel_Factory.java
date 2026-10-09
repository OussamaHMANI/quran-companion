package com.quoren.app.feature.reader;

import androidx.lifecycle.SavedStateHandle;
import com.quoren.app.domain.audio.AyahSynchronizationEngine;
import com.quoren.app.domain.memorization.RecitationRecognizer;
import com.quoren.app.domain.usecase.CompleteDailyGoalAyahUseCase;
import com.quoren.app.domain.usecase.GetAyahUseCase;
import com.quoren.app.domain.usecase.GetBasmalaUseCase;
import com.quoren.app.domain.usecase.GetPageUseCase;
import com.quoren.app.domain.usecase.GetSurahsUseCase;
import com.quoren.app.domain.usecase.ObserveAudioSettingsUseCase;
import com.quoren.app.domain.usecase.ObserveBookmarkedAyahIdsUseCase;
import com.quoren.app.domain.usecase.ObservePlaybackStateUseCase;
import com.quoren.app.domain.usecase.ObserveReaderSettingsUseCase;
import com.quoren.app.domain.usecase.PlayAudioUseCase;
import com.quoren.app.domain.usecase.SaveReadingProgressUseCase;
import com.quoren.app.domain.usecase.ToggleBookmarkUseCase;
import com.quoren.app.domain.usecase.UpdateMemorizationStatusUseCase;
import com.quoren.app.domain.usecase.UpdateReaderSettingsUseCase;
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
public final class ReaderViewModel_Factory implements Factory<ReaderViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<GetPageUseCase> getPageProvider;

  private final Provider<GetSurahsUseCase> getSurahsProvider;

  private final Provider<GetBasmalaUseCase> getBasmalaProvider;

  private final Provider<ObserveReaderSettingsUseCase> observeSettingsProvider;

  private final Provider<ObserveBookmarkedAyahIdsUseCase> observeBookmarkedIdsProvider;

  private final Provider<UpdateReaderSettingsUseCase> updateSettingsProvider;

  private final Provider<ToggleBookmarkUseCase> toggleBookmarkProvider;

  private final Provider<SaveReadingProgressUseCase> saveProgressProvider;

  private final Provider<PlayAudioUseCase> playAudioProvider;

  private final Provider<ObserveAudioSettingsUseCase> audioSettingsProvider;

  private final Provider<GetAyahUseCase> getAyahByIdProvider;

  private final Provider<UpdateMemorizationStatusUseCase> updateMemorizationStatusProvider;

  private final Provider<CompleteDailyGoalAyahUseCase> completeDailyGoalProvider;

  private final Provider<RecitationRecognizer> recognizerProvider;

  private final Provider<AyahSynchronizationEngine> syncEngineProvider;

  private final Provider<ObservePlaybackStateUseCase> observePlaybackProvider;

  public ReaderViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<GetPageUseCase> getPageProvider, Provider<GetSurahsUseCase> getSurahsProvider,
      Provider<GetBasmalaUseCase> getBasmalaProvider,
      Provider<ObserveReaderSettingsUseCase> observeSettingsProvider,
      Provider<ObserveBookmarkedAyahIdsUseCase> observeBookmarkedIdsProvider,
      Provider<UpdateReaderSettingsUseCase> updateSettingsProvider,
      Provider<ToggleBookmarkUseCase> toggleBookmarkProvider,
      Provider<SaveReadingProgressUseCase> saveProgressProvider,
      Provider<PlayAudioUseCase> playAudioProvider,
      Provider<ObserveAudioSettingsUseCase> audioSettingsProvider,
      Provider<GetAyahUseCase> getAyahByIdProvider,
      Provider<UpdateMemorizationStatusUseCase> updateMemorizationStatusProvider,
      Provider<CompleteDailyGoalAyahUseCase> completeDailyGoalProvider,
      Provider<RecitationRecognizer> recognizerProvider,
      Provider<AyahSynchronizationEngine> syncEngineProvider,
      Provider<ObservePlaybackStateUseCase> observePlaybackProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.getPageProvider = getPageProvider;
    this.getSurahsProvider = getSurahsProvider;
    this.getBasmalaProvider = getBasmalaProvider;
    this.observeSettingsProvider = observeSettingsProvider;
    this.observeBookmarkedIdsProvider = observeBookmarkedIdsProvider;
    this.updateSettingsProvider = updateSettingsProvider;
    this.toggleBookmarkProvider = toggleBookmarkProvider;
    this.saveProgressProvider = saveProgressProvider;
    this.playAudioProvider = playAudioProvider;
    this.audioSettingsProvider = audioSettingsProvider;
    this.getAyahByIdProvider = getAyahByIdProvider;
    this.updateMemorizationStatusProvider = updateMemorizationStatusProvider;
    this.completeDailyGoalProvider = completeDailyGoalProvider;
    this.recognizerProvider = recognizerProvider;
    this.syncEngineProvider = syncEngineProvider;
    this.observePlaybackProvider = observePlaybackProvider;
  }

  @Override
  public ReaderViewModel get() {
    return newInstance(savedStateHandleProvider.get(), getPageProvider.get(), getSurahsProvider.get(), getBasmalaProvider.get(), observeSettingsProvider.get(), observeBookmarkedIdsProvider.get(), updateSettingsProvider.get(), toggleBookmarkProvider.get(), saveProgressProvider.get(), playAudioProvider.get(), audioSettingsProvider.get(), getAyahByIdProvider.get(), updateMemorizationStatusProvider.get(), completeDailyGoalProvider.get(), recognizerProvider.get(), syncEngineProvider.get(), observePlaybackProvider.get());
  }

  public static ReaderViewModel_Factory create(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<GetPageUseCase> getPageProvider, Provider<GetSurahsUseCase> getSurahsProvider,
      Provider<GetBasmalaUseCase> getBasmalaProvider,
      Provider<ObserveReaderSettingsUseCase> observeSettingsProvider,
      Provider<ObserveBookmarkedAyahIdsUseCase> observeBookmarkedIdsProvider,
      Provider<UpdateReaderSettingsUseCase> updateSettingsProvider,
      Provider<ToggleBookmarkUseCase> toggleBookmarkProvider,
      Provider<SaveReadingProgressUseCase> saveProgressProvider,
      Provider<PlayAudioUseCase> playAudioProvider,
      Provider<ObserveAudioSettingsUseCase> audioSettingsProvider,
      Provider<GetAyahUseCase> getAyahByIdProvider,
      Provider<UpdateMemorizationStatusUseCase> updateMemorizationStatusProvider,
      Provider<CompleteDailyGoalAyahUseCase> completeDailyGoalProvider,
      Provider<RecitationRecognizer> recognizerProvider,
      Provider<AyahSynchronizationEngine> syncEngineProvider,
      Provider<ObservePlaybackStateUseCase> observePlaybackProvider) {
    return new ReaderViewModel_Factory(savedStateHandleProvider, getPageProvider, getSurahsProvider, getBasmalaProvider, observeSettingsProvider, observeBookmarkedIdsProvider, updateSettingsProvider, toggleBookmarkProvider, saveProgressProvider, playAudioProvider, audioSettingsProvider, getAyahByIdProvider, updateMemorizationStatusProvider, completeDailyGoalProvider, recognizerProvider, syncEngineProvider, observePlaybackProvider);
  }

  public static ReaderViewModel newInstance(SavedStateHandle savedStateHandle,
      GetPageUseCase getPage, GetSurahsUseCase getSurahs, GetBasmalaUseCase getBasmala,
      ObserveReaderSettingsUseCase observeSettings,
      ObserveBookmarkedAyahIdsUseCase observeBookmarkedIds,
      UpdateReaderSettingsUseCase updateSettings, ToggleBookmarkUseCase toggleBookmark,
      SaveReadingProgressUseCase saveProgress, PlayAudioUseCase playAudio,
      ObserveAudioSettingsUseCase audioSettings, GetAyahUseCase getAyahById,
      UpdateMemorizationStatusUseCase updateMemorizationStatus,
      CompleteDailyGoalAyahUseCase completeDailyGoal, RecitationRecognizer recognizer,
      AyahSynchronizationEngine syncEngine, ObservePlaybackStateUseCase observePlayback) {
    return new ReaderViewModel(savedStateHandle, getPage, getSurahs, getBasmala, observeSettings, observeBookmarkedIds, updateSettings, toggleBookmark, saveProgress, playAudio, audioSettings, getAyahById, updateMemorizationStatus, completeDailyGoal, recognizer, syncEngine, observePlayback);
  }
}
