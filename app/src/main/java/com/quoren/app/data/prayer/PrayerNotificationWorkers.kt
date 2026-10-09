package com.quoren.app.data.prayer

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.quoren.app.MainActivity
import com.quoren.app.R
import com.quoren.app.domain.model.PrayerName
import com.quoren.app.domain.prayer.PrayerTimeEngine
import com.quoren.app.domain.prayer.PrayerAlertPlanner
import com.quoren.app.domain.repository.PrayerConfiguration
import com.quoren.app.domain.repository.PrayerNotificationScheduler
import com.quoren.app.domain.usecase.ObservePrayerConfigurationUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

@Singleton
class PrayerNotificationSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val planner: PrayerAlertPlanner,
) : PrayerNotificationScheduler {

    private val workManager get() = WorkManager.getInstance(context)
    private val alarmManager get() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun ensureDailyRefresh() {
        val refresh = PeriodicWorkRequestBuilder<PrayerReminderRefreshWorker>(1, TimeUnit.DAYS)
            .addTag(REFRESH_TAG)
            .build()
        workManager.enqueueUniquePeriodicWork(REFRESH_NAME, ExistingPeriodicWorkPolicy.KEEP, refresh)
    }

    override suspend fun previewAdhan() {
        val request = OneTimeWorkRequestBuilder<PrayerNotificationWorker>()
            .setInputData(
                workDataOf(
                    PrayerNotificationWorker.KEY_PRAYER to PrayerName.MAGHRIB.name,
                    PrayerNotificationWorker.KEY_REMINDER_MINUTES to 0,
                    PrayerNotificationWorker.KEY_PLAY_ADHAN to true,
                ),
            )
            .build()
        workManager.enqueue(request)
    }

    override suspend fun schedule(configuration: PrayerConfiguration) {
        val now = Instant.now()
        val alerts = withContext(Dispatchers.Default) { planner.plan(configuration, now) }
        withContext(Dispatchers.IO) {
            workManager.cancelAllWorkByTag(REMINDER_TAG).result.get()
            cancelExactAlarms()
            val canScheduleExactly = Build.VERSION.SDK_INT < 31 || alarmManager.canScheduleExactAlarms()
            if (canScheduleExactly) {
                try {
                    alerts.forEach { alert -> scheduleExactAlert(alert, configuration, now) }
                } catch (_: SecurityException) {
                    cancelExactAlarms()
                    enqueueInexactFallback(alerts, now)
                }
            } else {
                enqueueInexactFallback(alerts, now)
            }
        }
    }

    private fun scheduleExactAlert(alert: com.quoren.app.domain.prayer.PrayerAlert, configuration: PrayerConfiguration, now: Instant) {
        val location = checkNotNull(configuration.location)
        val zone = runCatching { ZoneId.of(location.timeZoneId) }.getOrDefault(ZoneId.systemDefault())
        val dayOffset = ChronoUnit.DAYS.between(now.atZone(zone).toLocalDate(), alert.triggerAt.atZone(zone).toLocalDate()).toInt()
        val kind = when {
            alert.playAdhan -> 2
            alert.reminderMinutes > 0 -> 0
            else -> 1
        }
        val requestCode = (dayOffset * 32) + (alert.prayer.ordinal * 3) + kind
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, PrayerAlarmReceiver::class.java)
                .setAction(PrayerAlarmReceiver.ACTION_PRAYER_ALARM)
                .putExtra(PrayerAlarmReceiver.EXTRA_PRAYER, alert.prayer.name)
                .putExtra(PrayerAlarmReceiver.EXTRA_REMINDER_MINUTES, alert.reminderMinutes)
                .putExtra(PrayerAlarmReceiver.EXTRA_PLAY_ADHAN, alert.playAdhan),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alert.triggerAt.toEpochMilli(), pendingIntent)
    }

    private fun cancelExactAlarms() {
        repeat(PrayerAlarmReceiver.ALARM_REQUEST_COUNT) { requestCode ->
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                Intent(context, PrayerAlarmReceiver::class.java).setAction(PrayerAlarmReceiver.ACTION_PRAYER_ALARM),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
            )
            pendingIntent?.let(alarmManager::cancel)
        }
    }

    private fun enqueueInexactFallback(alerts: List<com.quoren.app.domain.prayer.PrayerAlert>, now: Instant) {
        val requests = alerts.map { alert ->
            reminderRequest(
                alert.prayer,
                Duration.between(now, alert.triggerAt).toMillis().coerceAtLeast(0L),
                alert.reminderMinutes,
                alert.playAdhan,
            )
        }
        if (requests.isNotEmpty()) workManager.enqueue(requests)
    }

    private fun reminderRequest(
        prayer: PrayerName,
        delayMillis: Long,
        reminderMinutes: Int,
        playAdhan: Boolean,
    ): OneTimeWorkRequest =
        OneTimeWorkRequestBuilder<PrayerNotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    PrayerNotificationWorker.KEY_PRAYER to prayer.name,
                    PrayerNotificationWorker.KEY_REMINDER_MINUTES to reminderMinutes,
                    PrayerNotificationWorker.KEY_PLAY_ADHAN to playAdhan,
                ),
            )
            .addTag(REMINDER_TAG)
            .build()

    companion object {
        private const val REFRESH_NAME = "prayer-reminder-refresh"
            private const val REFRESH_TAG = "prayer-reminder-refresh"
            private const val REMINDER_TAG = "prayer-reminder"
    }
}

