package com.dastageer.rento.presentation.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoDimens
import com.dastageer.rento.presentation.shared.theme.RentoShapes

/**
 * MapBackground — DarkBg2 fill with grid lines overlay.
 * Grid: horizontal + vertical lines every 28dp, DarkBd colour, 1dp stroke.
 */
@Composable
fun MapBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    val colors = LocalRentoColors.current
    val dimens = LocalRentoDimens.current

    Box(
        modifier = modifier
            .clip(RentoShapes.mapContainer)
            .background(colors.bg2)
            .drawBehind {
                val gridSpacing = dimens.mapGridSpacing.toPx()
                val lineColor = colors.border

                // Horizontal lines
                var y = gridSpacing
                while (y < size.height) {
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f,
                    )
                    y += gridSpacing
                }

                // Vertical lines
                var x = gridSpacing
                while (x < size.width) {
                    drawLine(
                        color = lineColor,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f,
                    )
                    x += gridSpacing
                }
            },
    ) {
        content()
    }
}
