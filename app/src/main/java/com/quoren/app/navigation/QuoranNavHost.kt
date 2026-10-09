package com.quoren.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.quoren.app.feature.audio.AudioSettingsScreen
import com.quoren.app.feature.audio.DownloadsScreen
import com.quoren.app.feature.audio.FullPlayerScreen
import com.quoren.app.feature.memorization.HifdhDashboardScreen
import com.quoren.app.feature.memorization.HifdhSessionScreen
import com.quoren.app.feature.prayer.PrayerScreen
import com.quoren.app.feature.qibla.QiblaScreen
import com.quoren.app.feature.mosque.MosqueScreen
import com.quoren.app.feature.quran.MoreScreen
import com.quoren.app.feature.quran.QuranIndexScreen
import com.quoren.app.feature.reader.ReaderScreen
import com.quoren.app.feature.search.SearchScreen

const val ROUTE_PLAYER = "player"
const val ROUTE_HOME = "home"
const val ROUTE_QURAN = "quran"
const val ROUTE_MORE = "more"
private const val ROUTE_SEARCH = "search"
private const val ROUTE_DOWNLOADS = "downloads"
private const val ROUTE_AUDIO_SETTINGS = "audio_settings"
const val ROUTE_HIFDH = "memorization"
const val ROUTE_PRAYER = "prayer"
private const val ROUTE_QIBLA = "qibla"
private const val ROUTE_MOSQUES = "mosques"
private const val ROUTE_HIFDH_SESSION = "memorization/session/{sessionId}"
private const val ROUTE_READER = "reader?page={page}&ayah={ayah}"

private fun readerRoute(page: Int, ayahId: Int) = "reader?page=$page&ayah=$ayahId"

@Composable
fun QuoranNavHost(nav: NavHostController) {
    NavHost(navController = nav, startDestination = ROUTE_HOME) {
        composable(ROUTE_HOME) {
            QuranIndexScreen(
                initialTab = 0,
                onOpenReader = { page, ayahId -> nav.navigate(readerRoute(page, ayahId)) },
                onOpenSearch = { nav.navigate(ROUTE_SEARCH) },
                onOpenDownloads = { nav.navigate(ROUTE_DOWNLOADS) },
                onOpenAudioSettings = { nav.navigate(ROUTE_AUDIO_SETTINGS) },
                onOpenMemorization = { nav.navigate(ROUTE_HIFDH) },
                onOpenPrayer = { nav.navigate(ROUTE_PRAYER) },
                onOpenQibla = { nav.navigate(ROUTE_QIBLA) },
                onOpenMosques = { nav.navigate(ROUTE_MOSQUES) },
            )
        }
        composable(ROUTE_QURAN) {
            QuranIndexScreen(
                initialTab = 1,
                onOpenReader = { page, ayahId -> nav.navigate(readerRoute(page, ayahId)) },
                onOpenSearch = { nav.navigate(ROUTE_SEARCH) },
                onOpenDownloads = { nav.navigate(ROUTE_DOWNLOADS) },
                onOpenAudioSettings = { nav.navigate(ROUTE_AUDIO_SETTINGS) },
                onOpenMemorization = { nav.navigate(ROUTE_HIFDH) },
                onOpenPrayer = { nav.navigate(ROUTE_PRAYER) },
                onOpenQibla = { nav.navigate(ROUTE_QIBLA) },
                onOpenMosques = { nav.navigate(ROUTE_MOSQUES) },
            )
        }
        composable(ROUTE_MORE) {
            MoreScreen(
                onOpenSearch = { nav.navigate(ROUTE_SEARCH) },
                onOpenDownloads = { nav.navigate(ROUTE_DOWNLOADS) },
                onOpenAudioSettings = { nav.navigate(ROUTE_AUDIO_SETTINGS) },
                onOpenQibla = { nav.navigate(ROUTE_QIBLA) },
                onOpenMosques = { nav.navigate(ROUTE_MOSQUES) },
            )
        }
        composable(ROUTE_HIFDH) {
            HifdhDashboardScreen(onOpenSession = { sessionId -> nav.navigate("memorization/session/$sessionId") })
        }
        composable(ROUTE_PRAYER) { PrayerScreen(onBack = { nav.popBackStack() }) }
                composable(ROUTE_QIBLA) {
                    QiblaScreen(onBack = { nav.popBackStack() }, onOpenPrayerSettings = { nav.navigate(ROUTE_PRAYER) })
                }
                composable(ROUTE_MOSQUES) {
                    MosqueScreen(onBack = { nav.popBackStack() }, onOpenPrayerSettings = { nav.navigate(ROUTE_PRAYER) })
                }
        composable(
            ROUTE_HIFDH_SESSION,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType }),
        ) {
            HifdhSessionScreen(onBack = { nav.popBackStack() })
        }
        composable(ROUTE_SEARCH) {
            SearchScreen(
                onBack = { nav.popBackStack() },
                onOpenAyah = { page, ayahId -> nav.navigate(readerRoute(page, ayahId)) },
            )
        }
        composable(ROUTE_DOWNLOADS) { DownloadsScreen(onBack = { nav.popBackStack() }) }
        composable(ROUTE_AUDIO_SETTINGS) { AudioSettingsScreen(onBack = { nav.popBackStack() }) }
        composable(ROUTE_PLAYER) { FullPlayerScreen(onBack = { nav.popBackStack() }) }
        composable(
            ROUTE_READER,
            arguments = listOf(
                navArgument("page") { type = NavType.IntType; defaultValue = 1 },
                navArgument("ayah") { type = NavType.IntType; defaultValue = 0 },
            ),
        ) {
            ReaderScreen(onBack = { nav.popBackStack() })
        }
    }
}
