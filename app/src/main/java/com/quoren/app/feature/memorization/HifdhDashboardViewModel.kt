package com.quoren.app.feature.memorization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quoren.app.domain.model.DailyGoal
import com.quoren.app.domain.model.MemorizationCounts
import com.quoren.app.domain.model.MemorizationMode
import com.quoren.app.domain.model.MemorizationProgress
import com.quoren.app.domain.model.MemorizationSession
import com.quoren.app.domain.model.RepeatCount
import com.quoren.app.domain.model.Reciter
import com.quoren.app.domain.model.Surah
import com.quoren.app.domain.usecase.CreateMemorizationSessionUseCase
import com.quoren.app.domain.usecase.GenerateDueReviewsUseCase
import com.quoren.app.domain.usecase.GetAyahUseCase
import com.quoren.app.domain.usecase.GetRecitersUseCase
import com.quoren.app.domain.usecase.GetSurahsUseCase
import com.quoren.app.domain.usecase.ObserveDailyGoalUseCase
import com.quoren.app.domain.usecase.ObserveMemorizationCountsUseCase
import com.quoren.app.domain.usecase.ObserveMemorizationProgressUseCase
import com.quoren.app.domain.usecase.ObserveMemorizationSessionsUseCase
import com.quoren.app.domain.usecase.ObserveDefaultReciterUseCase
import com.quoren.app.domain.usecase.UpdateDailyGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private data class HifdhCatalog(val surahs: List<Surah> = emptyList(), val reciters: List<Reciter> = emptyList())

data class HifdhDashboardState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val surahs: List<Surah> = emptyList(),
    val reciters: List<Reciter> = emptyList(),
    val sessions: List<MemorizationSession> = emptyList(),
    val progress: List<MemorizationProgress> = emptyList(),
    val counts: MemorizationCounts = MemorizationCounts(),
    val dailyGoal: DailyGoal = DailyGoal(3, 0, LocalDate.now()),
)

@HiltViewModel
class HifdhDashboardViewModel @Inject constructor(
    observeSessions: ObserveMemorizationSessionsUseCase,
    observeProgress: ObserveMemorizationProgressUseCase,
    observeCounts: ObserveMemorizationCountsUseCase,
    observeGoal: ObserveDailyGoalUseCase,
    getSurahs: GetSurahsUseCase,
    getReciters: GetRecitersUseCase,
    private val defaultReciter: ObserveDefaultReciterUseCase,
    private val createSession: CreateMemorizationSessionUseCase,
    private val generateDue: GenerateDueReviewsUseCase,
    private val getAyah: GetAyahUseCase,
    private val updateGoal: UpdateDailyGoalUseCase,
) : ViewModel() {

    private val catalog = MutableStateFlow(HifdhCatalog())
    private val _events = Channel<HifdhDashboardEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    val state: StateFlow<HifdhDashboardState> = combine(
        observeSessions(),
        observeProgress(),
        observeCounts(LocalDate.now()),
        observeGoal(),
        catalog,
    ) { sessions, progress, counts, goal, available ->
        HifdhDashboardState(
            loading = available.surahs.isEmpty(),
            surahs = available.surahs,
            reciters = available.reciters,
            sessions = sessions,
            progress = progress,
            counts = counts,
            dailyGoal = goal,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HifdhDashboardState())

    init {
        viewModelScope.launch {
            runCatching { catalog.value = HifdhCatalog(getSurahs(), getReciters()) }
                .onFailure { _events.send(HifdhDashboardEvent.Error(it.message.orEmpty())) }
        }
    }

    fun create(
        surahId: Int,
        startAyah: Int,
        endAyah: Int,
        reciterId: String,
        repeatCount: RepeatCount,
        mode: MemorizationMode,
    ) {
        viewModelScope.launch {
            runCatching { createSession(surahId, startAyah, endAyah, reciterId, repeatCount, mode) }
                .onSuccess { _events.send(HifdhDashboardEvent.OpenSession(it.sessionId)) }
                .onFailure { _events.send(HifdhDashboardEvent.Error(it.message.orEmpty())) }
        }
    }

    fun startReview() {
        viewModelScope.launch {
            runCatching {
                val due = generateDue(LocalDate.now()).first()
                val first = due.firstOrNull() ?: return@launch _events.send(HifdhDashboardEvent.NoReviewsDue)
                val ayah = getAyah(first.ayahId) ?: return@launch _events.send(HifdhDashboardEvent.NoReviewsDue)
                val reciter = defaultReciter().first()
                createSession(
                    ayah.surahId,
                    ayah.ayahNumber,
                    ayah.ayahNumber,
                    reciter.id,
                    RepeatCount.THREE,
                    MemorizationMode.LISTEN_RECALL,
                )
            }.onSuccess { _events.send(HifdhDashboardEvent.OpenSession(it.sessionId)) }
                .onFailure { _events.send(HifdhDashboardEvent.Error(it.message.orEmpty())) }
        }
    }

    fun setDailyGoal(target: Int) {
        viewModelScope.launch { runCatching { updateGoal(target) } }
    }
}

sealed interface HifdhDashboardEvent {
    data class OpenSession(val sessionId: String) : HifdhDashboardEvent
    data class Error(val message: String) : HifdhDashboardEvent
    data object NoReviewsDue : HifdhDashboardEvent
}
