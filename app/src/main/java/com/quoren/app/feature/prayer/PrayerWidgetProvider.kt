package com.quoren.app.feature.prayer

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.quoren.app.MainActivity
import com.quoren.app.R
import com.quoren.app.data.repository.PrayerRepositoryImpl
import com.quoren.app.domain.model.PrayerName
import com.quoren.app.domain.prayer.PrayerTimeEngine
import java.time.Instant
import java.time.ZoneId
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first

class PrayerWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch { updatePrayerWidgets(context, manager, appWidgetIds) }
    }

    override fun onEnabled(context: Context) {
        val request = PeriodicWorkRequestBuilder<PrayerWidgetRefreshWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(REFRESH_WORK, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    override fun onDisabled(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(REFRESH_WORK)
    }

    companion object {
        private const val REFRESH_WORK = "prayer-widget-refresh"
    }
}

class PrayerWidgetRefreshWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = runCatching {
        val manager = AppWidgetManager.getInstance(applicationContext)
        val component = ComponentName(applicationContext, PrayerWidgetProvider::class.java)
        updatePrayerWidgets(applicationContext, manager, manager.getAppWidgetIds(component))
        Result.success()
    }.getOrElse { Result.retry() }
}

suspend fun updatePrayerWidgets(context: Context, manager: AppWidgetManager, ids: IntArray) = withContext(Dispatchers.IO) {
    if (ids.isEmpty()) return@withContext
    val configuration = PrayerRepositoryImpl(context).observeConfiguration().first()
    val location = configuration.location
    val views = RemoteViews(context.packageName, R.layout.widget_prayer).apply {
        val openApp = PendingIntent.getActivity(
            context,
            8105,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        setOnClickPendingIntent(R.id.widget_prayer_root, openApp)
        if (location == null) {
            setTextViewText(R.id.widget_prayer_name, context.getString(R.string.prayer_location_not_set))
            setTextViewText(R.id.widget_prayer_countdown, context.getString(R.string.home_set_prayer_location))
            setTextViewText(R.id.widget_prayer_city, context.getString(R.string.prayer_set_location))
        } else {
            val zone = runCatching { ZoneId.of(location.timeZoneId) }.getOrDefault(ZoneId.systemDefault())
            val now = Instant.now()
            val schedule = PrayerTimeEngine().calculate(location, now.atZone(zone).toLocalDate(), configuration.settings)
            val next = PrayerTimeEngine().nextPrayer(schedule, now)
            val name = when (next.prayer.name) {
                PrayerName.FAJR -> R.string.prayer_fajr
                PrayerName.SUNRISE -> R.string.prayer_sunrise
                PrayerName.DHUHR -> R.string.prayer_dhuhr
                PrayerName.ASR -> R.string.prayer_asr
                PrayerName.MAGHRIB -> R.string.prayer_maghrib
                PrayerName.ISHA -> R.string.prayer_isha
            }
            val remaining = next.remainingSeconds.coerceAtLeast(0L)
            val countdown = String.format(Locale.ROOT, "%02d:%02d:%02d", remaining / 3600, remaining % 3600 / 60, remaining % 60)
            setTextViewText(R.id.widget_prayer_name, context.getString(name))
            setChronometer(R.id.widget_prayer_countdown, SystemClock.elapsedRealtime() + remaining * 1_000L, null, true)
            setChronometerCountDown(R.id.widget_prayer_countdown, true)
            setTextViewText(R.id.widget_prayer_city, location.city)
            setTextViewText(R.id.widget_prayer_time, java.time.format.DateTimeFormatter.ofPattern("HH:mm").withZone(zone).format(next.startsAt))
        }
    }
    ids.forEach { manager.updateAppWidget(it, views) }
}