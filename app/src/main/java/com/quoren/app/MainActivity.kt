package com.quoren.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.quoren.app.core.designsystem.QuoranTheme
import com.quoren.app.domain.model.PlaybackState
import com.quoren.app.domain.model.ReaderSettings
import com.quoren.app.domain.model.ThemeMode
import com.quoren.app.domain.usecase.ObservePlaybackStateUseCase
import com.quoren.app.domain.usecase.ObserveReaderSettingsUseCase
import com.quoren.app.domain.usecase.UpdateReaderSettingsUseCase
import com.quoren.app.feature.audio.MiniPlayer
import com.quoren.app.navigation.QuoranNavHost
import com.quoren.app.navigation.ROUTE_HOME
import com.quoren.app.navigation.ROUTE_HIFDH
import com.quoren.app.navigation.ROUTE_MORE
import com.quoren.app.navigation.ROUTE_PRAYER
import com.quoren.app.navigation.ROUTE_QURAN
import com.quoren.app.navigation.ROUTE_PLAYER
import com.quoren.app.R
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    observeSettings: ObserveReaderSettingsUseCase,
    observePlayback: ObservePlaybackStateUseCase,
    private val updateSettings: UpdateReaderSettingsUseCase,
) : ViewModel() {
    val settings: StateFlow<ReaderSettings> =
        observeSettings().stateIn(viewModelScope, SharingStarted.Eagerly, ReaderSettings())
    val playback: StateFlow<PlaybackState> =
        observePlayback().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlaybackState())

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            updateSettings { current ->
                current.copy(themeMode = if (enabled) ThemeMode.DARK else ThemeMode.LIGHT)
            }
        }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            val playback by viewModel.playback.collectAsStateWithLifecycle()
            val nav = rememberNavController()
            val route = nav.currentBackStackEntryAsState().value?.destination?.route
            val showMini = playback.isActive && route != ROUTE_PLAYER
            val topLevelRoutes = setOf(ROUTE_HOME, ROUTE_QURAN, ROUTE_HIFDH, ROUTE_PRAYER, ROUTE_MORE)
            val isDarkTheme = settings.themeMode == ThemeMode.DARK ||
                (settings.themeMode == ThemeMode.SYSTEM && isSystemInDarkTheme())

            val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
            LaunchedEffect(playback.isActive) {
                if (playback.isActive && Build.VERSION.SDK_INT >= 33 &&
                    ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                ) {
                    notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            QuoranTheme(settings.themeMode) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(Modifier.fillMaxSize()) {
                        if (route in topLevelRoutes) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(R.string.app_name),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                Row(
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        text = stringResource(if (isDarkTheme) R.string.theme_dark else R.string.theme_light),
                                        style = MaterialTheme.typography.labelLarge,
                                    )
                                    Switch(checked = isDarkTheme, onCheckedChange = viewModel::setDarkTheme)
                                }
                            }
                        }
                        Box(
                            Modifier
                                .weight(1f)
                                .consumeWindowInsets(if (showMini) WindowInsets.navigationBars else WindowInsets(0)),
                        ) {
                            QuoranNavHost(nav)
                        }
                        if (showMini) MiniPlayer(onOpenPlayer = { nav.navigate(ROUTE_PLAYER) })
                        if (route in topLevelRoutes) {
                            NavigationBar {
                                val destinations = listOf(
                                    Triple(ROUTE_HOME, R.string.home_title, Icons.Filled.Home),
                                    Triple(ROUTE_QURAN, R.string.tab_surahs, Icons.Filled.Search),
                                    Triple(ROUTE_HIFDH, R.string.tab_memorization, Icons.Filled.Person),
                                    Triple(ROUTE_PRAYER, R.string.prayer_title, Icons.Filled.LocationOn),
                                    Triple(ROUTE_MORE, R.string.more_title, Icons.Filled.Settings),
                                )
                                destinations.forEach { (destination, title, icon) ->
                                    NavigationBarItem(
                                        selected = route == destination,
                                        onClick = {
                                            nav.navigate(destination) {
                                                popUpTo(ROUTE_HOME) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = { Icon(icon, contentDescription = null) },
                                        label = { Text(stringResource(title)) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
