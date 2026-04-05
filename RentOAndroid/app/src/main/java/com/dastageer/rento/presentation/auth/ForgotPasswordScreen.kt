package com.dastageer.rento.presentation.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dastageer.rento.R
import com.dastageer.rento.presentation.shared.animations.BounceEffect
import com.dastageer.rento.presentation.shared.animations.RentoAnimations
import com.dastageer.rento.presentation.shared.components.BoxedInputField
import com.dastageer.rento.presentation.shared.components.ErrorBanner
import com.dastageer.rento.presentation.shared.components.MeshBackground
import com.dastageer.rento.presentation.shared.components.OutlinePrimaryButton
import com.dastageer.rento.presentation.shared.components.PrimaryButton
import com.dastageer.rento.presentation.auth.components.AuthBackButton
import com.dastageer.rento.presentation.auth.components.AuthHeader
import com.dastageer.rento.presentation.shared.icons.RentoIcons
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoTypography
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = koinViewModel(),
) {
    val colors = LocalRentoColors.current
    val typography = LocalRentoTypography.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var email by rememberSaveable { mutableStateOf("") }
    val floatY by RentoAnimations.rememberFloatY()
    val density = androidx.compose.ui.platform.LocalDensity.current.density

    val isSuccess = uiState is ForgotPasswordUiState.Success || uiState is ForgotPasswordUiState.Throttled

    MeshBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(13.dp))
            Box(Modifier.fillMaxSize()) {
                AuthBackButton(onClick = onBack)
            }

            AnimatedContent(
                targetState = isSuccess,
                transitionSpec = { fadeIn(androidx.compose.animation.core.tween(260)) togetherWith fadeOut(androidx.compose.animation.core.tween(260)) },
                label = "forgotContent",
            ) { success ->
                if (!success) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(Modifier.height(60.dp))
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
                        Spacer(Modifier.height(40.dp))
                        AuthHeader(
                            title = stringResource(R.string.forgot_title),
                            subtitle = stringResource(R.string.forgot_subtitle),
                        )
                        Spacer(Modifier.height(32.dp))
                        BoxedInputField(
                            value = email, onValueChange = { email = it },
                            placeholder = stringResource(R.string.auth_email_placeholder),
                            leadingIcon = RentoIcons.Mail,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { viewModel.sendReset(email) }),
                        )
                        Spacer(Modifier.height(16.dp))
                        AnimatedVisibility(visible = uiState is ForgotPasswordUiState.Error, enter = expandVertically() + fadeIn()) {
                            ErrorBanner(
                                message = (uiState as? ForgotPasswordUiState.Error)?.message ?: "",
                                modifier = Modifier.padding(bottom = 16.dp),
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        PrimaryButton(
                            text = stringResource(R.string.forgot_button),
                            onClick = { viewModel.sendReset(email) },
                            enabled = uiState !is ForgotPasswordUiState.Loading,
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(Modifier.height(60.dp))
                        BounceEffect(trigger = true) { mod ->
                            Box(
                                modifier = mod
                                    .size(80.dp)
                                    .background(colors.primaryTint, RoundedCornerShape(100.dp))
                                    .border(1.5.dp, colors.primaryRing, RoundedCornerShape(100.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(RentoIcons.Check, null, tint = colors.primary, modifier = Modifier.size(36.dp))
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                        Text(
                            stringResource(R.string.forgot_success_title),
                            style = typography.displaySLarge,
                            color = colors.t0,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(8.dp))
                        val sentEmail = (uiState as? ForgotPasswordUiState.Success)?.sentToEmail
                            ?: (uiState as? ForgotPasswordUiState.Throttled)?.let { email } ?: email
                        Text(
                            stringResource(R.string.forgot_success_body, sentEmail),
                            fontSize = 14.sp, color = colors.t2, textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(32.dp))

                        val throttled = uiState as? ForgotPasswordUiState.Throttled
                        OutlinePrimaryButton(
                            text = if (throttled != null) stringResource(R.string.forgot_resend_throttled, throttled.secondsRemaining)
                            else stringResource(R.string.forgot_resend),
                            onClick = { viewModel.resend() },
                            enabled = throttled == null,
                        )

                        Spacer(Modifier.height(20.dp))
                        Text(
                            stringResource(R.string.forgot_back_to_signin),
                            fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.primary,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null, onClick = onBack,
                            ),
                        )
                    }
                }
            }
        }
    }
}
