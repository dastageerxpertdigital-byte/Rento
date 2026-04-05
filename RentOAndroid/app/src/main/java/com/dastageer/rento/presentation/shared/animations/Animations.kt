package com.dastageer.rento.presentation.shared.animations

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Animation token constants and helpers matching the design spec.
 */
object RentoAnimations {

    // ─── Duration tokens ────────────────────────────────────────────────

    /** Default interaction transition duration (button press, chip select, etc.) */
    const val INTERACTION_DURATION = 200

    /** Chip / tabs colour transition */
    const val CHIP_TRANSITION_DURATION = 220

    /** Tab pill slide transition */
    const val TAB_PILL_DURATION = 260

    /** Progress step bar width animate */
    const val PROGRESS_STEP_DURATION = 300

    /** Banner slider dot transition */
    const val BANNER_DOT_DURATION = 320

    /** Screen transition */
    const val SCREEN_TRANSITION_DURATION = 180

    /** Form step transition */
    const val FORM_STEP_DURATION = 200

    // ─── Infinite animation durations ───────────────────────────────────

    /** floatY: floating logo on welcome screen — 4s */
    const val FLOAT_Y_DURATION = 4000

    /** pulse: notification dot, map pin — 2s */
    const val PULSE_DURATION = 2000

    /** gGlow: FAB add button glow — 3s */
    const val GLOW_DURATION = 3000

    /** shimmer: loading skeleton sweep — 1.5s */
    const val SHIMMER_DURATION = 1500

    /** Banner auto-advance interval */
    const val BANNER_AUTO_ADVANCE = 3200L

    // ─── Composable animation helpers ───────────────────────────────────

    /**
     * Float-Y animation: vertical oscillation for welcome screen logo.
     * 0%,100%: 0f; 50%: -9dp (returned as -9f, caller converts to dp offset).
     */
    @Composable
    fun rememberFloatY(): State<Float> {
        val transition = rememberInfiniteTransition(label = "floatY")
        return transition.animateFloat(
            initialValue = 0f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = FLOAT_Y_DURATION
                    0f at 0 using EaseInOut
                    -9f at FLOAT_Y_DURATION / 2 using EaseInOut
                    0f at FLOAT_Y_DURATION using EaseInOut
                },
                repeatMode = RepeatMode.Restart,
            ),
            label = "floatYValue",
        )
    }

    /**
     * Pulse animation: alpha oscillation for notification dots, map pins.
     * 0%,100%: alpha 1.0; 50%: alpha 0.42
     */
    @Composable
    fun rememberPulse(): State<Float> {
        val transition = rememberInfiniteTransition(label = "pulse")
        return transition.animateFloat(
            initialValue = 1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = PULSE_DURATION
                    1f at 0 using LinearEasing
                    0.42f at PULSE_DURATION / 2 using LinearEasing
                    1f at PULSE_DURATION using LinearEasing
                },
                repeatMode = RepeatMode.Restart,
            ),
            label = "pulseAlpha",
        )
    }

    /**
     * Glow animation: shadow spread oscillation for FAB.
     * Returns a 0..1 fraction; caller maps to dp spread range.
     * 0%,100%: 0f; 50%: 1f
     */
    @Composable
    fun rememberGlow(): State<Float> {
        val transition = rememberInfiniteTransition(label = "glow")
        return transition.animateFloat(
            initialValue = 0f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = GLOW_DURATION
                    0f at 0 using EaseInOut
                    1f at GLOW_DURATION / 2 using EaseInOut
                    0f at GLOW_DURATION using EaseInOut
                },
                repeatMode = RepeatMode.Restart,
            ),
            label = "glowFraction",
        )
    }

    /**
     * Shimmer animation: horizontal offset for skeleton loading.
     * Returns a fraction cycling from -1f to 2f for gradient translation.
     */
    @Composable
    fun rememberShimmerOffset(): State<Float> {
        val transition = rememberInfiniteTransition(label = "shimmer")
        return transition.animateFloat(
            initialValue = -1f,
            targetValue = 2f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = SHIMMER_DURATION,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Restart,
            ),
            label = "shimmerOffset",
        )
    }

    // ─── Spring specs ───────────────────────────────────────────────────

    /** Bottom sheet slide-up spring */
    fun <T> sheetSpring() = spring<T>(
        dampingRatio = 0.8f,
        stiffness = Spring.StiffnessMedium,
    )

    /** Bounce animation for small icon celebrations */
    fun <T> bounceSpring() = spring<T>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow,
    )
}

