package com.dastageer.rento.presentation.shared.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.RentoShapes

/**
 * ShimmerLoader — skeleton loading placeholder with horizontal sweep animation.
 * Gradient: DarkBg3 → DarkBg2 (shimmer peak) → DarkBg3. 1500ms linear infinite.
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    height: Dp = 190.dp,
) {
    val colors = LocalRentoColors.current
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offsetX by transition.animateFloat(
        initialValue = -500f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerOffset",
    )

    val brush = Brush.linearGradient(
        colors = listOf(colors.bg3, colors.bg2, colors.bg3),
        start = Offset(offsetX, 0f),
        end = Offset(offsetX + 500f, 0f),
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RentoShapes.card)
            .background(brush),
    )
}

/**
 * Shimmer card placeholder matching PropertyCard dimensions.
 */
@Composable
fun ShimmerPropertyCard(modifier: Modifier = Modifier) {
    val colors = LocalRentoColors.current
    val transition = rememberInfiniteTransition(label = "shimmerCard")
    val offsetX by transition.animateFloat(
        initialValue = -500f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerCardOffset",
    )

    val brush = Brush.linearGradient(
        colors = listOf(colors.bg3, colors.bg2, colors.bg3),
        start = Offset(offsetX, 0f),
        end = Offset(offsetX + 500f, 0f),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RentoShapes.card)
            .background(colors.bg2),
    ) {
        // Image area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .background(brush),
        )
        // Content area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.bg2)
                .height(100.dp),
        ) {
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .clip(RentoShapes.pill)
                    .background(brush),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(12.dp)
                    .clip(RentoShapes.pill)
                    .background(brush),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(14.dp)
                        .clip(RentoShapes.pill)
                        .background(brush),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(14.dp)
                        .clip(RentoShapes.pill)
                        .background(brush),
                )
            }
        }
    }
}
