package com.quoren.app.feature.audio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quoren.app.domain.model.DownloadedAudio
import com.quoren.app.domain.model.Reciter
import com.quoren.app.domain.model.Surah
import com.quoren.app.domain.usecase.GetRecitersUseCase
import com.quoren.app.domain.usecase.GetSurahsUseCase
import com.quoren.app.domain.usecase.ManageDownloadsUseCase
import com.quoren.app.domain.usecase.ObserveDefaultReciterUseCase
import com.quoren.app.domain.usecase.ObserveDownloadsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DownloadItemUi(val download: DownloadedAudio, val surahName: String, val reciterName: String)

data class DownloadsUiState(
    val items: List<DownloadItemUi> = emptyList(),
    val availableSurahs: List<Surah> = emptyList(),
    val totalBytes: Long = 0,
)

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    observeDownloads: ObserveDownloadsUseCase,
    observeDefaultReciter: ObserveDefaultReciterUseCase,
    getSurahs: GetSurahsUseCase,
    getReciters: GetRecitersUseCase,
    private val manage: ManageDownloadsUseCase,
) : ViewModel() {

    private val surahs = MutableStateFlow<List<Surah>>(emptyList())
    private val reciters = MutableStateFlow<List<Reciter>>(emptyList())

    val state: StateFlow<DownloadsUiState> =
        combine(observeDownloads(), observeDefaultReciter(), surahs, reciters) { downloads, default, allSurahs, allReciters ->
            val surahNames = allSurahs.associate { it.id to it.nameArabic }
            val reciterNames = allReciters.associate { it.id to it.arabicName }
            val taken = downloads.filter { it.reciterId == default.id }.map { it.surahId }.toSet()
            DownloadsUiState(
                items = downloads.map { DownloadItemUi(it, surahNames[it.surahId].orEmpty(), reciterNames[it.reciterId].orEmpty()) },
                availableSurahs = allSurahs.filter { it.id !in taken },
                totalBytes = downloads.sumOf { it.fileSize },
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DownloadsUiState())

    init {
        viewModelScope.launch {
            runCatching {
                surahs.value = getSurahs()
                reciters.value = getReciters()
            }
        }
    }

    fun onDownload(surahId: Int) = run { manage.downloadForDefaultReciter(surahId) }
    fun onPause(item: DownloadedAudio) = run { manage.pause(item) }
    fun onResume(item: DownloadedAudio) = run { manage.resume(item) }
    fun onRetry(item: DownloadedAudio) = run { manage.retry(item) }
    fun onDelete(item: DownloadedAudio) = run { manage.delete(item) }

    private fun run(block: suspend () -> Unit) {
        viewModelScope.launch { runCatching { block() } }
    }
}