/**
 * Bounce effect wrapper for celebrating positive actions (like saving a property).
 */
@Composable
fun BounceEffect(
    trigger: Boolean,
    content: @Composable (modifier: androidx.compose.ui.Modifier) -> Unit,
) {
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (trigger) 1.0f else 0.82f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "bounceScale",
    )
    val rotation by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (trigger) 0f else -15f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "bounceRotation",
    )
    content(
        androidx.compose.ui.Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            rotationZ = rotation
        }
    )
}

/**
 * Shimmer loader brush.
 */
@Composable
fun rememberShimmerBrush(
    colors: com.dastageer.rento.presentation.shared.theme.RentoColors
): androidx.compose.ui.graphics.Brush {
    val shimmerColors = listOf(colors.bg3, colors.bg2, colors.bg3)
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerTranslate",
    )
    return androidx.compose.ui.graphics.Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset(translateAnim - 500f, 0f),
        end = androidx.compose.ui.geometry.Offset(translateAnim, 0f),
    )
}

/**
 * Fade-in and slide-up modifier for staggered list items.
 */
@Composable
fun androidx.compose.ui.Modifier.fadeInSlideUpModifier(
    visible: Boolean,
    delayMillis: Int = 0,
): androidx.compose.ui.Modifier {
    val alpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 360,
            delayMillis = delayMillis,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        ),
        label = "fadeAlpha",
    )
    val offsetY by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (visible) 0.dp else 26.dp,
        animationSpec = tween(
            durationMillis = 420,
            delayMillis = delayMillis,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        ),
        label = "slideOffset",
    )
    return this
        .graphicsLayer { this.alpha = alpha }
        .offset(y = offsetY)
}

/**
 * Screen level navigation transitions for Navigation Compose.
 */
object NavTransitions {
    val pushEnter = androidx.compose.animation.fadeIn(tween(200)) + androidx.compose.animation.slideInHorizontally(
        animationSpec = tween(220, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        initialOffsetX = { it },
    )
    val pushExit = androidx.compose.animation.fadeOut(tween(200)) + androidx.compose.animation.slideOutHorizontally(
        animationSpec = tween(220, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        targetOffsetX = { -it / 3 },
    )
    val popEnter = androidx.compose.animation.fadeIn(tween(200)) + androidx.compose.animation.slideInHorizontally(
        animationSpec = tween(220, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        initialOffsetX = { -it / 3 },
    )
    val popExit = androidx.compose.animation.fadeOut(tween(200)) + androidx.compose.animation.slideOutHorizontally(
        animationSpec = tween(220, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        targetOffsetX = { it },
    )
    val formStepEnter = androidx.compose.animation.fadeIn(tween(200)) + androidx.compose.animation.slideInHorizontally(
        animationSpec = tween(220, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        initialOffsetX = { it },
    )
    val formStepBack = androidx.compose.animation.fadeIn(tween(200)) + androidx.compose.animation.slideInHorizontally(
        animationSpec = tween(220, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        initialOffsetX = { -it },
    )
    val overlayEnter = androidx.compose.animation.fadeIn(tween(200)) + androidx.compose.animation.slideInVertically(
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        initialOffsetY = { it / 3 },
    )
    val overlayExit = androidx.compose.animation.fadeOut(tween(180)) + androidx.compose.animation.slideOutVertically(
        animationSpec = tween(180),
        targetOffsetY = { it / 3 },
    )
    val dialogEnter = androidx.compose.animation.fadeIn(tween(200)) + androidx.compose.animation.scaleIn(
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        initialScale = 0.92f,
    )
    val dialogExit = androidx.compose.animation.fadeOut(tween(160)) + androidx.compose.animation.scaleOut(
        animationSpec = tween(160),
        targetScale = 0.92f,
    )
}
