package com.clearpole.videoyou.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.clearpole.videoyou.R
import com.clearpole.videoyou.utils.SettingsItemsUntil
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.tencent.mmkv.MMKV

@Composable
fun VideoYouOptTheme(
    content: @Composable () -> Unit
) {
    val kv = MMKV.mmkvWithID("theme")
    val colorScheme = when (kv.decodeInt("theme")) {
        0 -> {
            dynamicLightColorScheme(LocalContext.current)
        }

        R.style.hzt -> {
            lightColorScheme(
                primary = Color(R.color.hzt_theme_light_primary),
                onPrimary = Color(R.color.hzt_theme_light_onPrimary),
                primaryContainer = Color(R.color.hzt_theme_light_primaryContainer),
                onPrimaryContainer = Color(R.color.hzt_theme_light_onPrimaryContainer),
                inversePrimary = Color(R.color.hzt_theme_light_inversePrimary),
                secondary = Color(R.color.hzt_theme_light_secondary),
                onSecondary = Color(R.color.hzt_theme_light_onSecondary),
                secondaryContainer = Color(R.color.hzt_theme_light_secondaryContainer),
                onSecondaryContainer = Color(R.color.hzt_theme_light_onSecondaryContainer),
                tertiary = Color(R.color.hzt_theme_light_tertiary),
                onTertiary = Color(R.color.hzt_theme_light_onTertiary),
                tertiaryContainer = Color(R.color.hzt_theme_light_tertiaryContainer),
                onTertiaryContainer = Color(R.color.hzt_theme_light_onTertiaryContainer),
                background = Color(R.color.hzt_theme_light_background),
                onBackground = Color(R.color.hzt_theme_light_onBackground),
                surface = Color(R.color.hzt_theme_light_surface),
                onSurface = Color(R.color.hzt_theme_light_onSurface),
                surfaceVariant = Color(R.color.hzt_theme_light_surfaceVariant),
                onSurfaceVariant = Color(R.color.hzt_theme_light_onSurfaceVariant),
                surfaceTint = Color(R.color.hzt_theme_light_surfaceTint),
                inverseSurface = Color(R.color.hzt_theme_light_inverseSurface),
                inverseOnSurface = Color(R.color.hzt_theme_light_inverseOnSurface),
                error = Color(R.color.hzt_theme_light_error),
                onError = Color(R.color.hzt_theme_light_onError),
                errorContainer = Color(R.color.hzt_theme_light_errorContainer),
                onErrorContainer = Color(R.color.hzt_theme_light_onErrorContainer),
                outline = Color(R.color.hzt_theme_light_outline),
                outlineVariant = Color(R.color.hzt_theme_light_outlineVariant),
                scrim = Color(R.color.hzt_theme_light_scrim),
            )
        }

        R.style.cxw -> {
            lightColorScheme(
                primary = Color(R.color.cxw_theme_light_primary),
                onPrimary = Color(R.color.cxw_theme_light_onPrimary),
                primaryContainer = Color(R.color.cxw_theme_light_primaryContainer),
                onPrimaryContainer = Color(R.color.cxw_theme_light_onPrimaryContainer),
                inversePrimary = Color(R.color.cxw_theme_light_inversePrimary),
                secondary = Color(R.color.cxw_theme_light_secondary),
                onSecondary = Color(R.color.cxw_theme_light_onSecondary),
                secondaryContainer = Color(R.color.cxw_theme_light_secondaryContainer),
                onSecondaryContainer = Color(R.color.cxw_theme_light_onSecondaryContainer),
                tertiary = Color(R.color.cxw_theme_light_tertiary),
                onTertiary = Color(R.color.cxw_theme_light_onTertiary),
                tertiaryContainer = Color(R.color.cxw_theme_light_tertiaryContainer),
                onTertiaryContainer = Color(R.color.cxw_theme_light_onTertiaryContainer),
                background = Color(R.color.cxw_theme_light_background),
                onBackground = Color(R.color.cxw_theme_light_onBackground),
                surface = Color(R.color.cxw_theme_light_surface),
                onSurface = Color(R.color.cxw_theme_light_onSurface),
                surfaceVariant = Color(R.color.cxw_theme_light_surfaceVariant),
                onSurfaceVariant = Color(R.color.cxw_theme_light_onSurfaceVariant),
                surfaceTint = Color(R.color.cxw_theme_light_surfaceTint),
                inverseSurface = Color(R.color.cxw_theme_light_inverseSurface),
                inverseOnSurface = Color(R.color.cxw_theme_light_inverseOnSurface),
                error = Color(R.color.cxw_theme_light_error),
                onError = Color(R.color.cxw_theme_light_onError),
                errorContainer = Color(R.color.cxw_theme_light_errorContainer),
                onErrorContainer = Color(R.color.cxw_theme_light_onErrorContainer),
                outline = Color(R.color.cxw_theme_light_outline),
                outlineVariant = Color(R.color.cxw_theme_light_outlineVariant),
                scrim = Color(R.color.cxw_theme_light_scrim),
            )
        }

        R.style.szy -> {
            lightColorScheme(
                primary = Color(R.color.szy_theme_light_primary),
                onPrimary = Color(R.color.szy_theme_light_onPrimary),
                primaryContainer = Color(R.color.szy_theme_light_primaryContainer),
                onPrimaryContainer = Color(R.color.szy_theme_light_onPrimaryContainer),
                inversePrimary = Color(R.color.szy_theme_light_inversePrimary),
                secondary = Color(R.color.szy_theme_light_secondary),
                onSecondary = Color(R.color.szy_theme_light_onSecondary),
                secondaryContainer = Color(R.color.szy_theme_light_secondaryContainer),
                onSecondaryContainer = Color(R.color.szy_theme_light_onSecondaryContainer),
                tertiary = Color(R.color.szy_theme_light_tertiary),
                onTertiary = Color(R.color.szy_theme_light_onTertiary),
                tertiaryContainer = Color(R.color.szy_theme_light_tertiaryContainer),
                onTertiaryContainer = Color(R.color.szy_theme_light_onTertiaryContainer),
                background = Color(R.color.szy_theme_light_background),
                onBackground = Color(R.color.szy_theme_light_onBackground),
                surface = Color(R.color.szy_theme_light_surface),
                onSurface = Color(R.color.szy_theme_light_onSurface),
                surfaceVariant = Color(R.color.szy_theme_light_surfaceVariant),
                onSurfaceVariant = Color(R.color.szy_theme_light_onSurfaceVariant),
                surfaceTint = Color(R.color.szy_theme_light_surfaceTint),
                inverseSurface = Color(R.color.szy_theme_light_inverseSurface),
                inverseOnSurface = Color(R.color.szy_theme_light_inverseOnSurface),
                error = Color(R.color.szy_theme_light_error),
                onError = Color(R.color.szy_theme_light_onError),
                errorContainer = Color(R.color.szy_theme_light_errorContainer),
                onErrorContainer = Color(R.color.szy_theme_light_onErrorContainer),
                outline = Color(R.color.szy_theme_light_outline),
                outlineVariant = Color(R.color.szy_theme_light_outlineVariant),
                scrim = Color(R.color.szy_theme_light_scrim),
            )
        }

        R.style.xfy -> {
            lightColorScheme(
                primary = Color(R.color.xfy_theme_light_primary),
                onPrimary = Color(R.color.xfy_theme_light_onPrimary),
                primaryContainer = Color(R.color.xfy_theme_light_primaryContainer),
                onPrimaryContainer = Color(R.color.xfy_theme_light_onPrimaryContainer),
                inversePrimary = Color(R.color.xfy_theme_light_inversePrimary),
                secondary = Color(R.color.xfy_theme_light_secondary),
                onSecondary = Color(R.color.xfy_theme_light_onSecondary),
                secondaryContainer = Color(R.color.xfy_theme_light_secondaryContainer),
                onSecondaryContainer = Color(R.color.xfy_theme_light_onSecondaryContainer),
                tertiary = Color(R.color.xfy_theme_light_tertiary),
                onTertiary = Color(R.color.xfy_theme_light_onTertiary),
                tertiaryContainer = Color(R.color.xfy_theme_light_tertiaryContainer),
                onTertiaryContainer = Color(R.color.xfy_theme_light_onTertiaryContainer),
                background = Color(R.color.xfy_theme_light_background),
                onBackground = Color(R.color.xfy_theme_light_onBackground),
                surface = Color(R.color.xfy_theme_light_surface),
                onSurface = Color(R.color.xfy_theme_light_onSurface),
                surfaceVariant = Color(R.color.xfy_theme_light_surfaceVariant),
                onSurfaceVariant = Color(R.color.xfy_theme_light_onSurfaceVariant),
                surfaceTint = Color(R.color.xfy_theme_light_surfaceTint),
                inverseSurface = Color(R.color.xfy_theme_light_inverseSurface),
                inverseOnSurface = Color(R.color.xfy_theme_light_inverseOnSurface),
                error = Color(R.color.xfy_theme_light_error),
                onError = Color(R.color.xfy_theme_light_onError),
                errorContainer = Color(R.color.xfy_theme_light_errorContainer),
                onErrorContainer = Color(R.color.xfy_theme_light_onErrorContainer),
                outline = Color(R.color.xfy_theme_light_outline),
                outlineVariant = Color(R.color.xfy_theme_light_outlineVariant),
                scrim = Color(R.color.xfy_theme_light_scrim),
            )
        }

        else -> {
            lightColorScheme()
        }
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            ImmersionBar.with(view.context as Activity).hideBar(BarHide.FLAG_HIDE_BAR).init()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}