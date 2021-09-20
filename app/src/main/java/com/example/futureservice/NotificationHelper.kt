package com.example.futureservice

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

/**
 * Created by blasius.n.puspika on 18/09/21.
 */

class NotificationHelper(private val context: Context) {

  companion object {
    private const val CHANNEL_ID = "1"
    private const val CHANNEL_NAME = "Countdown Timer"
    private const val CHANNEL_DESCRIPTION = "Channel for run count downtimer service"
    const val NOTIFICATION_ID = 10001
  }

  private val notificationManager by lazy {
    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
  }

  private val contentIntent by lazy {
    PendingIntent.getActivity(
      context,
      0,
      Intent(context, MainActivity::class.java),
      PendingIntent.FLAG_UPDATE_CURRENT
    )
  }

  private val notificationBuilder: NotificationCompat.Builder by lazy {
    NotificationCompat.Builder(context, CHANNEL_ID)
      .setContentTitle(context.getString(R.string.app_name))
      .setSound(null)
      .setContentIntent(contentIntent)
      .setSmallIcon(R.drawable.ic_baseline_timer_24)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun createNotificationChannel(): NotificationChannel {
    return NotificationChannel(
      CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
      description = CHANNEL_DESCRIPTION
      setSound(null, null)
    }
  }

  fun getNotification(): Notification {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      notificationManager.createNotificationChannel(createNotificationChannel())
    }
    return notificationBuilder.build()
  }

  fun updateNotification(currentTime: String) {
    notificationBuilder.setContentText(currentTime)
    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
  }
}