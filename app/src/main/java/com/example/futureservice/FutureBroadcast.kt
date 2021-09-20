package com.example.futureservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log

/**
 * Created by blasius.n.puspika on 21/09/21.
 */

private const val TAG = "MyBroadcastReceiver"

class MyBroadcastReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    val pendingResult: PendingResult = goAsync()
    val asyncTask = Task(pendingResult, intent)
    asyncTask.execute()
  }

  private class Task(
    private val pendingResult: PendingResult,
    private val intent: Intent
  ) : AsyncTask<String, Int, String>() {

    override fun doInBackground(vararg params: String?): String {
      val sb = StringBuilder()
      sb.append("Action: ${intent.action}\n")
      sb.append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
      return toString().also { log ->
        Log.d(TAG, log)
      }
    }

    override fun onPostExecute(result: String?) {
      super.onPostExecute(result)
      // Must call finish() so the BroadcastReceiver can be recycled.
      pendingResult.finish()
    }
  }
}