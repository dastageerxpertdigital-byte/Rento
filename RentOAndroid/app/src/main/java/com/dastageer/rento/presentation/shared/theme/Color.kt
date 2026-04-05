package com.dastageer.rento.presentation.shared.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ─── Dark Theme Raw Values ──────────────────────────────────────────────────

// Backgrounds (darkest → lightest)
val DarkBg0 = Color(0xFF04100C) // App background / screen base
val DarkBg1 = Color(0xFF08150F) // Card surface primary
val DarkBg2 = Color(0xFF0D1E17) // Card surface secondary / input bg
val DarkBg3 = Color(0xFF142A20) // Subtle fill / chip bg
val DarkBg4 = Color(0xFF1C3529) // Progress track / toggle track

// Primary (green)
val DarkPri = Color(0xFF2ECC8A) // Primary action, selected states, icons
val DarkPri2 = Color(0xFF54DBA2) // Lighter primary, gradient mid-stop
val DarkPri3 = Color(0xFFA8F0D8) // Lightest primary, gradient end-stop
val DarkSec = Color(0xFF27B99A) // Secondary green (gradient complement)

// Semantic
val DarkAcc = Color(0xFFF0C94A) // Accent / star / warning
val DarkRed = Color(0xFFE06060) // Error / rejected / destructive actions
val DarkBlue = Color(0xFF5A9FD4) // Info / badge

// Text hierarchy
val DarkT0 = Color(0xFFE4F4EC) // Primary text
val DarkT1 = Color(0xFF9DC8B4) // Secondary text
val DarkT2 = Color(0xFF5A8A76) // Tertiary text / section labels / icons
val DarkT3 = Color(0xFF2A4A3C) // Placeholder / disabled text

// Borders
val DarkBd = Color(0xFF142A20) // Standard border
val DarkBd2 = Color(0xFF1C3529) // Subtle border

// Overlays & navigation bar
val DarkNav = Color(0xF208150F) // Bottom nav background (~95% opacity over DarkBg1)
val DarkOv = Color(0xB804100C) // Modal / sheet overlay (~72% opacity)
val DarkNavFallback = Color(0xCC08150F) // API 30 nav fallback (80% — no blur available)

// Input
val DarkIpl = Color(0xFF243D30) // Input underline — idle state

// Semi-transparent tints
val DarkPriM = Color(0x1A2ECC8A) // Primary tint 10%
val DarkPriR = Color(0x4D2ECC8A) // Primary ring 30% — shadows/glows
val DarkAccM = Color(0x21F0C94A) // Accent tint 13%
val DarkRedM = Color(0x1CE06060) // Error tint 11%
val DarkBlueM = Color(0x1C5A9FD4) // Blue tint 11%

// Card image overlay — dark (transparent top → dark bottom, matches .iov CSS class)
val DarkImageOverlayStart = Color(0x0004100C) // transparent DarkBg0
val DarkImageOverlayMid = Color(0x2E04100C) // 18% opacity
val DarkImageOverlayEnd = Color(0xEB04100C) // 92% opacity

// Glass dialog
val GlassDialogBgDark = Color(0xE6081610) // 90% opaque deep dark green-tinted
val GlassScrimDark = Color(0x99000000) // 60% black backdrop
val GlassDialogBorderHighlight = Color(0x2AFFFFFF) // 16% white top-left highlight
val GlassDialogBorderMid = Color(0x122ECC8A) // 7% green mid
val GlassDialogBorderFade = Color(0x08FFFFFF) // 3% white fade

// ─── Light Theme Raw Values ─────────────────────────────────────────────────

val LightBg0 = Color(0xFFF0FAF6)
val LightBg1 = Color(0xFFFFFFFF)
val LightBg2 = Color(0xFFFFFFFF)
val LightBg3 = Color(0xFFE8F5EE)
val LightBg4 = Color(0xFFD6EDE5)

val LightPri = Color(0xFF0C7A50)
val LightPri2 = Color(0xFF14A06A)
val LightPri3 = Color(0xFF1EC888)
val LightSec = Color(0xFF0D9E86)

val LightAcc = Color(0xFFB87010)
val LightRed = Color(0xFFC04040)
val LightBlue = Color(0xFF2E6EA0)

val LightT0 = Color(0xFF06201A)
val LightT1 = Color(0xFF265044)
val LightT2 = Color(0xFF52806E)
val LightT3 = Color(0xFFA0C4B8)

val LightBd = Color(0xFFC4E2D8)
val LightBd2 = Color(0xFFB0D4C8)

val LightNav = Color(0xF5FFFFFF) // ~96% white
val LightNavFallback = Color(0xF0FFFFFF) // API 30 fallback (94% — no blur)
val LightOv = Color(0x8006201A) // ~50% dark-green overlay
val LightIpl = Color(0xFFB8D8CC)

