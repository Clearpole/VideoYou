package com.clearpole.videoyou.utils

import android.animation.ValueAnimator
import com.google.android.material.slider.Slider

class SeekBarAnim {
    companion object {
        fun progressAnim(progress: Float, view: Slider) {
            val lastProgress: String = view.value.toString() + "f"
            val dh = ValueAnimator.ofFloat(lastProgress.toFloat(), progress)
            dh.duration = java.lang.String.valueOf(500).toLong()
            dh.addUpdateListener {
                val nowAnimatedValue = dh.animatedValue as Float
                if (nowAnimatedValue == progress) {
                    dh.cancel()
                }
                view.value = nowAnimatedValue
            }
            dh.start()
        }
    }
}