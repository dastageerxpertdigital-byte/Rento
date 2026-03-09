package com.dastageer.rento.presentation.shared.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RadialGradientShader
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors

/**
 * MeshBackground — full-screen bg0 fill with two subtle radial gradient ellipses.
 * Ellipse at 15%/18%: DarkPri at 5.5% opacity, transparent at 55%.
 * Ellipse at 82%/80%: DarkSec at 3.5% opacity, transparent at 55%.
 */
@Composable
fun MeshBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    val colors = LocalRentoColors.current

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Base fill
            drawRect(color = colors.bg0)

            // Top-left radial glow
            val priGlow = colors.primary.copy(alpha = 0.055f)
            drawCircle(
                color = priGlow,
                radius = size.width * 0.55f,
                center = Offset(size.width * 0.15f, size.height * 0.18f),
            )

            // Bottom-right radial glow
            val secGlow = colors.secondary.copy(alpha = 0.035f)
            drawCircle(
                color = secGlow,
                radius = size.width * 0.55f,
                center = Offset(size.width * 0.82f, size.height * 0.80f),
            )
        }
        content()
    }
}
