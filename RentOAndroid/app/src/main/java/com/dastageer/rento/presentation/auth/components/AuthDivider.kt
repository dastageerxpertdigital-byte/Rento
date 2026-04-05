package com.dastageer.rento.presentation.auth.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dastageer.rento.R
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors

@Composable
fun AuthDivider(modifier: Modifier = Modifier) {
    val colors = LocalRentoColors.current
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(Modifier.weight(1f), thickness = 1.dp, color = colors.border2)
        Spacer(Modifier.width(12.dp))
        Text(stringResource(R.string.auth_or_divider), fontSize = 12.sp, color = colors.t2)
        Spacer(Modifier.width(12.dp))
        HorizontalDivider(Modifier.weight(1f), thickness = 1.dp, color = colors.border2)
    }
}
