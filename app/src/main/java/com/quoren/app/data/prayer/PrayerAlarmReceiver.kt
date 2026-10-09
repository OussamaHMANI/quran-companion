package com.quoren.app.data.prayer

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.quoren.app.MainActivity
import com.quoren.app.R
import com.quoren.app.domain.model.PrayerName

class PrayerAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prayer = intent.getStringExtra(EXTRA_PRAYER)?.let { runCatching { PrayerName.valueOf(it) }.getOrNull() } ?: return
        val playAdhan = intent.getBooleanExtra(EXTRA_PLAY_ADHAN, false)
        val reminderMinutes = intent.getIntExtra(EXTRA_REMINDER_MINUTES, 0)
        if (playAdhan) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, PrayerAdhanService::class.java)
                    .putExtra(EXTRA_PRAYER, prayer.name),
            )
        } else {
            postPrayerNotification(context, prayer, reminderMinutes)
        }
    }

    private fun postPrayerNotification(context: Context, prayer: PrayerName, reminderMinutes: Int) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return
        val name = prayer.localizedName(context)
        val notification = prayerNotification(context, name, reminderMinutes, ongoing = false)
        @Suppress("MissingPermission")
        NotificationManagerCompat.from(context).notify(prayer.ordinal + NOTIFICATION_ID_OFFSET, notification)
    }

    companion object {
        const val ACTION_PRAYER_ALARM = "com.quoren.app.action.PRAYER_ALARM"
        const val EXTRA_PRAYER = "prayer_name"
        const val EXTRA_REMINDER_MINUTES = "reminder_minutes"
        const val EXTRA_PLAY_ADHAN = "play_adhan"
        const val NOTIFICATION_ID_OFFSET = 4800
        const val NOTIFICATION_CHANNEL_ID = "prayer_times"
        const val ALARM_REQUEST_COUNT = 448
    }
}

class PrayerAdhanService : android.app.Service() {

    private var player: androidx.media3.exoplayer.ExoPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }
        val prayer = intent?.getStringExtra(PrayerAlarmReceiver.EXTRA_PRAYER)
            ?.let { runCatching { PrayerName.valueOf(it) }.getOrNull() }
        if (prayer == null) {
            stopSelf()
            return START_NOT_STICKY
        }
        createNotificationChannel()
        val prayerName = prayer.localizedName(this)
        val notification = prayerNotification(this, prayerName, 0, ongoing = true)
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(
                prayer.ordinal + PrayerAlarmReceiver.NOTIFICATION_ID_OFFSET,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK,
            )
        } else {
            startForeground(prayer.ordinal + PrayerAlarmReceiver.NOTIFICATION_ID_OFFSET, notification)
        }
        playAdhan()
        return START_NOT_STICKY
    }

    private fun playAdhan() {
        player?.release()
        player = androidx.media3.exoplayer.ExoPlayer.Builder(this).build().also { exo ->
            val attributes = androidx.media3.common.AudioAttributes.Builder()
                .setUsage(androidx.media3.common.C.USAGE_ALARM)
                .setContentType(androidx.media3.common.C.AUDIO_CONTENT_TYPE_SPEECH)
                .build()
            exo.setAudioAttributes(attributes, true)
            exo.addListener(object : androidx.media3.common.Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == androidx.media3.common.Player.STATE_ENDED) stopSelf()
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    stopSelf()
                }
            })
            exo.setMediaItem(
                androidx.media3.common.MediaItem.fromUri(
                    androidx.media3.datasource.RawResourceDataSource.buildRawResourceUri(R.raw.adhan_other),
                ),
            )
            exo.prepare()
            exo.play()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < 26) return
        val channel = NotificationChannel(
            PrayerAlarmReceiver.NOTIFICATION_CHANNEL_ID,
            getString(R.string.prayer_notification_channel),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply { setSound(null, null) }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun onDestroy() {
        player?.release()
        player = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): android.os.IBinder? = null

    companion object {
        const val ACTION_STOP = "com.quoren.app.action.STOP_ADHAN"
    }
}

private fun PrayerName.localizedName(context: Context): String = context.getString(
    when (this) {
        PrayerName.FAJR -> R.string.prayer_fajr
        PrayerName.SUNRISE -> R.string.prayer_sunrise
        PrayerName.DHUHR -> R.string.prayer_dhuhr
        PrayerName.ASR -> R.string.prayer_asr
        PrayerName.MAGHRIB -> R.string.prayer_maghrib
        PrayerName.ISHA -> R.string.prayer_isha
    },
)

private fun prayerNotification(
    context: Context,
    prayerName: String,
    reminderMinutes: Int,
    ongoing: Boolean,
): android.app.Notification {
    val launchIntent = Intent(context, MainActivity::class.java)
    val openApp = PendingIntent.getActivity(
        context,
        4812,
        launchIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
    val message = if (reminderMinutes > 0) {
        context.getString(R.string.prayer_notification_reminder_body, reminderMinutes)
    } else if (ongoing) {
        context.getString(R.string.prayer_adhan_playing)
    } else {
        context.getString(R.string.prayer_notification_body)
    }
    val builder = NotificationCompat.Builder(context, PrayerAlarmReceiver.NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(context.getString(R.string.prayer_notification_title, prayerName))
        .setContentText(message)
        .setContentIntent(openApp)
        .setCategory(if (ongoing) NotificationCompat.CATEGORY_ALARM else NotificationCompat.CATEGORY_REMINDER)
        .setOngoing(ongoing)
        .setAutoCancel(!ongoing)
    if (ongoing) {
        val stopIntent = PendingIntent.getService(
            context,
            4813,
            Intent(context, PrayerAdhanService::class.java).setAction(PrayerAdhanService.ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        builder.addAction(0, context.getString(R.string.prayer_stop_adhan), stopIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
    }
    return builder.build()
}

class PrayerScheduleRefreshReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in REFRESH_ACTIONS) return
        val refresh = OneTimeWorkRequestBuilder<PrayerReminderRefreshWorker>()
            .addTag(REFRESH_WORK_TAG)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(REFRESH_WORK_NAME, ExistingWorkPolicy.REPLACE, refresh)
    }

    companion object {
        private const val REFRESH_WORK_NAME = "prayer-alarm-refresh-event"
        private const val REFRESH_WORK_TAG = "prayer-alarm-refresh-event"
        private val REFRESH_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED,
        )
    }
}