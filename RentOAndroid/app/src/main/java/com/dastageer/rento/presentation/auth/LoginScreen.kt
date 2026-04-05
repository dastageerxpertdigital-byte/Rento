package com.dastageer.rento.presentation.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dastageer.rento.R
import com.dastageer.rento.domain.usecase.auth.AuthDestination
import com.dastageer.rento.presentation.auth.components.AuthBackButton
import com.dastageer.rento.presentation.auth.components.AuthDivider
import com.dastageer.rento.presentation.auth.components.AuthHeader
import com.dastageer.rento.presentation.auth.components.GoogleSignInButton
import com.dastageer.rento.presentation.shared.animations.fadeInSlideUpModifier
import com.dastageer.rento.presentation.shared.components.BoxedInputField
import com.dastageer.rento.presentation.shared.components.ErrorBanner
import com.dastageer.rento.presentation.shared.components.MeshBackground
import com.dastageer.rento.presentation.shared.components.PrimaryButton
import com.dastageer.rento.presentation.shared.icons.RentoIcons
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPw: () -> Unit,
    onNavigateToVerify: () -> Unit,
    onNavigateToBlocked: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val colors = LocalRentoColors.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    DisposableEffect(Unit) { onDispose { viewModel.resetState() } }

    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is AuthUiState.Success -> when (s.destination) {
                AuthDestination.HOME -> onNavigateToHome()
                AuthDestination.ONBOARDING -> onNavigateToOnboarding()
                AuthDestination.VERIFY_EMAIL -> onNavigateToVerify()
                AuthDestination.BLOCKED -> onNavigateToBlocked()
            }
            else -> Unit
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential: SignInCredential = Identity.getSignInClient(context)
                    .getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    viewModel.loginWithGoogle(
                        idToken,
                        credential.displayName,
                        credential.id,
                        credential.profilePictureUri?.toString(),
                    )
                }
            } catch (_: Exception) { /* handled by viewModel */ }
        }
    }

    MeshBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(13.dp))
            AuthBackButton(onClick = onBack)

            Spacer(Modifier.height(32.dp))
            AuthHeader(
                title = stringResource(R.string.login_title),
                subtitle = stringResource(R.string.login_subtitle),
                modifier = Modifier.fadeInSlideUpModifier(visible, 0),
            )

            Spacer(Modifier.height(32.dp))
            BoxedInputField(
                value = email,
                onValueChange = { email = it },
                placeholder = stringResource(R.string.auth_email_placeholder),
                leadingIcon = RentoIcons.Mail,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            )
            Spacer(Modifier.height(16.dp))
            BoxedInputField(
                value = password,
                onValueChange = { password = it },
                placeholder = stringResource(R.string.auth_password_placeholder),
                leadingIcon = RentoIcons.Lock,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { viewModel.loginWithEmail(email, password) }),
                visualTransformation = PasswordVisualTransformation(),
            )

            Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.weight(1f))
                Text(
                    stringResource(R.string.login_forgot_password),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onNavigateToForgotPw,
                    ),
                )
            }

            Spacer(Modifier.height(24.dp))

            AnimatedVisibility(
                visible = uiState is AuthUiState.Error,
                enter = expandVertically() + fadeIn(),
            ) {
                val errorMsg = (uiState as? AuthUiState.Error)?.message ?: ""
                ErrorBanner(message = errorMsg, modifier = Modifier.padding(bottom = 16.dp))
            }

            PrimaryButton(
                text = stringResource(R.string.login_button),
                onClick = { viewModel.loginWithEmail(email, password) },
                enabled = uiState !is AuthUiState.Loading,
            )

            Spacer(Modifier.height(28.dp))
            AuthDivider()
            Spacer(Modifier.height(20.dp))

            GoogleSignInButton(onClick = {
                val webClientId = context.getString(R.string.google_web_client_id)
                val request = com.google.android.gms.auth.api.identity.BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(
                        com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            .setServerClientId(webClientId)
                            .setFilterByAuthorizedAccounts(false)
                            .build()
                    ).build()
                Identity.getSignInClient(context).beginSignIn(request)
                    .addOnSuccessListener { result ->
                        googleSignInLauncher.launch(
                            IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        )
                    }
                    .addOnFailureListener { /* Play Services unavailable */ }
            })

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            ) {
                Text(stringResource(R.string.login_no_account) + " ", fontSize = 14.sp, color = colors.t2)
                Text(
                    stringResource(R.string.login_sign_up_link),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onNavigateToRegister,
                    ),
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
