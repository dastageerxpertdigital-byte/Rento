package com.dastageer.rento.presentation.shared.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dastageer.rento.R
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors

@Composable
fun RentoDeleteSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
) {
    val colors = LocalRentoColors.current
    
    val transition = rememberInfiniteTransition(label = "spinnerTransition")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinnerRotation"
    )

    Canvas(
        modifier = modifier
            .size(size)
            .graphicsLayer { rotationZ = rotation }
            .semantics { contentDescription = "Loading, please wait" } 
    ) {
        val outerStroke = Stroke(width = 2.dp.toPx())
        val arcStroke = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        
        // Outer circle
        drawCircle(
            color = colors.border2,
            radius = (size.toPx() / 2) - outerStroke.width / 2,
            style = outerStroke
        )
        
        // Sweeping arc
        val sweepGradient = Brush.sweepGradient(
            colors = listOf(colors.primary, colors.primary2, colors.primary),
        )
        
        drawArc(
            brush = sweepGradient,
            startAngle = 0f,
            sweepAngle = 270f,
            useCenter = false,
            style = arcStroke,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RentoDeleteSpinnerPreview() {
    com.dastageer.rento.presentation.shared.theme.RentoTheme(darkTheme = true) {
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(16.dp)) {
            RentoDeleteSpinner()
        }
    }
}
