package com.dastageer.rento.presentation.shared.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoTypography

/**
 * Section label — 11sp, Bold, DarkT2, UPPERCASE, letter-spacing 0.06em.
 */
@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalRentoColors.current
    val typo = LocalRentoTypography.current

    Text(
        text = text.uppercase(),
        modifier = modifier,
        style = typo.label.copy(color = colors.t2),
    )
}