val LightPriM = Color(0x140C7A50) // Primary tint 8%
val LightPriR = Color(0x3D0C7A50) // Primary ring 24%
val LightAccM = Color(0x17B87010)
val LightRedM = Color(0x12C04040)
val LightBlueM = Color(0x122E6EA0)

// Card image overlay — light
val LightImageOverlayStart = Color(0x0006201A)
val LightImageOverlayMid = Color(0x2606201A)
val LightImageOverlayEnd = Color(0xE106201A) // 88% dark-green — prototype value

// Glass dialog
val GlassDialogBgLight = Color(0xF0FFFFFF) // 94% white
val GlassScrimLight = Color(0x8C06201A) // 55% dark-green scrim
val GlassDialogBorderHighlightLight = Color(0x400C7A50)
val GlassDialogBorderMidLight = Color(0x18000000)
val GlassDialogBorderFadeLight = Color(0x0A0C7A50)

// ─── RentoColors Data Class ─────────────────────────────────────────────────

data class RentoColors(
    val bg0: Color,
    val bg1: Color,
    val bg2: Color,
    val bg3: Color,
    val bg4: Color,
    val primary: Color,
    val primary2: Color,
    val primary3: Color,
    val secondary: Color,
    val accent: Color,
    val red: Color,
    val blue: Color,
    val t0: Color,
    val t1: Color,
    val t2: Color,
    val t3: Color,
    val border: Color,
    val border2: Color,
    val navBg: Color,
    val navBgFallback: Color,
    val overlay: Color,
    val inputUnderlineIdle: Color,
    val primaryTint: Color,
    val primaryRing: Color,
    val accentTint: Color,
    val redTint: Color,
    val blueTint: Color,
    val imageOverlayStart: Color,
    val imageOverlayMid: Color,
    val imageOverlayEnd: Color,
    val glassDialogBg: Color,
    val glassScrim: Color,
    val glassDialogBorderHighlight: Color,
    val glassDialogBorderMid: Color,
    val glassDialogBorderFade: Color,
    val isDark: Boolean,
)

val RentoDarkColors = RentoColors(
    bg0 = DarkBg0, bg1 = DarkBg1, bg2 = DarkBg2, bg3 = DarkBg3, bg4 = DarkBg4,
    primary = DarkPri, primary2 = DarkPri2, primary3 = DarkPri3, secondary = DarkSec,
    accent = DarkAcc, red = DarkRed, blue = DarkBlue,
    t0 = DarkT0, t1 = DarkT1, t2 = DarkT2, t3 = DarkT3,
    border = DarkBd, border2 = DarkBd2,
    navBg = DarkNav, navBgFallback = DarkNavFallback,
    overlay = DarkOv,
    inputUnderlineIdle = DarkIpl,
    primaryTint = DarkPriM, primaryRing = DarkPriR,
    accentTint = DarkAccM, redTint = DarkRedM, blueTint = DarkBlueM,
    imageOverlayStart = DarkImageOverlayStart,
    imageOverlayMid = DarkImageOverlayMid,
    imageOverlayEnd = DarkImageOverlayEnd,
    glassDialogBg = GlassDialogBgDark, glassScrim = GlassScrimDark,
    glassDialogBorderHighlight = GlassDialogBorderHighlight,
    glassDialogBorderMid = GlassDialogBorderMid,
    glassDialogBorderFade = GlassDialogBorderFade,
    isDark = true,
)

val RentoLightColors = RentoColors(
    bg0 = LightBg0, bg1 = LightBg1, bg2 = LightBg2, bg3 = LightBg3, bg4 = LightBg4,
    primary = LightPri, primary2 = LightPri2, primary3 = LightPri3, secondary = LightSec,
    accent = LightAcc, red = LightRed, blue = LightBlue,
    t0 = LightT0, t1 = LightT1, t2 = LightT2, t3 = LightT3,
    border = LightBd, border2 = LightBd2,
    navBg = LightNav, navBgFallback = LightNavFallback,
    overlay = LightOv,
    inputUnderlineIdle = LightIpl,
    primaryTint = LightPriM, primaryRing = LightPriR,
    accentTint = LightAccM, redTint = LightRedM, blueTint = LightBlueM,
    imageOverlayStart = LightImageOverlayStart,
    imageOverlayMid = LightImageOverlayMid,
    imageOverlayEnd = LightImageOverlayEnd,
    glassDialogBg = GlassDialogBgLight, glassScrim = GlassScrimLight,
    glassDialogBorderHighlight = GlassDialogBorderHighlightLight,
    glassDialogBorderMid = GlassDialogBorderMidLight,
    glassDialogBorderFade = GlassDialogBorderFadeLight,
    isDark = false,
)

val LocalRentoColors = staticCompositionLocalOf<RentoColors> { RentoDarkColors }
