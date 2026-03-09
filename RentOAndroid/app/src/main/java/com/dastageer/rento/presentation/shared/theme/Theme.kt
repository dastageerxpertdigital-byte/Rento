package com.dastageer.rento.presentation.shared.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// ─── Theme Composable ───────────────────────────────────────────────────────

@Composable
fun RentoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) RentoDarkColors else RentoLightColors
    val typography = RentoTypography()
    val dimens = RentoDimens()

    CompositionLocalProvider(
        LocalRentoColors provides colors,
        LocalRentoTypography provides typography,
        LocalRentoDimens provides dimens,
    ) {
        MaterialTheme(
            colorScheme = colors.toMaterialColorScheme(),
            typography  = typography.toMaterial3Typography(),
            content     = content,
        )
    }
}

// ─── Material3 Color Scheme Bridge ──────────────────────────────────────────

fun RentoColors.toMaterialColorScheme() = if (isDark) {
    darkColorScheme(
        primary          = primary,
        onPrimary        = Color.White,
        primaryContainer = primaryTint,
        background       = bg0,
        surface          = bg1,
        surfaceVariant   = bg2,
        onBackground     = t0,
        onSurface        = t0,
        onSurfaceVariant = t1,
        error            = red,
        onError          = Color.White,
        outline          = border,
        outlineVariant   = border2,
    )
} else {
    lightColorScheme(
        primary          = primary,
        onPrimary        = Color.White,
        primaryContainer = primaryTint,
        background       = bg0,
        surface          = bg1,
        surfaceVariant   = bg2,
        onBackground     = t0,
        onSurface        = t0,
        onSurfaceVariant = t1,
        error            = red,
        onError          = Color.White,
        outline          = border,
        outlineVariant   = border2,
    )
}

// ─── Ergonomic Extension Properties ─────────────────────────────────────────

val MaterialTheme.rentoColors: RentoColors
    @Composable @ReadOnlyComposable
    get() = LocalRentoColors.current

val MaterialTheme.rentoTypography: RentoTypography
    @Composable @ReadOnlyComposable
    get() = LocalRentoTypography.current

val MaterialTheme.rentoDimens: RentoDimens
    @Composable @ReadOnlyComposable
    get() = LocalRentoDimens.current
