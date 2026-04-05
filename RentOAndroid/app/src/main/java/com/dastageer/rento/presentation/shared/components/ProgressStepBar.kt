package com.dastageer.rento.presentation.shared.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.dastageer.rento.presentation.shared.animations.RentoAnimations
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoDimens
import com.dastageer.rento.presentation.shared.theme.LocalRentoTypography
import com.dastageer.rento.presentation.shared.theme.RentoShapes

/**
 * Progress step bar for multi-step forms.
 * Active: 22dp wide, DarkPri. Completed: 5dp wide, DarkPri2 55%. Future: 5dp wide, DarkBg4.
 * Label: "Step N of T", 11sp SemiBold DarkT2.
 */
@Composable
fun ProgressStepBar(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalRentoColors.current
    val dimens = LocalRentoDimens.current
    val typo = LocalRentoTypography.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimens.progressStepDotGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (step in 0 until totalSteps) {
                val isActive = step == currentStep
                val isCompleted = step < currentStep

                val dotWidth by animateDpAsState(
                    targetValue = if (isActive) {
                        dimens.progressStepDotActiveWidth
                    } else {
                        dimens.progressStepDotInactiveWidth
                    },
                    animationSpec = tween(RentoAnimations.PROGRESS_STEP_DURATION),
                    label = "stepDotWidth$step",
                )

                val dotColor = when {
                    isActive -> colors.primary
                    isCompleted -> colors.primary2.copy(alpha = 0.55f)
                    else -> colors.bg4
                }

                Box(
                    modifier = Modifier
                        .width(dotWidth)
                        .height(dimens.progressStepDotHeight)
                        .clip(RentoShapes.pill)
                        .background(dotColor),
                )
            }
        }

        // Step label
        Text(
            text = "Step ${currentStep + 1} of $totalSteps",
            style = typo.microMedium.copy(color = colors.t2),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}
