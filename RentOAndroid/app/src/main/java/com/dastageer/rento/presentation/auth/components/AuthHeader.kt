package com.dastageer.rento.presentation.auth.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoTypography

@Composable
fun AuthHeader(title: String, subtitle: String?, modifier: Modifier = Modifier) {
    val colors = LocalRentoColors.current
    val typography = LocalRentoTypography.current

    Column(modifier) {
        Text(text = title, style = typography.displayL, color = colors.t0)
        if (subtitle != null) {
            Spacer(Modifier.height(8.dp))
            Text(text = subtitle, fontSize = 14.sp, color = colors.t2)
        }
    }
}
