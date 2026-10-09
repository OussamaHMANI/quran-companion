package com.quoren.app.data.audio

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.quoren.app.MainActivity
import com.quoren.app.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/** Hosts the MediaSession around the shared player: notification, lock screen and background playback. */
@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    @Inject lateinit var player: ExoPlayer

    private var session: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        val activity = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        session = MediaSession.Builder(this, player).setSessionActivity(activity).build()
        setMediaNotificationProvider(
            DefaultMediaNotificationProvider.Builder(this).setChannelName(R.string.audio_channel_name).build(),
        )
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = session

    override fun onTaskRemoved(rootIntent: Intent?) {
        if (!player.playWhenReady || player.mediaItemCount == 0) stopSelf()
    }

    override fun onDestroy() {
        session?.release()
        session = null
        super.onDestroy()
    }
}
