package com.quoren.app.feature.memorization

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quoren.app.R
import com.quoren.app.core.designsystem.QuranFontFamily
import com.quoren.app.domain.model.MemorizationMode
import com.quoren.app.domain.model.RepeatCount
import com.quoren.app.domain.model.Surah
import kotlinx.coroutines.flow.collectLatest

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HifdhDashboardScreen(
    onOpenSession: (String) -> Unit,
    viewModel: HifdhDashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showCreate by rememberSaveable { mutableStateOf(false) }
    var notice by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is HifdhDashboardEvent.OpenSession -> onOpenSession(event.sessionId)
                HifdhDashboardEvent.NoReviewsDue -> notice = R.string.hifdh_no_reviews
                is HifdhDashboardEvent.Error -> notice = R.string.load_error
            }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.hifdh_title)) }) }) { padding ->
        when {
            state.loading -> Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            else -> Column(Modifier.padding(padding).fillMaxSize()) {
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Card(
                        onClick = viewModel::startReview,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 82.dp),
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(stringResource(R.string.hifdh_start_review), style = MaterialTheme.typography.titleMedium)
                                Text(
                                    stringResource(R.string.hifdh_due_count, state.counts.dueCount),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    }
                    Button(onClick = { showCreate = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Text(stringResource(R.string.hifdh_new_session), Modifier.padding(start = 8.dp))
                    }
                    Text(stringResource(R.string.hifdh_daily_goal), style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(1, 3, 5, 10).forEach { goal ->
                            FilterChip(
                                selected = state.dailyGoal.ayahTarget == goal,
                                onClick = { viewModel.setDailyGoal(goal) },
                                label = { Text(goal.toString()) },
                            )
                        }
                    }
                    Text(
                        stringResource(R.string.hifdh_goal_progress, state.dailyGoal.ayahsCompleted, state.dailyGoal.ayahTarget),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(stringResource(R.string.hifdh_statistics), style = MaterialTheme.typography.titleMedium)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(stringResource(R.string.hifdh_memorized_count, state.counts.memorizedCount))
                        Text(stringResource(R.string.hifdh_learning_count, state.counts.learningCount))
                        Text(stringResource(R.string.hifdh_weak_count, state.counts.weakCount))
                    }
                    Text(stringResource(R.string.hifdh_continue), style = MaterialTheme.typography.titleMedium)
                    val names = state.surahs.associate { it.id to it.nameArabic }
                    if (state.sessions.isEmpty()) {
                        Text(stringResource(R.string.hifdh_no_sessions), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        state.sessions.forEach { session ->
                            Card(
                                onClick = { onOpenSession(session.sessionId) },
                                modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
                            ) {
                                Row(
                                    Modifier.fillMaxWidth().padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(names[session.surahId].orEmpty(), fontFamily = QuranFontFamily, style = MaterialTheme.typography.titleMedium)
                                        Text(
                                            "${stringResource(R.string.ayah_count, session.endAyah - session.startAyah + 1)} · ${session.currentAyah}",
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    }
                                    Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(R.string.hifdh_continue))
                                }
                            }
                        }
                    }
                    notice?.let { Text(stringResource(it), color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }

    if (showCreate) {
        CreateSessionDialog(
            surahs = state.surahs,
            reciters = state.reciters,
            onDismiss = { showCreate = false },
            onCreate = { surah, start, end, reciterId, repeats, mode ->
                viewModel.create(surah.id, start, end, reciterId, repeats, mode)
                showCreate = false
            },
        )
    }
}

@Composable
private fun CreateSessionDialog(
    surahs: List<Surah>,
    reciters: List<com.quoren.app.domain.model.Reciter>,
    onDismiss: () -> Unit,
    onCreate: (Surah, Int, Int, String, RepeatCount, MemorizationMode) -> Unit,
) {
    var surah by remember { mutableStateOf(surahs.firstOrNull()) }
    var startText by remember { mutableStateOf("1") }
    var endText by remember { mutableStateOf("5") }
    var reciterId by remember { mutableStateOf(reciters.firstOrNull()?.id.orEmpty()) }
    var repeat by remember { mutableStateOf(RepeatCount.THREE) }
    var mode by remember { mutableStateOf(MemorizationMode.VOICE_RECALL) }
    var menu by remember { mutableStateOf(0) }
    val start = startText.toIntOrNull()
    val end = endText.toIntOrNull()
    val valid = surah != null && reciterId.isNotBlank() && start != null && end != null &&
        start in 1..(surah?.numberOfAyahs ?: 0) && end in start..(surah?.numberOfAyahs ?: 0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.hifdh_new_session)) },
        text = {
            Column(
                Modifier.widthIn(max = 480.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DropdownChoice(
                    label = stringResource(R.string.hifdh_surah),
                    value = surah?.nameArabic.orEmpty(),
                    expanded = menu == 1,
                    onExpand = { menu = 1 },
                    onDismiss = { menu = 0 },
                    options = surahs.map { it.nameArabic },
                    onSelect = { selected -> surah = surahs.firstOrNull { it.nameArabic == selected }; endText = minOf(endText.toIntOrNull() ?: 5, surah?.numberOfAyahs ?: 5).toString(); menu = 0 },
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startText,
                        onValueChange = { startText = it.filter(Char::isDigit).take(3) },
                        label = { Text(stringResource(R.string.hifdh_start_ayah)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = endText,
                        onValueChange = { endText = it.filter(Char::isDigit).take(3) },
                        label = { Text(stringResource(R.string.hifdh_end_ayah)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                }
                DropdownChoice(
                    label = stringResource(R.string.hifdh_reciter),
                    value = reciters.firstOrNull { it.id == reciterId }?.arabicName.orEmpty(),
                    expanded = menu == 2,
                    onExpand = { menu = 2 },
                    onDismiss = { menu = 0 },
                    options = reciters.map { it.arabicName },
                    onSelect = { selected -> reciterId = reciters.firstOrNull { it.arabicName == selected }?.id.orEmpty(); menu = 0 },
                )
                Text(stringResource(R.string.hifdh_repeat_count), style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    RepeatCount.entries.filter { it != RepeatCount.UNLIMITED }.forEach { value ->
                        FilterChip(selected = repeat == value, onClick = { repeat = value }, label = { Text(value.times.toString()) })
                    }
                }
                Text(stringResource(R.string.hifdh_mode), style = MaterialTheme.typography.titleSmall)
                MemorizationMode.entries.forEach { value ->
                    FilterChip(
                        selected = mode == value,
                        onClick = { mode = value },
                        label = { Text(stringResource(value.labelRes())) },
                    )
                }
                if (!valid) Text(stringResource(R.string.hifdh_invalid_range), color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            Button(
                onClick = { if (valid) onCreate(surah!!, start!!, end!!, reciterId, repeat, mode) },
                enabled = valid,
            ) { Text(stringResource(R.string.hifdh_new_session)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_close)) } },
    )
}

@Composable
private fun DropdownChoice(
    label: String,
    value: String,
    expanded: Boolean,
    onExpand: () -> Unit,
    onDismiss: () -> Unit,
    options: List<String>,
    onSelect: (String) -> Unit,
) {
    Box {
        TextButton(onClick = onExpand, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
            Column(Modifier.fillMaxWidth()) {
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.bodyLarge)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
            options.forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { onSelect(option) }) }
        }
    }
}

private fun MemorizationMode.labelRes(): Int = when (this) {
    MemorizationMode.READ_LISTEN -> R.string.hifdh_mode_read_listen
    MemorizationMode.LISTEN_RECALL -> R.string.hifdh_mode_listen_recall
    MemorizationMode.VOICE_RECALL -> R.string.hifdh_mode_voice_recall
    MemorizationMode.PROGRESSIVE_HIDING -> R.string.hifdh_mode_progressive
    MemorizationMode.GROUP -> R.string.hifdh_mode_group
}
