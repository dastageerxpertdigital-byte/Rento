package com.dastageer.rento.presentation.shared.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dastageer.rento.presentation.shared.animations.RentoAnimations
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoDimens
import com.dastageer.rento.presentation.shared.theme.LocalRentoTypography
import com.dastageer.rento.presentation.shared.theme.RentoShapes
import com.dastageer.rento.presentation.shared.theme.gradientPrimary

/**
 * Primary action button with gradient background (DarkPri → DarkSec, 135°).
 * Full-width pill shape. White bold text. Press: scale(0.97) + alpha(0.9).
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fullWidth: Boolean = true,
) {
    val colors = LocalRentoColors.current
    val typo = LocalRentoTypography.current
    val dimens = LocalRentoDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(RentoAnimations.INTERACTION_DURATION),
        label = "primaryBtnScale",
    )
    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(RentoAnimations.INTERACTION_DURATION),
        label = "primaryBtnAlpha",
    )

    Box(
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .scale(scale)
            .alpha(if (enabled) alpha else 0.5f)
            .clip(RentoShapes.pill)
            .background(gradientPrimary(colors))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(vertical = dimens.btnPadV, horizontal = dimens.btnPadH),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.material3.Text(
            text = text,
            style = typo.bodyL.copy(color = Color.White, textAlign = TextAlign.Center),
        )
    }
}

/**
 * Ghost button — transparent bg, DarkBd2 border, DarkT1 text.
 * Pressed: DarkBg3 fill.
 */
@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fullWidth: Boolean = true,
) {
    val colors = LocalRentoColors.current
    val typo = LocalRentoTypography.current
    val dimens = LocalRentoDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val bgColor = if (isPressed) colors.bg3 else Color.Transparent

    Box(
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .clip(RentoShapes.pill)
            .background(bgColor)
            .border(1.5.dp, colors.border2, RentoShapes.pill)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(vertical = dimens.btnPadV, horizontal = dimens.btnPadH),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.material3.Text(
            text = text,
            style = typo.bodySMedium.copy(color = colors.t1, textAlign = TextAlign.Center),
        )
    }
}

/**
 * Outline primary button — transparent bg, DarkPri border, DarkPri text.
 * Pressed: DarkPriM background tint.
 */
@Composable
fun OutlinePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fullWidth: Boolean = true,
) {
    val colors = LocalRentoColors.current
    val typo = LocalRentoTypography.current
    val dimens = LocalRentoDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val bgColor = if (isPressed) colors.primaryTint else Color.Transparent

    Box(
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .clip(RentoShapes.pill)
            .background(bgColor)
            .border(1.5.dp, colors.primary, RentoShapes.pill)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(vertical = dimens.btnPadV, horizontal = dimens.btnPadH),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.material3.Text(
            text = text,
            style = typo.bodySMedium.copy(color = colors.primary, textAlign = TextAlign.Center),
        )
    }
}

/**
 * Destructive button — DarkRed fill, white text.
 * Used for "Delete" and "Block" actions per spec rule #23.
 */
@Composable
fun DestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fullWidth: Boolean = true,
) {
    val colors = LocalRentoColors.current
    val typo = LocalRentoTypography.current
    val dimens = LocalRentoDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(RentoAnimations.INTERACTION_DURATION),
        label = "destructiveBtnScale",
    )

    Box(
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .scale(scale)
            .alpha(if (enabled) 1f else 0.5f)
            .clip(RentoShapes.pill)
            .background(colors.red)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(vertical = dimens.btnPadV, horizontal = dimens.btnPadH),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.material3.Text(
            text = text,
            style = typo.bodyL.copy(color = Color.White, textAlign = TextAlign.Center),
        )
    }
}
