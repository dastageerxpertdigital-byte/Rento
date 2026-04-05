package com.dastageer.rento.presentation.shared.components

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dastageer.rento.presentation.shared.animations.NavTransitions
import com.dastageer.rento.presentation.shared.icons.RentoIcons
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoTypography
import com.dastageer.rento.presentation.shared.theme.glassDialogBorder
import kotlinx.coroutines.delay

enum class GlassDialogPhase { CONFIRM, LOADING, SUCCESS, ERROR }

@Composable
fun GlassScrim(
    visible: Boolean,
    dismissible: Boolean,
    onDismiss: () -> Unit,
) {
    val colors = LocalRentoColors.current

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(androidx.compose.animation.core.tween(220)),
        exit = fadeOut(androidx.compose.animation.core.tween(180))
    ) {
        val blurModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Modifier.blur(16.dp)
        } else {
            Modifier
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(blurModifier)
                .background(colors.glassScrim)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (dismissible) onDismiss()
                    }
                )
        )
    }
}

@Composable
private fun CustomCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
) {
    val colors = LocalRentoColors.current

    Row(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!checked) }
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val shape = RoundedCornerShape(5.dp)
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(shape)
                .background(if (checked) colors.primary else colors.bg4)
                .border(1.5.dp, if (checked) colors.primary else colors.border2, shape),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = RentoIcons.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        Text(text = label, fontSize = 13.sp, color = colors.t1)
    }
}

