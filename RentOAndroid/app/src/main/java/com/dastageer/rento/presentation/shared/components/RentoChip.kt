package com.dastageer.rento.presentation.shared.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dastageer.rento.presentation.shared.animations.RentoAnimations
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoDimens
import com.dastageer.rento.presentation.shared.theme.LocalRentoTypography
import com.dastageer.rento.presentation.shared.theme.RentoShapes

/**
 * RentoChip — selectable chip with optional leading icon.
 * Idle: DarkBg3 fill, DarkBd2 border, DarkT1 text.
 * Selected: DarkPriM fill, DarkPri border, DarkPri bold text.
 * 220ms colour transition.
 */
@Composable
fun RentoChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val colors = LocalRentoColors.current
    val typo = LocalRentoTypography.current
    val dimens = LocalRentoDimens.current

    val bgColor by animateColorAsState(
        targetValue = if (selected) colors.primaryTint else colors.bg3,
        animationSpec = tween(RentoAnimations.CHIP_TRANSITION_DURATION),
        label = "chipBg",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) colors.primary else colors.border2,
        animationSpec = tween(RentoAnimations.CHIP_TRANSITION_DURATION),
        label = "chipBorder",
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) colors.primary else colors.t1,
        animationSpec = tween(RentoAnimations.CHIP_TRANSITION_DURATION),
        label = "chipContent",
    )
    val fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium

    Row(
        modifier = modifier
            .clip(RentoShapes.pill)
            .background(bgColor)
            .border(1.5.dp, borderColor, RentoShapes.pill)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = dimens.chipPadV, horizontal = dimens.chipPadH),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = contentColor,
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = label,
            style = typo.bodySMedium.copy(
                color = contentColor,
                fontWeight = fontWeight,
                fontSize = 12.5.sp,
            ),
        )
    }
}
