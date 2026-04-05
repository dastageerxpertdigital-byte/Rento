package com.dastageer.rento.presentation.shared.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dastageer.rento.R
import com.dastageer.rento.presentation.shared.icons.RentoIcons
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import kotlinx.coroutines.delay

@Immutable
data class InAppNotification(
    val id: String,
    val title: String,
    val body: String,
)

@Composable
fun InAppNotificationBanner(
    notification: InAppNotification?,
    onDismiss: () -> Unit,
    onTap: () -> Unit,
) {
    val colors = LocalRentoColors.current
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    // Auto-dismiss after 4s
    LaunchedEffect(notification?.id) {
        if (notification != null) {
            delay(4000)
            onDismiss()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = statusBarHeight + 8.dp, start = 12.dp, end = 12.dp)
    ) {
        AnimatedVisibility(
            visible = notification != null,
            enter = slideInVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                initialOffsetY = { -it }
            ) + fadeIn(tween(220)),
            exit = slideOutVertically(
                animationSpec = tween(220),
                targetOffsetY = { -it }
            ) + fadeOut(tween(180))
        ) {
            if (notification != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.bg1)
                        .border(1.dp, colors.border2, RoundedCornerShape(16.dp))
                        .pointerInput(Unit) {
                            detectVerticalDragGestures { _, dragAmount ->
                                if (dragAmount < -10) {
                                    // Swipe up detected
                                    onDismiss()
                                }
                            }
                        }
                        .clickable(onClick = onTap)
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon box
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.primaryTint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = RentoIcons.Bell,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Content Column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp, end = 12.dp)
                    ) {
                        Text(
                            text = notification.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.t0,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = notification.body,
                            fontSize = 12.sp,
                            color = colors.t2,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Close Button
                    Icon(
                        imageVector = RentoIcons.Close,
                        contentDescription = stringResource(R.string.notif_banner_close_cd),
                        tint = colors.t2,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable(onClick = onDismiss)
                    )
                }
            }
        }
    }
}
