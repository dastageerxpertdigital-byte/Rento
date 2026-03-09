package com.dastageer.rento.presentation.shared.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoDimens
import com.dastageer.rento.presentation.shared.theme.RentoShapes

/**
 * Linear progress bar — DarkBg4 track, DarkPri fill.
 * Progress animates smoothly.
 */
@Composable
fun RentoProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val colors = LocalRentoColors.current
    val dimens = LocalRentoDimens.current

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(300),
        label = "progressBar",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.progressBarHeight)
            .clip(RentoShapes.pill)
            .background(colors.bg4),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = animatedProgress)
                .height(dimens.progressBarHeight)
                .clip(RentoShapes.pill)
                .background(colors.primary),
        )
    }
}
