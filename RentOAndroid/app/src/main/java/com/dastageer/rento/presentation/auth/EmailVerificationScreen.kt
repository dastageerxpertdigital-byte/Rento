package com.dastageer.rento.presentation.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dastageer.rento.R
import com.dastageer.rento.domain.usecase.auth.AuthDestination
import com.dastageer.rento.presentation.shared.animations.RentoAnimations
import com.dastageer.rento.presentation.shared.components.ErrorBanner
import com.dastageer.rento.presentation.shared.components.MeshBackground
import com.dastageer.rento.presentation.shared.components.OutlinePrimaryButton
import com.dastageer.rento.presentation.shared.components.PrimaryButton
import com.dastageer.rento.presentation.shared.icons.RentoIcons
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoTypography
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun EmailVerificationScreen(
    onVerified: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val colors = LocalRentoColors.current
    val typography = LocalRentoTypography.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val floatY by RentoAnimations.rememberFloatY()
    val density = androidx.compose.ui.platform.LocalDensity.current.density
    val currentEmail = remember { FirebaseAuth.getInstance().currentUser?.email ?: "" }

    var resendEnabled by remember { mutableStateOf(true) }
    var resendCountdown by remember { mutableIntStateOf(0) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success && (uiState as AuthUiState.Success).destination == AuthDestination.ONBOARDING) {
            onVerified()
        }
    }

    LaunchedEffect(Unit) {
        repeat(10) {
            delay(5_000)
            viewModel.checkEmailVerified()
        }
    }

    LaunchedEffect(resendEnabled) {
        if (!resendEnabled) {
            for (i in 60 downTo 1) {
                resendCountdown = i
                delay(1_000)
            }
            resendEnabled = true
            resendCountdown = 0
        }
    }

    MeshBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(80.dp))

            Box(
                modifier = Modifier
                    .offset { IntOffset(0, (floatY * density).roundToInt()) }
                    .size(80.dp)
                    .background(colors.bg3, RoundedCornerShape(100.dp))
                    .border(1.5.dp, colors.border2, RoundedCornerShape(100.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(RentoIcons.Mail, null, tint = colors.primary, modifier = Modifier.size(36.dp))
            }

            Spacer(Modifier.height(32.dp))
            Text(
                stringResource(R.string.verify_title),
                style = typography.displayM, color = colors.t0, textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.verify_subtitle),
                fontSize = 14.sp, color = colors.t2, textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 272.dp),
            )
            Spacer(Modifier.height(8.dp))
            Text(currentEmail, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.primary, textAlign = TextAlign.Center)

            Spacer(Modifier.height(40.dp))

            PrimaryButton(
                text = stringResource(R.string.verify_button),
                onClick = { viewModel.checkEmailVerified() },
                enabled = uiState !is AuthUiState.Loading,
            )

            Spacer(Modifier.height(14.dp))

            OutlinePrimaryButton(
                text = if (!resendEnabled) stringResource(R.string.verify_resend_throttled, resendCountdown)
                else stringResource(R.string.verify_resend),
                onClick = {
                    viewModel.resendVerification()
                    resendEnabled = false
                },
                enabled = resendEnabled,
            )

            Spacer(Modifier.height(40.dp))

            Text(stringResource(R.string.verify_wrong_account), fontSize = 13.sp, color = colors.t2)
            Text(
                stringResource(R.string.verify_sign_out),
                fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.primary,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    viewModel.signOut()
                    onSignOut()
                },
            )

            Spacer(Modifier.height(24.dp))

            AnimatedVisibility(visible = uiState is AuthUiState.Error, enter = expandVertically() + fadeIn()) {
                ErrorBanner(message = (uiState as? AuthUiState.Error)?.message ?: "")
            }
        }
    }
}
