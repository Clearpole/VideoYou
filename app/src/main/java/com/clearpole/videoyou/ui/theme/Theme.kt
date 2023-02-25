package com.clearpole.videoyou.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.clearpole.videoyou.R
import com.clearpole.videoyou.objects.AppObjects
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.tencent.mmkv.MMKV

@Composable
fun VideoYouOptTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = when (AppObjects.theme) {
        0 -> {
            dynamicLightColorScheme(LocalContext.current)
        }

        1 -> {
            lightColorScheme(
                primary = hzt_theme_light_primary,
                onPrimary = hzt_theme_light_onPrimary,
                primaryContainer = hzt_theme_light_primaryContainer,
                onPrimaryContainer = hzt_theme_light_onPrimaryContainer,
                inversePrimary = hzt_theme_light_inversePrimary,
                secondary = hzt_theme_light_secondary,
                onSecondary = hzt_theme_light_onSecondary,
                secondaryContainer = hzt_theme_light_secondaryContainer,
                onSecondaryContainer = hzt_theme_light_onSecondaryContainer,
                tertiary = hzt_theme_light_tertiary,
                onTertiary = hzt_theme_light_onTertiary,
                tertiaryContainer = hzt_theme_light_tertiaryContainer,
                onTertiaryContainer = hzt_theme_light_onTertiaryContainer,
                background = hzt_theme_light_background,
                onBackground = hzt_theme_light_onBackground,
                surface = hzt_theme_light_surface,
                onSurface = hzt_theme_light_onSurface,
                surfaceVariant = hzt_theme_light_surfaceVariant,
                onSurfaceVariant = hzt_theme_light_onSurfaceVariant,
                surfaceTint = hzt_theme_light_surfaceTint,
                inverseSurface = hzt_theme_light_inverseSurface,
                inverseOnSurface = hzt_theme_light_inverseOnSurface,
                error = hzt_theme_light_error,
                onError = hzt_theme_light_onError,
                errorContainer = hzt_theme_light_errorContainer,
                onErrorContainer = hzt_theme_light_onErrorContainer,
                outline = hzt_theme_light_outline,
                outlineVariant = hzt_theme_light_outlineVariant,
                scrim = hzt_theme_light_scrim,
            )
        }

        2 -> {
            lightColorScheme(
                primary = cxw_theme_light_primary,
                onPrimary = cxw_theme_light_onPrimary,
                primaryContainer = cxw_theme_light_primaryContainer,
                onPrimaryContainer = cxw_theme_light_onPrimaryContainer,
                inversePrimary = cxw_theme_light_inversePrimary,
                secondary = cxw_theme_light_secondary,
                onSecondary = cxw_theme_light_onSecondary,
                secondaryContainer = cxw_theme_light_secondaryContainer,
                onSecondaryContainer = cxw_theme_light_onSecondaryContainer,
                tertiary = cxw_theme_light_tertiary,
                onTertiary = cxw_theme_light_onTertiary,
                tertiaryContainer = cxw_theme_light_tertiaryContainer,
                onTertiaryContainer = cxw_theme_light_onTertiaryContainer,
                background = cxw_theme_light_background,
                onBackground = cxw_theme_light_onBackground,
                surface = cxw_theme_light_surface,
                onSurface = cxw_theme_light_onSurface,
                surfaceVariant = cxw_theme_light_surfaceVariant,
                onSurfaceVariant = cxw_theme_light_onSurfaceVariant,
                surfaceTint = cxw_theme_light_surfaceTint,
                inverseSurface = cxw_theme_light_inverseSurface,
                inverseOnSurface = cxw_theme_light_inverseOnSurface,
                error = cxw_theme_light_error,
                onError = cxw_theme_light_onError,
                errorContainer = cxw_theme_light_errorContainer,
                onErrorContainer = cxw_theme_light_onErrorContainer,
                outline = cxw_theme_light_outline,
                outlineVariant = cxw_theme_light_outlineVariant,
                scrim = cxw_theme_light_scrim,
            )
        }

        3 -> {
            lightColorScheme(
                primary = szy_theme_light_primary,
                onPrimary = szy_theme_light_onPrimary,
                primaryContainer = szy_theme_light_primaryContainer,
                onPrimaryContainer = szy_theme_light_onPrimaryContainer,
                inversePrimary = szy_theme_light_inversePrimary,
                secondary = szy_theme_light_secondary,
                onSecondary = szy_theme_light_onSecondary,
                secondaryContainer = szy_theme_light_secondaryContainer,
                onSecondaryContainer = szy_theme_light_onSecondaryContainer,
                tertiary = szy_theme_light_tertiary,
                onTertiary = szy_theme_light_onTertiary,
                tertiaryContainer = szy_theme_light_tertiaryContainer,
                onTertiaryContainer = szy_theme_light_onTertiaryContainer,
                background = szy_theme_light_background,
                onBackground = szy_theme_light_onBackground,
                surface = szy_theme_light_surface,
                onSurface = szy_theme_light_onSurface,
                surfaceVariant = szy_theme_light_surfaceVariant,
                onSurfaceVariant = szy_theme_light_onSurfaceVariant,
                surfaceTint = szy_theme_light_surfaceTint,
                inverseSurface = szy_theme_light_inverseSurface,
                inverseOnSurface = szy_theme_light_inverseOnSurface,
                error = szy_theme_light_error,
                onError = szy_theme_light_onError,
                errorContainer = szy_theme_light_errorContainer,
                onErrorContainer = szy_theme_light_onErrorContainer,
                outline = szy_theme_light_outline,
                outlineVariant = szy_theme_light_outlineVariant,
                scrim = szy_theme_light_scrim,
            )
        }

        4 -> {
            lightColorScheme(
                primary = xfy_theme_light_primary,
                onPrimary = xfy_theme_light_onPrimary,
                primaryContainer = xfy_theme_light_primaryContainer,
                onPrimaryContainer = xfy_theme_light_onPrimaryContainer,
                inversePrimary = xfy_theme_light_inversePrimary,
                secondary = xfy_theme_light_secondary,
                onSecondary = xfy_theme_light_onSecondary,
                secondaryContainer = xfy_theme_light_secondaryContainer,
                onSecondaryContainer = xfy_theme_light_onSecondaryContainer,
                tertiary = xfy_theme_light_tertiary,
                onTertiary = xfy_theme_light_onTertiary,
                tertiaryContainer = xfy_theme_light_tertiaryContainer,
                onTertiaryContainer = xfy_theme_light_onTertiaryContainer,
                background = xfy_theme_light_background,
                onBackground = xfy_theme_light_onBackground,
                surface = xfy_theme_light_surface,
                onSurface = xfy_theme_light_onSurface,
                surfaceVariant = xfy_theme_light_surfaceVariant,
                onSurfaceVariant = xfy_theme_light_onSurfaceVariant,
                surfaceTint = xfy_theme_light_surfaceTint,
                inverseSurface = xfy_theme_light_inverseSurface,
                inverseOnSurface = xfy_theme_light_inverseOnSurface,
                error = xfy_theme_light_error,
                onError = xfy_theme_light_onError,
                errorContainer = xfy_theme_light_errorContainer,
                onErrorContainer = xfy_theme_light_onErrorContainer,
                outline = xfy_theme_light_outline,
                outlineVariant = xfy_theme_light_outlineVariant,
                scrim = xfy_theme_light_scrim,
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