package com.dastageer.rento.presentation.shared.components

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.dastageer.rento.presentation.shared.animations.RentoAnimations
import com.dastageer.rento.presentation.shared.icons.RentoIcons
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.gradientPrimary

data class NavItem(
    val id: String,
    val labelRes: Int,
    val icon: ImageVector,
)

@Composable
fun BottomNavBar(
    items: List<NavItem>,
    selectedItemId: String,
    onItemSelected: (String) -> Unit,
    onAddTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalRentoColors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(86.dp)
            .zIndex(Float.MAX_VALUE)
    ) {
        // Blur background layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .then(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Modifier
                            .background(colors.navBg)
                            .blur(24.dp)
                    } else {
                        Modifier.background(colors.navBgFallback)
                    }
                )
        )

        // Top border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.border)
                .align(Alignment.TopCenter)
        )

        // Nav Items & FAB
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(bottom = 18.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val firstHalf = items.take(2)
            val secondHalf = items.drop(2)

            // Render first 2 items
            firstHalf.forEach { item ->
                NavBarItem(
                    item = item,
                    isSelected = selectedItemId == item.id,
                    onClick = { onItemSelected(item.id) }
                )
            }

            // Centre FAB
            val glowFraction by RentoAnimations.rememberGlow()

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .drawBehind {
                        drawCircle(
                            color = colors.primaryRing,
                            radius = size.width / 2 + (12.dp.toPx() * glowFraction),
                            alpha = 0.5f * (1f - glowFraction)
                        )
                    }
                    .clip(CircleShape)
                    .background(gradientPrimary(colors))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = androidx.compose.material3.ripple(color = Color.White),
                        onClick = onAddTap
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = RentoIcons.Plus,
                    contentDescription = "Add listing or request",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Render last 2 items
            secondHalf.forEach { item ->
                NavBarItem(
                    item = item,
                    isSelected = selectedItemId == item.id,
                    onClick = { onItemSelected(item.id) }
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalRentoColors.current

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) colors.primaryTint else Color.Transparent,
        animationSpec = tween(220),
        label = "navBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) colors.primary else colors.t2,
        animationSpec = tween(220),
        label = "navContent"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 8.dp, horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null, // Label is read by screen reader instead
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = androidx.compose.ui.res.stringResource(id = item.labelRes),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )
    }
}
