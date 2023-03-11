package com.clearpole.videoyoux.ui.theme.utils

import android.app.Activity
import android.graphics.Typeface
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class Toast {
    companion object {
        fun showInfo(context: Activity, notes: String, title: String? = null,duration:Long? = MotionToast.LONG_DURATION) {
            MotionToast.createColorToast(
                context, title = title, message = "${title.orEmpty()}，$notes",
                style = MotionToastStyle.INFO,
                position = MotionToast.GRAVITY_BOTTOM,
                duration = duration!!,
                font = Typeface.DEFAULT_BOLD
            )
        }
        fun showSuccess(context: Activity, notes: String, title: String? = null,duration:Long? = MotionToast.LONG_DURATION) {
            MotionToast.createColorToast(
                context, title = title, message = "${title.orEmpty()} $notes".trim(),
                style = MotionToastStyle.SUCCESS,
                position = MotionToast.GRAVITY_BOTTOM,
                duration = duration!!,
                font = Typeface.DEFAULT_BOLD
            )
        }
        fun showError(context: Activity, notes: String, title: String? = null,duration:Long? = MotionToast.LONG_DURATION) {
            MotionToast.createColorToast(
                context, title = title, message = "${title.orEmpty()} $notes".trim(),
                style = MotionToastStyle.ERROR,
                position = MotionToast.GRAVITY_BOTTOM,
                duration = duration!!,
                font = Typeface.DEFAULT_BOLD
            )
        }
        fun showWarning(context: Activity, notes: String, title: String? = null,duration:Long? = MotionToast.LONG_DURATION) {
            MotionToast.createColorToast(
                context, title = title, message = "${title.orEmpty()} $notes".trim(),
                style = MotionToastStyle.WARNING,
                position = MotionToast.GRAVITY_BOTTOM,
                duration = duration!!,
                font = Typeface.DEFAULT_BOLD
            )
        }
    }
}