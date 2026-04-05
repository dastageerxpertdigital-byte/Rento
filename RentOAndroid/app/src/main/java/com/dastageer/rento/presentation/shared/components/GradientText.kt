package com.dastageer.rento.presentation.shared.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.gradientText

/**
 * GradientText — text rendered with the gradient text brush
 * (DarkPri → DarkPri2 → DarkPri3, horizontal).
 * Used for hero headings like "Your Next Home".
 */
@Composable
fun GradientText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    val colors = LocalRentoColors.current
    val brush = gradientText(colors)

    Text(
        text = text,
        modifier = modifier,
        style = style.copy(brush = brush),
    )
}
