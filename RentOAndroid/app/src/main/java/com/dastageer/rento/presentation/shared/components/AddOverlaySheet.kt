package com.dastageer.rento.presentation.shared.components

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dastageer.rento.R
import com.dastageer.rento.presentation.shared.animations.NavTransitions
import com.dastageer.rento.presentation.shared.icons.RentoIcons
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoTypography

@Composable
fun AddOverlaySheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onLookingTap: () -> Unit,
    onHostingTap: () -> Unit,
) {
    val colors = LocalRentoColors.current
    val typography = LocalRentoTypography.current

    AnimatedVisibility(
        visible = visible,
        enter = NavTransitions.overlayEnter,
        exit = NavTransitions.overlayExit
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Modifier
                            .blur(6.dp)
                            .background(colors.overlay.copy(alpha = 0.72f))
                    } else {
                        Modifier.background(colors.overlay.copy(alpha = 0.72f))
                    }
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Sheet container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(colors.bg1)
                    .border(
                        width = 1.5.dp,
                        color = colors.border2,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* Consume tap so it doesn't dismiss */ }
                    )
                    .padding(horizontal = 20.dp).padding(bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 24.dp)
                        .size(width = 40.dp, height = 5.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(colors.bg4)
                )

                // Title
                Text(
                    text = stringResource(R.string.add_overlay_title),
                    style = typography.displayS.copy(
                        fontSize = 26.sp,
                        color = colors.t0,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                // Subtitle
                Text(
                    text = stringResource(R.string.add_overlay_subtitle),
                    fontSize = 14.sp,
                    color = colors.t2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 28.dp)
                )

                // Intent cards
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // LOOKING card
                    IntentCard(
                        title = stringResource(R.string.add_overlay_looking_title),
                        subtitle = stringResource(R.string.add_overlay_looking_subtitle),
                        icon = RentoIcons.Search,
                        iconBg = Color(0x26598FD4),
                        iconTint = colors.blue,
                        onClick = onLookingTap
                    )

                    // HOSTING card
                    IntentCard(
                        title = stringResource(R.string.add_overlay_hosting_title),
                        subtitle = stringResource(R.string.add_overlay_hosting_subtitle),
                        icon = RentoIcons.Home,
                        iconBg = colors.primaryTint,
                        iconTint = colors.primary,
                        onClick = onHostingTap
                    )
                }
            }
        }
    }
}

@Composable
private fun IntentCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit,
) {
    val colors = LocalRentoColors.current

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.985f else 1f,
        animationSpec = androidx.compose.animation.core.spring(),
        label = "intentScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(24.dp))
            .background(colors.bg2)
            .border(1.5.dp, colors.border2, RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(),
                onClick = onClick
            )
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Circle
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.size(18.dp))

        // Text
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.t0
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = colors.t2
            )
        }

        Icon(
            imageVector = RentoIcons.Chevron,
            contentDescription = null,
            tint = colors.t3,
            modifier = Modifier.size(22.dp)
        )
    }
}
