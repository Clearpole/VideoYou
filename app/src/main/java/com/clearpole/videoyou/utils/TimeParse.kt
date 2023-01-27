package com.clearpole.videoyou.utils

import kotlin.math.roundToInt

class TimeParse {
    companion object {
        fun timeParse(duration: Long): String? {
            var time: String? = ""
            val minute = duration / 60000
            val seconds = duration % 60000
            val second = (seconds.toFloat() / 1000).roundToInt().toLong()
            if (minute < 10) {
                time += "0"
            }
            time += "$minute:"
            if (second < 10) {
                time += "0"
            }
            time += second
            return time
        }
    }
}