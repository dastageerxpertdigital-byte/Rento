package com.dastageer.rento.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dastageer.rento.R
import com.dastageer.rento.domain.repository.UserRepository
import com.dastageer.rento.presentation.shared.components.GhostButton
import com.dastageer.rento.presentation.shared.components.MeshBackground
import com.dastageer.rento.presentation.shared.icons.RentoIcons
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoTypography
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun BlockedScreen(
    onSignOut: () -> Unit,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val colors = LocalRentoColors.current
    val typography = LocalRentoTypography.current
    val userRepository: UserRepository = koinInject()
    val fallbackEmail = stringResource(R.string.blocked_admin_fallback)
    var adminEmail by remember { mutableStateOf(fallbackEmail) }

    LaunchedEffect(Unit) {
        userRepository.getAdminEmail().onSuccess { adminEmail = it }
    }

    MeshBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(colors.redTint, RoundedCornerShape(100.dp))
                    .border(1.5.dp, colors.red, RoundedCornerShape(100.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(RentoIcons.Shield, null, tint = colors.red, modifier = Modifier.size(36.dp))
            }

            Spacer(Modifier.height(24.dp))
            Text(
                stringResource(R.string.blocked_title),
                style = typography.displaySLarge, color = colors.t0, textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                stringResource(R.string.blocked_subtitle),
                fontSize = 14.sp, color = colors.t2, textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(6.dp))
            Text(adminEmail, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.primary, textAlign = TextAlign.Center)

            Spacer(Modifier.height(40.dp))

            GhostButton(
                text = stringResource(R.string.blocked_sign_out),
                onClick = {
                    viewModel.signOut()
                    onSignOut()
                },
            )
        }
    }
}