@Composable
private fun GlassDialogContent(
    phase: GlassDialogPhase,
    iconVector: ImageVector,
    iconCircleColor: Color,
    title: String,
    body: String,
    contextInfo: String? = null,
    acknowledgeLabel: String? = null,
    onAcknowledgeChange: ((Boolean) -> Unit)? = null,
    acknowledgeChecked: Boolean = false,
    confirmText: String,
    cancelText: String,
    loadingText: String? = null,
    successTitle: String,
    errorTitle: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
) {
    val colors = LocalRentoColors.current
    val typography = LocalRentoTypography.current

    Column(
        modifier = Modifier.padding(horizontal = 28.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (phase) {
            GlassDialogPhase.CONFIRM -> {
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(iconCircleColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = Color.White
                    )
                }

                Text(
                    text = title,
                    style = typography.displayS.copy(
                        fontSize = 22.sp,
                        color = colors.t0,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (contextInfo != null) {
                    Text(
                        text = contextInfo,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.t1,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Text(
                    text = body,
                    fontSize = 14.sp,
                    color = colors.t1,
                    lineHeight = 23.sp, // ~1.65
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                if (acknowledgeLabel != null && onAcknowledgeChange != null) {
                    CustomCheckbox(
                        checked = acknowledgeChecked,
                        onCheckedChange = onAcknowledgeChange,
                        label = acknowledgeLabel
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // ORDER: Cancel first — rule enforced per spec
                    GhostButton(
                        text = cancelText,
                        onClick = onCancel,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // We can check if it's destructive via checking the specific icon if we desired,
                    // but usually, confirm button action dictates. If we need to always make it DestructiveButton or Primary,
                    // we can infer from icon or just default to DestructiveButton if context requires it.
                    // For the sake of this shared content, we'll use DestructiveButton or PrimaryButton depending
                    // on the icon. Let's assume Destructive if the circle color is red.
                    if (iconCircleColor == colors.red) {
                        DestructiveButton(
                            text = confirmText,
                            onClick = onConfirm,
                            enabled = acknowledgeLabel == null || acknowledgeChecked,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        PrimaryButton(
                            text = confirmText,
                            onClick = onConfirm,
                            enabled = acknowledgeLabel == null || acknowledgeChecked,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            GlassDialogPhase.LOADING -> {
                RentoDeleteSpinner(modifier = Modifier.padding(bottom = 16.dp), size = 32.dp)
                if (loadingText != null) {
                    Text(text = loadingText, fontSize = 14.sp, color = colors.t1, textAlign = TextAlign.Center)
                }
            }
            GlassDialogPhase.SUCCESS -> {
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = RentoIcons.Check,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = Color.White
                    )
                }

                Text(
                    text = successTitle,
                    style = typography.displayS.copy(
                        fontSize = 22.sp,
                        color = colors.t0,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            GlassDialogPhase.ERROR -> {
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(colors.red),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = RentoIcons.Close,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = Color.White
                    )
                }

                Text(
                    text = errorTitle,
                    style = typography.displayS.copy(
                        fontSize = 22.sp,
                        color = colors.t0,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // ORDER: Cancel first — rule enforced per spec
                    GhostButton(
                        text = "Cancel",
                        onClick = onCancel,
                        modifier = Modifier.fillMaxWidth()
                    )
                    PrimaryButton(
                        text = "Try Again",
                        onClick = onRetry,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun GlassDialog(
    visible: Boolean,
    phase: GlassDialogPhase,
    iconVector: ImageVector,
    iconCircleColor: Color,
    title: String,
    body: String,
    contextInfo: String? = null,
    acknowledgeLabel: String? = null,
    onAcknowledgeChange: ((Boolean) -> Unit)? = null,
    acknowledgeChecked: Boolean = false,
    confirmText: String,
    cancelText: String,
    loadingText: String? = null,
    successTitle: String,
    errorTitle: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit = {},
    onDismissSuccess: () -> Unit = {},
) {
    val colors = LocalRentoColors.current

    val isDismissible = phase != GlassDialogPhase.LOADING && phase != GlassDialogPhase.SUCCESS

    // Compose BackHandler handling blocking
    androidx.activity.compose.BackHandler(enabled = visible && !isDismissible) {
        // Block back press
    }

    // Auto-dismiss logic on SUCCESS
    LaunchedEffect(phase) {
        if (phase == GlassDialogPhase.SUCCESS) {
            delay(1800)
            onDismissSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        GlassScrim(visible = visible, dismissible = isDismissible, onDismiss = onCancel)

        AnimatedVisibility(
            visible = visible,
            enter = NavTransitions.dialogEnter,
            exit = NavTransitions.dialogExit
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .clip(RoundedCornerShape(28.dp))
                    .border(1.dp, glassDialogBorder(colors), RoundedCornerShape(28.dp))
                    .background(colors.glassDialogBg)
                    .graphicsLayer { alpha = 0.97f }
                    // Handle taps on the dialog itself so it doesn't dismiss
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
            ) {
                GlassDialogContent(
                    phase = phase,
                    iconVector = iconVector,
                    iconCircleColor = iconCircleColor,
                    title = title,
                    body = body,
                    contextInfo = contextInfo,
                    acknowledgeLabel = acknowledgeLabel,
                    onAcknowledgeChange = onAcknowledgeChange,
                    acknowledgeChecked = acknowledgeChecked,
                    confirmText = confirmText,
                    cancelText = cancelText,
                    loadingText = loadingText,
                    successTitle = successTitle,
                    errorTitle = errorTitle,
                    onConfirm = onConfirm,
                    onCancel = onCancel,
                    onRetry = onRetry,
                )
            }
        }
    }
}

@Composable
fun GlassBottomSheet(
    visible: Boolean,
    phase: GlassDialogPhase,
    iconVector: ImageVector,
    iconCircleColor: Color,
    title: String,
    body: String,
    contextInfo: String? = null,
    acknowledgeLabel: String? = null,
    onAcknowledgeChange: ((Boolean) -> Unit)? = null,
    acknowledgeChecked: Boolean = false,
    confirmText: String,
    cancelText: String,
    loadingText: String? = null,
    successTitle: String,
    errorTitle: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit = {},
    onDismissSuccess: () -> Unit = {},
) {
    val colors = LocalRentoColors.current

    val isDismissible = phase != GlassDialogPhase.LOADING && phase != GlassDialogPhase.SUCCESS

    androidx.activity.compose.BackHandler(enabled = visible && !isDismissible) {
        // Block back press
    }

    LaunchedEffect(phase) {
        if (phase == GlassDialogPhase.SUCCESS) {
            delay(1800)
            onDismissSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        GlassScrim(visible = visible, dismissible = isDismissible, onDismiss = onCancel)

        AnimatedVisibility(
            visible = visible,
            enter = NavTransitions.overlayEnter,
            exit = NavTransitions.overlayExit
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .border(
                        width = 1.dp,
                        brush = glassDialogBorder(colors),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
                    .background(colors.glassDialogBg)
                    .graphicsLayer { alpha = 0.97f }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .size(width = 40.dp, height = 5.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(colors.bg4)
                )

                // Extra padding wrapper for the bottom 48dp
                Box(modifier = Modifier.padding(bottom = 20.dp)) {
                    GlassDialogContent(
                        phase = phase,
                        iconVector = iconVector,
                        iconCircleColor = iconCircleColor,
                        title = title,
                        body = body,
                        contextInfo = contextInfo,
                        acknowledgeLabel = acknowledgeLabel,
                        onAcknowledgeChange = onAcknowledgeChange,
                        acknowledgeChecked = acknowledgeChecked,
                        confirmText = confirmText,
                        cancelText = cancelText,
                        loadingText = loadingText,
                        successTitle = successTitle,
                        errorTitle = errorTitle,
                        onConfirm = onConfirm,
                        onCancel = onCancel,
                        onRetry = onRetry,
                    )
                }
            }
        }
    }
}
