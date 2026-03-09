package com.dastageer.rento.presentation.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoDimens
import com.dastageer.rento.presentation.shared.theme.LocalRentoTypography

/**
 * EmptyState — illustrated empty state with icon, title, and subtitle.
 * Icon: 80dp rounded square (28dp corner), DarkPriM fill, DarkPri icon.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalRentoColors.current
    val typo = LocalRentoTypography.current
    val dimens = LocalRentoDimens.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.screenPadH),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(dimens.emptyStateIconSize)
                .clip(RoundedCornerShape(dimens.emptyStateIconRadius))
                .background(colors.primaryTint)
                .border(1.5.dp, colors.primaryRing, RoundedCornerShape(dimens.emptyStateIconRadius)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = colors.primary,
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = title,
            style = typo.displayS.copy(color = colors.t0),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = typo.bodyM.copy(color = colors.t2),
            textAlign = TextAlign.Center,
        )
    }
}
