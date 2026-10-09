package com.quoren.app.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quoren.app.domain.model.SearchResult
import com.quoren.app.domain.usecase.SearchQuranUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val searching: Boolean = false,
    val results: List<SearchResult> = emptyList(),
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(private val search: SearchQuranUseCase) : ViewModel() {

    private val query = MutableStateFlow("")

    val state: StateFlow<SearchUiState> = query
        .debounce(250)
        .distinctUntilChanged()
        .flatMapLatest { q ->
            flow {
                emit(SearchUiState(q, searching = true))
                emit(SearchUiState(q, results = search(q)))
            }.catch { emit(SearchUiState(q)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUiState())

    val currentQuery: StateFlow<String> get() = query

    fun onQueryChanged(value: String) {
        query.value = value
    }
}
