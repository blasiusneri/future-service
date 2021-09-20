package com.example.futureservice

import android.app.NotificationManager
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.core.app.NotificationManagerCompat
import com.example.futureservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  companion object {
    const val COUNTDOWN_TIMER_INTENT_FILTER = "COUNTDOWN_TIMER_INTENT_FILTER"
    const val COUNTDOWN_TIMER_COMPLETE = "COUNTDOWN_TIMER_COMPLETE"
  }

  private var countdownTimerService: CountdownTimerService? = null

  private val broadcastReceiver: BroadcastReceiver by lazy {
    object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        val countdownComplete =
          intent?.getBooleanExtra(COUNTDOWN_TIMER_COMPLETE, false)
        binding.tvCountdownTimer.visibility = if (countdownComplete == true) {
          View.VISIBLE
        } else {
          View.GONE
        }
      }
    }
  }

  private val binding: ActivityMainBinding by lazy {
    ActivityMainBinding.inflate(layoutInflater)
  }

  private val boundServiceConnection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
      val binder: CountdownTimerService.CountdownTimerBinder =
        service as CountdownTimerService.CountdownTimerBinder
      countdownTimerService = binder.getCountdownTimerService()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
      countdownTimerService = null
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    binding.buttonStart.setOnClickListener {
      binding.tvCountdownTimer.visibility = View.GONE
      startService(Intent(this, CountdownTimerService::class.java))
    }

    binding.buttonStop.setOnClickListener {
      stopService(Intent(this, CountdownTimerService::class.java))
    }

    registerReceiver(broadcastReceiver, IntentFilter(COUNTDOWN_TIMER_INTENT_FILTER))
  }

  override fun onStart() {
    super.onStart()
    bindToCountdownTimerService()
  }

  private fun bindToCountdownTimerService() {
    Intent(this, CountdownTimerService::class.java).also {
      bindService(it, boundServiceConnection, Context.BIND_AUTO_CREATE)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    unregisterReceiver(broadcastReceiver)
  }
}