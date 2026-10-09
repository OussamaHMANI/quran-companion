package com.quoren.app.feature.quran

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quoren.app.domain.model.ReadingProgress
import com.quoren.app.domain.model.Surah
import com.quoren.app.domain.usecase.GetJuzStartsUseCase
import com.quoren.app.domain.usecase.GetSurahsUseCase
import com.quoren.app.domain.usecase.ObserveReadingProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JuzItem(val juz: Int, val surahName: String, val ayahNumber: Int, val ayahId: Int, val page: Int)

data class ContinueReading(val surahName: String, val progress: ReadingProgress)

data class QuranIndexUiState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val surahs: List<Surah> = emptyList(),
    val juz: List<JuzItem> = emptyList(),
    val readingProgress: ReadingProgress? = null,
    val continueReading: ContinueReading? = null,
)

@HiltViewModel
class QuranIndexViewModel @Inject constructor(
    private val getSurahs: GetSurahsUseCase,
    private val getJuzStarts: GetJuzStartsUseCase,
    private val observeProgress: ObserveReadingProgressUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(QuranIndexUiState())
    val state: StateFlow<QuranIndexUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeProgress().collect { progress ->
                _state.update { current ->
                    val surahName = current.surahs.firstOrNull { it.id == progress?.surahId }?.nameArabic.orEmpty()
                    current.copy(
                        readingProgress = progress,
                        continueReading = progress?.let { ContinueReading(surahName, it) },
                    )
                }
            }
        }
    }

    fun load() {
        if (_state.value.loading || _state.value.surahs.isNotEmpty()) return
        _state.update { it.copy(loading = true, error = false) }
        viewModelScope.launch {
            try {
                val surahs = getSurahs()
                val byId = surahs.associateBy { it.id }
                val juz = getJuzStarts().map {
                    JuzItem(it.juz, byId.getValue(it.surahId).nameArabic, it.ayahNumber, it.ayahId, it.page)
                }
                _state.update { current ->
                    val surahName = current.readingProgress?.let { byId[it.surahId]?.nameArabic }.orEmpty()
                    current.copy(
                        loading = false,
                        surahs = surahs,
                        juz = juz,
                        continueReading = current.readingProgress?.let { ContinueReading(surahName, it) },
                    )
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = true) }
            }
        }
    }
}
