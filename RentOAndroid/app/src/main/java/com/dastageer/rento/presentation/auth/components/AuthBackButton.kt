package com.dastageer.rento.presentation.auth.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dastageer.rento.R
import com.dastageer.rento.presentation.shared.icons.RentoIcons
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors

@Composable
fun AuthBackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalRentoColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, tween(200), label = "backScale")

    Box(
        modifier = modifier
            .size(42.dp)
            .scale(scale)
            .background(colors.bg2, RoundedCornerShape(15.dp))
            .border(1.5.dp, colors.border2, RoundedCornerShape(15.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = RentoIcons.Back,
            contentDescription = stringResource(R.string.auth_back_cd),
            tint = colors.t0,
            modifier = Modifier.size(20.dp),
        )
    }
}
