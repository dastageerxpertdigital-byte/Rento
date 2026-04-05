package com.dastageer.rento.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dastageer.rento.presentation.shared.animations.RentoAnimations
import com.dastageer.rento.presentation.shared.components.GradientText
import com.dastageer.rento.presentation.shared.icons.RentoIcons
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoTypography
import com.dastageer.rento.presentation.shared.theme.gradientPrimary
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun SplashScreen(
    onNavigateToWelcome: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToVerify: () -> Unit,
    onNavigateToBlocked: () -> Unit,
    viewModel: SplashViewModel = koinViewModel(),
) {
    val colors = LocalRentoColors.current
    val typography = LocalRentoTypography.current
    val density = LocalDensity.current.density
    val destination by viewModel.destination.collectAsStateWithLifecycle()
    val floatY by RentoAnimations.rememberFloatY()

    LaunchedEffect(destination) {
        when (destination) {
            is SplashViewModel.SplashDestination.Welcome -> onNavigateToWelcome()
            is SplashViewModel.SplashDestination.Home -> onNavigateToHome()
            is SplashViewModel.SplashDestination.Onboarding -> onNavigateToOnboarding()
            is SplashViewModel.SplashDestination.VerifyEmail -> onNavigateToVerify()
            is SplashViewModel.SplashDestination.Blocked -> onNavigateToBlocked()
            null -> Unit
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(colors.bg0),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(0, (floatY * density).roundToInt()) }
                    .shadow(12.dp, RoundedCornerShape(30.dp), spotColor = colors.primaryRing)
                    .size(92.dp)
                    .background(gradientPrimary(colors), RoundedCornerShape(30.dp))
                    .border(1.5.dp, colors.primaryRing, RoundedCornerShape(30.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(RentoIcons.Home, contentDescription = null, tint = Color.White, modifier = Modifier.size(46.dp))
            }
            Spacer(Modifier.height(16.dp))
            GradientText("RentO", style = typography.displayL)
        }
    }
}
