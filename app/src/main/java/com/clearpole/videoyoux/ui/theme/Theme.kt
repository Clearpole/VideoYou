package com.clearpole.videoyoux.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import com.clearpole.videoyoux.utils.IsNightMode
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.kyant.monet.LocalTonalPalettes
import com.kyant.monet.PaletteStyle
import com.kyant.monet.TonalPalettes
import com.kyant.monet.dynamicColorScheme
import com.kyant.monet.n1
import com.kyant.monet.rangeTo

@Composable
fun VideoYouTheme(
    hideBar: Boolean,
    content: @Composable () -> Unit
) {
    // key color
    val color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        colorResource(id = android.R.color.system_accent1_500)
    } else Color(0xFF007FAC)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            if (hideBar) {
                ImmersionBar.with(view.context as Activity).hideBar(BarHide.FLAG_HIDE_BAR).init()
            }else{
                ImmersionBar.with(view.context as Activity).transparentBar().statusBarDarkFont(!IsNightMode.isNightMode(view.resources)).init()
            }
        }
    }

    CompositionLocalProvider(
        LocalTonalPalettes provides TonalPalettes(
            keyColor = color,
            style = PaletteStyle.TonalSpot
        )
    ) {
        CompositionLocalProvider(LocalContentColor provides 0.n1..100.n1) {
            MaterialTheme(
                colorScheme = dynamicColorScheme(),
                content = content
            )
        }
    }
}