package com.quoren.app.feature.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quoren.app.domain.model.BookmarkedAyah
import com.quoren.app.domain.usecase.ObserveBookmarksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(observeBookmarks: ObserveBookmarksUseCase) : ViewModel() {
    val bookmarks: StateFlow<List<BookmarkedAyah>?> = observeBookmarks()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
