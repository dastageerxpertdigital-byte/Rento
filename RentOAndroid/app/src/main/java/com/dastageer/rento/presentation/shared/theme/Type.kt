package com.dastageer.rento.presentation.shared.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.dastageer.rento.R

// ─── Font Families ──────────────────────────────────────────────────────────

val FrauncesFamily = FontFamily(
    Font(R.font.fraunces_light, FontWeight.Light),
    Font(R.font.fraunces_regular, FontWeight.Normal),
    Font(R.font.fraunces_semibold, FontWeight.SemiBold),
    Font(R.font.fraunces_bold, FontWeight.Bold),
)

val PlusJakartaSansFamily = FontFamily(
    Font(R.font.plus_jakarta_sans_light, FontWeight.Light),
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold),
)

// ─── Display (Fraunces) ─────────────────────────────────────────────────────

val RentoDisplayXL = TextStyle(
    fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold,
    fontSize = 50.sp, lineHeight = 56.sp,
)
val RentoDisplayL = TextStyle(
    fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold,
    fontSize = 32.sp, lineHeight = 38.sp,
)
val RentoDisplayM = TextStyle(
    fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold,
    fontSize = 28.sp, lineHeight = 34.sp,
)
val RentoDisplayS = TextStyle(
    fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold,
    fontSize = 22.sp, lineHeight = 28.sp,
)
val RentoDisplaySLarge = TextStyle(
    fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold,
    fontSize = 26.sp, lineHeight = 32.sp,
)

// ─── Body / UI (Plus Jakarta Sans) ──────────────────────────────────────────

val RentoBodyL = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.Bold,
    fontSize = 15.sp, lineHeight = 22.sp,
)
val RentoBodyLSemiBold = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp, lineHeight = 24.sp,
)
val RentoBodyM = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.Normal,
    fontSize = 14.sp, lineHeight = 22.sp,
)
val RentoBodyMMedium = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.Medium,
    fontSize = 14.sp, lineHeight = 22.sp,
)
val RentoBodyS = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.Normal,
    fontSize = 13.sp, lineHeight = 20.sp,
)
val RentoBodySMedium = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.Medium,
    fontSize = 13.sp, lineHeight = 20.sp,
)
val RentoLabel = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.Bold,
    fontSize = 11.sp, letterSpacing = 0.06.em,
)
val RentoLabelLarge = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.Bold,
    fontSize = 12.sp, letterSpacing = 0.06.em,
)
val RentoMicro = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.Normal,
    fontSize = 10.sp, lineHeight = 14.sp,
)
val RentoMicroMedium = TextStyle(
    fontFamily = PlusJakartaSansFamily, fontWeight = FontWeight.Medium,
    fontSize = 11.sp, lineHeight = 15.sp,
)

// ─── Typography Data Class ──────────────────────────────────────────────────

data class RentoTypography(
    val displayXL: TextStyle      = RentoDisplayXL,
    val displayL: TextStyle       = RentoDisplayL,
    val displayM: TextStyle       = RentoDisplayM,
    val displayS: TextStyle       = RentoDisplayS,
    val displaySLarge: TextStyle  = RentoDisplaySLarge,
    val bodyL: TextStyle          = RentoBodyL,
    val bodyLSemiBold: TextStyle  = RentoBodyLSemiBold,
    val bodyM: TextStyle          = RentoBodyM,
    val bodyMMedium: TextStyle    = RentoBodyMMedium,
    val bodyS: TextStyle          = RentoBodyS,
    val bodySMedium: TextStyle    = RentoBodySMedium,
    val label: TextStyle          = RentoLabel,
    val labelLarge: TextStyle     = RentoLabelLarge,
    val micro: TextStyle          = RentoMicro,
    val microMedium: TextStyle    = RentoMicroMedium,
)

val LocalRentoTypography = staticCompositionLocalOf<RentoTypography> { RentoTypography() }

// ─── Material3 Typography Bridge ────────────────────────────────────────────

fun RentoTypography.toMaterial3Typography() = Typography(
    displayLarge   = displayXL,
    displayMedium  = displayL,
    displaySmall   = displayM,
    headlineLarge  = displaySLarge,
    headlineMedium = displayS,
    bodyLarge      = bodyL,
    bodyMedium     = bodyM,
    bodySmall      = bodyS,
    labelLarge     = labelLarge,
    labelMedium    = label,
    labelSmall     = micro,
)
