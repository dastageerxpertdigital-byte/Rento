package com.dastageer.rento.presentation.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoDimens
import com.dastageer.rento.presentation.shared.theme.RentoShapes

/**
 * Badge variants per spec:
 * - Primary (.bp): DarkPriM fill, DarkPri text, DarkPriR border
 * - Red (.br): DarkRedM fill, DarkRed text
 * - Accent (.ba): DarkAccM fill, DarkAcc text
 * - Blue (.bb): DarkBlueM fill, DarkBlue text
 * - Neutral (.bm): DarkBg3 fill, DarkT2 text, DarkBd2 border
 */
enum class BadgeVariant { PRIMARY, RED, ACCENT, BLUE, NEUTRAL }

@Composable
fun RentoBadge(
    text: String,
    variant: BadgeVariant,
    modifier: Modifier = Modifier,
) {
    val colors = LocalRentoColors.current
    val dimens = LocalRentoDimens.current

    val bgColor = when (variant) {
        BadgeVariant.PRIMARY -> colors.primaryTint
        BadgeVariant.RED -> colors.redTint
        BadgeVariant.ACCENT -> colors.accentTint
        BadgeVariant.BLUE -> colors.blueTint
        BadgeVariant.NEUTRAL -> colors.bg3
    }
    val textColor = when (variant) {
        BadgeVariant.PRIMARY -> colors.primary
        BadgeVariant.RED -> colors.red
        BadgeVariant.ACCENT -> colors.accent
        BadgeVariant.BLUE -> colors.blue
        BadgeVariant.NEUTRAL -> colors.t2
    }
    val borderColor = when (variant) {
        BadgeVariant.PRIMARY -> colors.primaryRing
        BadgeVariant.NEUTRAL -> colors.border2
        else -> null
    }

    val mod = modifier
        .clip(RentoShapes.badge)
        .background(bgColor)
        .then(
            if (borderColor != null) {
                Modifier.border(1.dp, borderColor, RentoShapes.badge)
            } else {
                Modifier
            }
        )
        .padding(vertical = dimens.badgePadV, horizontal = dimens.badgePadH)

    Text(
        text = text,
        modifier = mod,
        style = androidx.compose.ui.text.TextStyle(
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        ),
    )
}
