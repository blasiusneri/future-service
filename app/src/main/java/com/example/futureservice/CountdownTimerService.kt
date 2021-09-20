package com.example.futureservice

import android.app.Service
import android.content.Intent
import android.os.*
import com.example.futureservice.MainActivity.Companion.COUNTDOWN_TIMER_COMPLETE
import com.example.futureservice.MainActivity.Companion.COUNTDOWN_TIMER_INTENT_FILTER
import kotlinx.coroutines.*
import java.lang.Runnable
import kotlin.coroutines.CoroutineContext

/**
 * Created by blasius.n.puspika on 19/09/21.
 */

class CountdownTimerService : Service(), CoroutineScope {

  companion object {
    private const val START_COUNTDOWN_TIME = 10 //in second
  }

  private val serviceJob = Job()
  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + serviceJob

  private val notificationHelper by lazy {
    NotificationHelper(this)
  }

  private var timerStarted = false
  private var currentTime: Int = 0

  private val handler = Handler(Looper.getMainLooper())
  private var runnable = object: Runnable {
    override fun run() {
      if (currentTime > 0) {
        currentTime--
        updateTimer()
        handler.postDelayed(this, 1000)
      } else {
        updateUi()
        stopTimer()
      }
    }
  }

  private var countDownStartTime: Int = START_COUNTDOWN_TIME
    set(value) {
      currentTime = value
      field = value
    }

  private val binder by lazy { CountdownTimerBinder() }

  override fun onBind(intent: Intent?): IBinder? {
    startTimer()
    return binder
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)
    if (timerStarted.not()) {
      startTimer()
    }
    return START_NOT_STICKY
  }

  private fun startTimer() {
    countDownStartTime = START_COUNTDOWN_TIME
    timerStarted = true
    startForeground(NotificationHelper.NOTIFICATION_ID, notificationHelper.getNotification())
    launch(coroutineContext) {
      handler.post(runnable)
    }
  }

  private fun updateTimer() {
    notificationHelper.updateNotification(currentTime.secondsToTime())
  }

  private fun stopTimer() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      stopForeground(true)
    } else {
      stopSelf()
    }
    timerStarted = false
    handler.removeCallbacks(runnable)
    handler.removeCallbacksAndMessages(null)
  }

  override fun onDestroy() {
    super.onDestroy()
    if (timerStarted) {
      stopTimer()
    }
    serviceJob.cancel()
  }

  private fun updateUi() {
    sendBroadcast(Intent(COUNTDOWN_TIMER_INTENT_FILTER)
      .putExtra(COUNTDOWN_TIMER_COMPLETE, true))
  }

  inner class CountdownTimerBinder : Binder() {

    fun getCountdownTimerService(): CountdownTimerService = this@CountdownTimerService
  }
}