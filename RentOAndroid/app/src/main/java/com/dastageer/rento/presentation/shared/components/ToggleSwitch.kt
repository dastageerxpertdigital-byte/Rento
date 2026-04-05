package com.dastageer.rento.presentation.shared.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoDimens
import com.dastageer.rento.presentation.shared.theme.RentoShapes

/**
 * ToggleSwitch — 46×26dp, pill shape.
 * On: DarkPri fill + shadow. Off: DarkBg4 fill.
 * Thumb: 20dp white circle, animated position.
 */
@Composable
fun ToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalRentoColors.current
    val dimens = LocalRentoDimens.current

    val trackColor = if (checked) colors.primary else colors.bg4
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) {
            dimens.toggleWidth - dimens.toggleThumbSize - dimens.toggleThumbOffset
        } else {
            dimens.toggleThumbOffset
        },
        animationSpec = spring(dampingRatio = 0.7f),
        label = "toggleThumbOffset",
    )

    Box(
        modifier = modifier
            .size(width = dimens.toggleWidth, height = dimens.toggleHeight)
            .clip(RentoShapes.toggleSwitch)
            .background(trackColor)
            .then(
                if (checked) {
                    Modifier.shadow(
                        10.dp,
                        RentoShapes.toggleSwitch,
                        ambientColor = colors.primaryRing,
                        spotColor = colors.primaryRing
                    )
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!checked) },
            ),
    ) {
        Box(
            modifier = Modifier
                .size(dimens.toggleThumbSize)
                .offset(x = thumbOffset, y = dimens.toggleThumbOffset)
                .clip(CircleShape)
                .background(Color.White),
        )
    }
}