@HiltWorker
class PrayerReminderRefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val observeConfiguration: ObservePrayerConfigurationUseCase,
    private val scheduler: PrayerNotificationScheduler,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = runCatching {
        scheduler.schedule(observeConfiguration().first())
        Result.success()
    }.getOrElse { Result.retry() }
}

class PrayerNotificationWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) return Result.success()

        val prayer = inputData.getString(KEY_PRAYER)?.let { runCatching { PrayerName.valueOf(it) }.getOrNull() }
            ?: return Result.failure()
        val prayerName = when (prayer) {
            PrayerName.FAJR -> applicationContext.getString(R.string.prayer_fajr)
            PrayerName.SUNRISE -> applicationContext.getString(R.string.prayer_sunrise)
            PrayerName.DHUHR -> applicationContext.getString(R.string.prayer_dhuhr)
            PrayerName.ASR -> applicationContext.getString(R.string.prayer_asr)
            PrayerName.MAGHRIB -> applicationContext.getString(R.string.prayer_maghrib)
            PrayerName.ISHA -> applicationContext.getString(R.string.prayer_isha)
        }
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                applicationContext.getString(R.string.prayer_notification_channel),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply { setSound(null, null) }
            manager.createNotificationChannel(channel)
        }
        val launchIntent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_REQUEST_CODE,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val reminderMinutes = inputData.getInt(KEY_REMINDER_MINUTES, 0)
        val notificationText = if (reminderMinutes > 0) {
            applicationContext.getString(R.string.prayer_notification_reminder_body, reminderMinutes)
        } else {
            applicationContext.getString(R.string.prayer_notification_body)
        }
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(applicationContext.getString(R.string.prayer_notification_title, prayerName))
            .setContentText(notificationText)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        if (inputData.getBoolean(KEY_PLAY_ADHAN, false)) {
            val foregroundNotification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(applicationContext.getString(R.string.prayer_notification_title, prayerName))
                .setContentText(applicationContext.getString(R.string.prayer_adhan_playing))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build()
            playSelectedAdhan(prayer, foregroundNotification)
        }
        @Suppress("MissingPermission")
        NotificationManagerCompat.from(applicationContext).notify(prayer.ordinal + NOTIFICATION_ID_OFFSET, notification)
        return Result.success()
    }

    private suspend fun playSelectedAdhan(prayer: PrayerName, notification: Notification) {
        val notificationId = prayer.ordinal + NOTIFICATION_ID_OFFSET
        val foregroundInfo = if (Build.VERSION.SDK_INT >= 29) {
            ForegroundInfo(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            ForegroundInfo(notificationId, notification)
        }
        setForeground(foregroundInfo)

        val trackSelector = DefaultTrackSelector(applicationContext).apply {
            setParameters(buildUponParameters().setTrackTypeDisabled(C.TRACK_TYPE_VIDEO, true))
        }
        val player = withContext(Dispatchers.Main.immediate) {
            ExoPlayer.Builder(applicationContext).setTrackSelector(trackSelector).build().also {
                val attributes = AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
                    .build()
                it.setAudioAttributes(attributes, true)
            }
        }
        try {
            withContext(Dispatchers.Main.immediate) {
                suspendCancellableCoroutine { continuation ->
                    val listener = object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            if (playbackState == Player.STATE_ENDED && continuation.isActive) continuation.resume(Unit)
                        }

                        override fun onPlayerError(error: PlaybackException) {
                            if (continuation.isActive) continuation.resume(Unit)
                        }
                    }
                    player.addListener(listener)
                    continuation.invokeOnCancellation { player.removeListener(listener) }
                    player.setMediaItem(MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(R.raw.adhan_other)))
                    player.prepare()
                    player.play()
                }
            }
        } finally {
            withContext(Dispatchers.Main.immediate) { player.release() }
        }
    }

    companion object {
        const val KEY_PRAYER = "prayer_name"
        const val KEY_REMINDER_MINUTES = "reminder_minutes"
        const val KEY_PLAY_ADHAN = "play_adhan"
        private const val NOTIFICATION_ID_OFFSET = 4800
        private const val CHANNEL_ID = "prayer_times"
        private const val NOTIFICATION_REQUEST_CODE = 4812
    }
}