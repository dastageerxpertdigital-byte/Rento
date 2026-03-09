package com.dastageer.rento.presentation.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dastageer.rento.presentation.shared.icons.RentoIcons
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoDimens
import com.dastageer.rento.presentation.shared.theme.LocalRentoTypography
import com.dastageer.rento.presentation.shared.theme.RentoShapes

/**
 * ErrorBanner — inline error message with rounded container.
 * DarkRedM fill, DarkRed border, DarkRed text + icon.
 */
@Composable
fun ErrorBanner(
    message: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalRentoColors.current
    val typo = LocalRentoTypography.current
    val dimens = LocalRentoDimens.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RentoShapes.errorBanner)
            .background(colors.redTint)
            .padding(vertical = dimens.errorBannerPadV, horizontal = dimens.errorBannerPadH),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = RentoIcons.Close,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = colors.red,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = message,
            style = typo.bodySMedium.copy(color = colors.red),
        )
    }
}
