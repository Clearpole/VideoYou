package com.clearpole.videoyou.utils

import android.os.Handler
import android.view.View

abstract class BaseClickListener protected constructor() : View.OnClickListener {
    private var clickCount = 0

    @Suppress("DEPRECATION")
    private val handler: Handler = Handler()

    override fun onClick(v: View) {
        clickCount++
        handler.postDelayed({
            if (clickCount == 1) {
                onSingleClick(v)
            } else if (clickCount == 2) {
                onDoubleClick(v)
            }
            handler.removeCallbacksAndMessages(null)
            clickCount = 0
        }, TIMEOUT.toLong())
    }

    abstract fun onSingleClick(v: View?)

    abstract fun onDoubleClick(v: View?)

    companion object {
        private const val TIMEOUT = 250
    }
}


