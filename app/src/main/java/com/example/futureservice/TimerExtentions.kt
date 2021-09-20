package com.example.futureservice

/**
 * Created by blasius.n.puspika on 19/09/21.
 */

fun Int.secondsToTime(): String {
  val hours = this / 3600
  val minutes = (this % 3600) / 60
  val seconds = this % 60

  return if (hours == 0) {
    String.format("%02d:%02d", minutes, seconds)
  } else {
    String.format("%02d:%02d:%02d", hours, minutes, seconds)
  }
}