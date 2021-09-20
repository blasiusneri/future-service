package com.example.futureservice

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.Settings

/**
 * Created by blasius.n.puspika on 18/09/21.
 */

class AudioPlayerService : Service() {

  private lateinit var mediaPlayer: MediaPlayer
  private val notificationHelper: NotificationHelper by lazy {
    NotificationHelper(this)
  }

  //will trigger when activity started using startService command
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    if (this::mediaPlayer.isInitialized.not()) {
      mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI)
      mediaPlayer.isLooping = true
      mediaPlayer.start()
      startForeground(NotificationHelper.NOTIFICATION_ID, notificationHelper.getNotification())
    }
    return START_STICKY
  }

  override fun onBind(intent: Intent?): IBinder? {
    //onBind non needed in this example
    return null
  }

  override fun onDestroy() {
    super.onDestroy()
    //always declare counterpart of what you do in creating phase
    mediaPlayer.stop()
  }
}