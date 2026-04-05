package com.dastageer.rento.presentation.auth

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dastageer.rento.R
import com.dastageer.rento.presentation.shared.animations.RentoAnimations
import com.dastageer.rento.presentation.shared.animations.fadeInSlideUpModifier
import com.dastageer.rento.presentation.shared.components.GradientText
import com.dastageer.rento.presentation.shared.icons.RentoIcons
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoTypography
import com.dastageer.rento.presentation.shared.theme.gradientPrimary
import kotlin.math.roundToInt

@Composable
fun WelcomeScreen(
    onSignIn: () -> Unit,
    onLooking: () -> Unit,
    onHosting: () -> Unit,
) {
    val colors = LocalRentoColors.current
    val typography = LocalRentoTypography.current
    val density = LocalDensity.current.density
    val floatY by RentoAnimations.rememberFloatY()
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    com.dastageer.rento.presentation.shared.components.MeshBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.weight(1f).padding(top = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(0, (floatY * density).roundToInt()) }
                        .shadow(12.dp, RoundedCornerShape(30.dp), spotColor = colors.primaryRing)
                        .size(92.dp)
                        .background(gradientPrimary(colors), RoundedCornerShape(30.dp))
                        .border(1.5.dp, colors.primaryRing, RoundedCornerShape(30.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(RentoIcons.Home, null, tint = Color.White, modifier = Modifier.size(46.dp))
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    stringResource(R.string.welcome_hero_line1),
                    style = typography.displayXL,
                    color = colors.t0,
                    textAlign = TextAlign.Center,
                )
                GradientText(stringResource(R.string.welcome_hero_line2), style = typography.displayXL)
                Spacer(Modifier.height(14.dp))
                Text(
                    stringResource(R.string.welcome_subtitle),
                    fontSize = 14.sp,
                    color = colors.t2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 272.dp),
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                IntentCard(
                    icon = RentoIcons.Search,
                    title = stringResource(R.string.welcome_looking_title),
                    subtitle = stringResource(R.string.welcome_looking_subtitle),
                    onClick = onLooking,
                    modifier = Modifier.fadeInSlideUpModifier(visible, 0),
                )
                IntentCard(
                    icon = RentoIcons.Home,
                    title = stringResource(R.string.welcome_hosting_title),
                    subtitle = stringResource(R.string.welcome_hosting_subtitle),
                    onClick = onHosting,
                    modifier = Modifier.fadeInSlideUpModifier(visible, 100),
                )
            }

            Column(
                modifier = Modifier.padding(bottom = 32.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.welcome_have_account) + " ", fontSize = 14.sp, color = colors.t2)
                    Text(
                        stringResource(R.string.welcome_sign_in_link),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onSignIn,
                        ),
                    )
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(stringResource(R.string.welcome_terms), fontSize = 11.sp, color = colors.t3, textDecoration = TextDecoration.Underline)
                    Text(stringResource(R.string.welcome_terms_separator), fontSize = 11.sp, color = colors.t3)
                    Text(stringResource(R.string.welcome_privacy), fontSize = 11.sp, color = colors.t3, textDecoration = TextDecoration.Underline)
                }
            }
        }
    }
}

@Composable
private fun IntentCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalRentoColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (isPressed) 0.985f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "intentScale",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .background(colors.bg2, RoundedCornerShape(24.dp))
            .border(1.5.dp, colors.border2, RoundedCornerShape(24.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(colors.primaryTint, RoundedCornerShape(18.dp))
                .border(1.5.dp, colors.primaryRing, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = colors.primary, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.width(18.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.t0)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, fontSize = 13.sp, color = colors.t2)
        }
        Icon(RentoIcons.Chevron, null, tint = colors.t3, modifier = Modifier.size(20.dp))
    }
}
