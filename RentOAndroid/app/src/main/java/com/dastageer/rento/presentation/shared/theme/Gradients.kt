package com.dastageer.rento.presentation.shared.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary action button gradient: top-left → bottom-right (135° equivalent)
fun gradientPrimary(colors: RentoColors): Brush = Brush.linearGradient(
    colors = listOf(colors.primary, colors.secondary),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
)

// Gradient text brush (hero headings — "Your Next Home" etc.)
// Matches prototype: background: linear-gradient(130deg, pri 0%, pri2 55%, pri3 100%)
fun gradientText(colors: RentoColors): Brush = Brush.linearGradient(
    colorStops = arrayOf(
        0.00f to colors.primary,
        0.55f to colors.primary2,
        1.00f to colors.primary3,
    ),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, 0f),
)

// Glass dialog border brush — simulates light catching glass edge
fun glassDialogBorder(colors: RentoColors): Brush = Brush.linearGradient(
    colorStops = arrayOf(
        0.0f to colors.glassDialogBorderHighlight,
        0.4f to colors.glassDialogBorderMid,
        1.0f to colors.glassDialogBorderFade,
    ),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
)

// Property card background gradients — 4 variants cycled by listing index
// Exact values from prototype PD array — always deep green, no light/dark change
val GradientCard1: Brush = Brush.linearGradient(
    colors = listOf(Color(0xFF03200E), Color(0xFF083C1C), Color(0xFF052814)),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
)
val GradientCard2: Brush = Brush.linearGradient(
    colors = listOf(Color(0xFF031E18), Color(0xFF073828), Color(0xFF042818)),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
)
val GradientCard3: Brush = Brush.linearGradient(
    colors = listOf(Color(0xFF041C0E), Color(0xFF093618), Color(0xFF062412)),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
)
val GradientCard4: Brush = Brush.linearGradient(
    colors = listOf(Color(0xFF031A14), Color(0xFF073020), Color(0xFF041C12)),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
)

val CardGradients: List<Brush> = listOf(GradientCard1, GradientCard2, GradientCard3, GradientCard4)

fun cardGradientForIndex(index: Int): Brush = CardGradients[index % CardGradients.size]

// Image overlay gradient — vertical, transparent-top to dark-bottom
fun gradientImageOverlay(colors: RentoColors): Brush = Brush.verticalGradient(
    colorStops = arrayOf(
        0.00f to colors.imageOverlayStart,
        0.55f to colors.imageOverlayMid,
        1.00f to colors.imageOverlayEnd,
    ),
)

// Banner slider featured pill gradient (subtle green tint)
fun gradientFeaturedPill(colors: RentoColors): Brush = Brush.linearGradient(
    colors = listOf(colors.primaryTint, colors.primaryTint),
)
