package com.dastageer.rento.presentation.shared.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors

/**
 * RadiusCircle — for request form map radius visualization.
 * Circle: diameter = 80dp + (radiusKm × 12dp).
 * Fill: DarkPriM (10% green). Stroke: 2dp DarkPri.
 * Centre dot: 12dp circle, DarkPri fill, 4dp DarkBg0 ring.
 */
@Composable
fun RadiusCircle(
    radiusKm: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalRentoColors.current

    val diameter by animateDpAsState(
        targetValue = 80.dp + (radiusKm * 12).dp,
        animationSpec = tween(150),
        label = "radiusDiameter",
    )

    Box(
        modifier = modifier.size(diameter),
        contentAlignment = Alignment.Center,
    ) {
        // Outer circle
        Box(
            modifier = Modifier
                .size(diameter)
                .clip(CircleShape)
                .background(colors.primaryTint)
                .border(2.dp, colors.primary, CircleShape),
        )

        // Centre dot with ring
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(colors.primary)
                .border(4.dp, colors.bg0, CircleShape),
        )
    }
}
