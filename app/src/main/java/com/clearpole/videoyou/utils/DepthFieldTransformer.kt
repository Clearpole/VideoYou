package com.clearpole.videoyou.utils

import android.view.View
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

private const val MIN_SCALE = 0.8f

class DepthFieldTransformer : ViewPager2.PageTransformer, ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.apply {
            when {
                position < -1 -> {
                    alpha = 1f - MIN_SCALE
                    scaleX = MIN_SCALE
                    scaleY = MIN_SCALE
                }
                position <= 1 -> {
                    val absPos = abs(position)
                    val scale = if (absPos > 1) 0F else 1 - absPos
                    page.scaleX = MIN_SCALE + (1f - MIN_SCALE) * scale
                    page.scaleY = MIN_SCALE + (1f - MIN_SCALE) * scale
                    page.alpha = MIN_SCALE + (1f - MIN_SCALE) * scale
                }
                else -> {
                    page.scaleX = 0f
                    page.scaleY = 0f
                    page.alpha = 0f
                }
            }
        }


    }
}