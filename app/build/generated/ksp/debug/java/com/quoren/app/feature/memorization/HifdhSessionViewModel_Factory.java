package com.quoren.app.feature.memorization;

import androidx.lifecycle.SavedStateHandle;
import com.quoren.app.domain.memorization.RecitationRecognizer;
import com.quoren.app.domain.usecase.CompleteDailyGoalAyahUseCase;
import com.quoren.app.domain.usecase.CompleteMemorizationSessionUseCase;
import com.quoren.app.domain.usecase.GetAyahByReferenceUseCase;
import com.quoren.app.domain.usecase.GetMemorizationSessionUseCase;
import com.quoren.app.domain.usecase.GetSurahsUseCase;
import com.quoren.app.domain.usecase.ObserveAudioSettingsUseCase;
import com.quoren.app.domain.usecase.ObserveAyahMemorizationProgressUseCase;
import com.quoren.app.domain.usecase.ObservePlaybackStateUseCase;
import com.quoren.app.domain.usecase.PlayAudioUseCase;
import com.quoren.app.domain.usecase.RateReviewUseCase;
import com.quoren.app.domain.usecase.UpdateMemorizationSessionPositionUseCase;
import com.quoren.app.domain.usecase.UpdateMemorizationStatusUseCase;
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
public final class HifdhSessionViewModel_Factory implements Factory<HifdhSessionViewModel> {
  private final Provider<SavedStateHandle> savedStateProvider;

  private final Provider<GetMemorizationSessionUseCase> getSessionProvider;

  private final Provider<GetSurahsUseCase> getSurahsProvider;

  private final Provider<GetAyahByReferenceUseCase> getAyahProvider;

  private final Provider<ObserveAyahMemorizationProgressUseCase> observeProgressProvider;

  private final Provider<ObservePlaybackStateUseCase> observePlaybackProvider;

  private final Provider<ObserveAudioSettingsUseCase> observeAudioSettingsProvider;

  private final Provider<PlayAudioUseCase> playAudioProvider;

  private final Provider<UpdateMemorizationStatusUseCase> setStatusProvider;

  private final Provider<RateReviewUseCase> rateReviewProvider;

  private final Provider<UpdateMemorizationSessionPositionUseCase> updatePositionProvider;

  private final Provider<CompleteMemorizationSessionUseCase> completeSessionProvider;

  private final Provider<CompleteDailyGoalAyahUseCase> completeDailyGoalProvider;

  private final Provider<RecitationRecognizer> recognizerProvider;

  public HifdhSessionViewModel_Factory(Provider<SavedStateHandle> savedStateProvider,
      Provider<GetMemorizationSessionUseCase> getSessionProvider,
      Provider<GetSurahsUseCase> getSurahsProvider,
      Provider<GetAyahByReferenceUseCase> getAyahProvider,
      Provider<ObserveAyahMemorizationProgressUseCase> observeProgressProvider,
      Provider<ObservePlaybackStateUseCase> observePlaybackProvider,
      Provider<ObserveAudioSettingsUseCase> observeAudioSettingsProvider,
      Provider<PlayAudioUseCase> playAudioProvider,
      Provider<UpdateMemorizationStatusUseCase> setStatusProvider,
      Provider<RateReviewUseCase> rateReviewProvider,
      Provider<UpdateMemorizationSessionPositionUseCase> updatePositionProvider,
      Provider<CompleteMemorizationSessionUseCase> completeSessionProvider,
      Provider<CompleteDailyGoalAyahUseCase> completeDailyGoalProvider,
      Provider<RecitationRecognizer> recognizerProvider) {
    this.savedStateProvider = savedStateProvider;
    this.getSessionProvider = getSessionProvider;
    this.getSurahsProvider = getSurahsProvider;
    this.getAyahProvider = getAyahProvider;
    this.observeProgressProvider = observeProgressProvider;
    this.observePlaybackProvider = observePlaybackProvider;
    this.observeAudioSettingsProvider = observeAudioSettingsProvider;
    this.playAudioProvider = playAudioProvider;
    this.setStatusProvider = setStatusProvider;
    this.rateReviewProvider = rateReviewProvider;
    this.updatePositionProvider = updatePositionProvider;
    this.completeSessionProvider = completeSessionProvider;
    this.completeDailyGoalProvider = completeDailyGoalProvider;
    this.recognizerProvider = recognizerProvider;
  }

  @Override
  public HifdhSessionViewModel get() {
    return newInstance(savedStateProvider.get(), getSessionProvider.get(), getSurahsProvider.get(), getAyahProvider.get(), observeProgressProvider.get(), observePlaybackProvider.get(), observeAudioSettingsProvider.get(), playAudioProvider.get(), setStatusProvider.get(), rateReviewProvider.get(), updatePositionProvider.get(), completeSessionProvider.get(), completeDailyGoalProvider.get(), recognizerProvider.get());
  }

  public static HifdhSessionViewModel_Factory create(Provider<SavedStateHandle> savedStateProvider,
      Provider<GetMemorizationSessionUseCase> getSessionProvider,
      Provider<GetSurahsUseCase> getSurahsProvider,
      Provider<GetAyahByReferenceUseCase> getAyahProvider,
      Provider<ObserveAyahMemorizationProgressUseCase> observeProgressProvider,
      Provider<ObservePlaybackStateUseCase> observePlaybackProvider,
      Provider<ObserveAudioSettingsUseCase> observeAudioSettingsProvider,
      Provider<PlayAudioUseCase> playAudioProvider,
      Provider<UpdateMemorizationStatusUseCase> setStatusProvider,
      Provider<RateReviewUseCase> rateReviewProvider,
      Provider<UpdateMemorizationSessionPositionUseCase> updatePositionProvider,
      Provider<CompleteMemorizationSessionUseCase> completeSessionProvider,
      Provider<CompleteDailyGoalAyahUseCase> completeDailyGoalProvider,
      Provider<RecitationRecognizer> recognizerProvider) {
    return new HifdhSessionViewModel_Factory(savedStateProvider, getSessionProvider, getSurahsProvider, getAyahProvider, observeProgressProvider, observePlaybackProvider, observeAudioSettingsProvider, playAudioProvider, setStatusProvider, rateReviewProvider, updatePositionProvider, completeSessionProvider, completeDailyGoalProvider, recognizerProvider);
  }

  public static HifdhSessionViewModel newInstance(SavedStateHandle savedState,
      GetMemorizationSessionUseCase getSession, GetSurahsUseCase getSurahs,
      GetAyahByReferenceUseCase getAyah, ObserveAyahMemorizationProgressUseCase observeProgress,
      ObservePlaybackStateUseCase observePlayback, ObserveAudioSettingsUseCase observeAudioSettings,
      PlayAudioUseCase playAudio, UpdateMemorizationStatusUseCase setStatus,
      RateReviewUseCase rateReview, UpdateMemorizationSessionPositionUseCase updatePosition,
      CompleteMemorizationSessionUseCase completeSession,
      CompleteDailyGoalAyahUseCase completeDailyGoal, RecitationRecognizer recognizer) {
    return new HifdhSessionViewModel(savedState, getSession, getSurahs, getAyah, observeProgress, observePlayback, observeAudioSettings, playAudio, setStatus, rateReview, updatePosition, completeSession, completeDailyGoal, recognizer);
  }
}
