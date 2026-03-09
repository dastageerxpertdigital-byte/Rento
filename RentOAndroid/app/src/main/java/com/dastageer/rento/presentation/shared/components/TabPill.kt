package com.dastageer.rento.presentation.shared.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dastageer.rento.presentation.shared.animations.RentoAnimations
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoTypography
import com.dastageer.rento.presentation.shared.theme.RentoShapes

/**
 * TabPill — "Looking / Hosting" toggle on home screen.
 * Container: DarkBg3 fill, DarkBd border, pill shape.
 * Active tab: DarkPri fill, white bold text, 260ms transition.
 * Inactive tab: transparent, DarkT2 text.
 */
@Composable
fun TabPill(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalRentoColors.current
    val typo = LocalRentoTypography.current

    Row(
        modifier = modifier
            .clip(RentoShapes.pill)
            .background(colors.bg3)
            .border(1.5.dp, colors.border, RentoShapes.pill)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEachIndexed { index, label ->
            val isActive = index == selectedIndex

            val tabBg by animateColorAsState(
                targetValue = if (isActive) colors.primary else Color.Transparent,
                animationSpec = tween(RentoAnimations.TAB_PILL_DURATION),
                label = "tabPillBg$index",
            )
            val textColor by animateColorAsState(
                targetValue = if (isActive) Color.White else colors.t2,
                animationSpec = tween(RentoAnimations.TAB_PILL_DURATION),
                label = "tabPillText$index",
            )

            Box(
                modifier = Modifier
                    .clip(RentoShapes.pill)
                    .background(tabBg)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onTabSelected(index) },
                    )
                    .padding(vertical = 8.dp, horizontal = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = typo.bodySMedium.copy(
                        color = textColor,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                    ),
                )
            }
        }
    }
}
