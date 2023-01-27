package com.clearpole.videoyou.utils

import android.app.Activity
import android.content.res.Resources
import android.widget.LinearLayout
import com.gyf.immersionbar.ImmersionBar

class SetBarTransparent {
    companion object {
        fun setBarTransparent(statusBarView: LinearLayout, activity: Activity,resources: Resources) {
            ImmersionBar.with(activity).transparentBar().statusBarDarkFont(!IsNightMode.isNightMode(resources))
                .init()
            val statusBarHeight = ImmersionBar.getStatusBarHeight(activity)
            statusBarView.layoutParams.height = statusBarHeight
        }
    }
}