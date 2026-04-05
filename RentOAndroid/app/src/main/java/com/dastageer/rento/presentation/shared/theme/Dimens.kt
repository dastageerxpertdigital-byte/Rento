package com.dastageer.rento.presentation.shared.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class RentoDimens(
    // Screen layout
    val screenPadH: Dp = 20.dp,
    val screenPadTop: Dp = 16.dp,
    val sectionPad: Dp = 24.dp,
    val sectionGap: Dp = 24.dp,

    // Card
    val cardRadius: Dp = 24.dp,
    val cardRadiusSmall: Dp = 18.dp,
    val cardRadiusMedium: Dp = 20.dp,
    val cardGap: Dp = 16.dp,
    val gridGap: Dp = 12.dp,
    val cardImageHeightFull: Dp = 190.dp,
    val cardImageHeightCompact: Dp = 108.dp,

    // Buttons
    val btnRadius: Dp = 100.dp,
    val btnPadV: Dp = 16.dp,
    val btnPadH: Dp = 24.dp,

    // Chips
    val chipRadius: Dp = 100.dp,
    val chipPadV: Dp = 7.dp,
    val chipPadH: Dp = 14.dp,
    val chipGap: Dp = 8.dp,
    val chipGapLarge: Dp = 10.dp,

    // Input fields
    val inputWrapperRadius: Dp = 16.dp,
    val inputPadV: Dp = 12.dp,
    val inputPadH: Dp = 16.dp,
    val inputBorderWidth: Dp = 1.5.dp,
    val inputUnderlineWidth: Dp = 2.dp,

    // Badges
    val badgeRadius: Dp = 7.dp,
    val badgePadV: Dp = 3.dp,
    val badgePadH: Dp = 9.dp,

    // Navigation bar
    val navHeight: Dp = 86.dp,
    val navPaddingBottom: Dp = 18.dp,
    val navPaddingH: Dp = 8.dp,
    val navItemRadius: Dp = 18.dp,
    val navItemPadV: Dp = 8.dp,
    val navItemPadH: Dp = 14.dp,
    val navIconSize: Dp = 22.dp,
    val navLabelGap: Dp = 4.dp,

    // FAB (centre add button)
    val fabSize: Dp = 54.dp,

    // Detail screen
    val detailImageHeight: Dp = 315.dp,
    val sliderHeight: Dp = 162.dp,

    // Icon button (back, share, save etc.)
    val iconBtnSize: Dp = 42.dp,
    val iconBtnRadius: Dp = 15.dp,

    // Avatar
    val avatarSizeS: Dp = 44.dp,
    val avatarSizeM: Dp = 52.dp,
    val avatarSizeL: Dp = 74.dp,
    val avatarSizeXL: Dp = 84.dp,

    // Amenity / stat tile
    val amenityTileRadius: Dp = 16.dp,
    val amenityTilePadV: Dp = 14.dp,
    val amenityTilePadH: Dp = 8.dp,

    // Toggle switch
    val toggleWidth: Dp = 46.dp,
    val toggleHeight: Dp = 26.dp,
    val toggleThumbSize: Dp = 20.dp,
    val toggleThumbOffset: Dp = 3.dp,

    // Bottom sheet drag handle
    val dragHandleWidth: Dp = 40.dp,
    val dragHandleHeight: Dp = 5.dp,
    val dragHandleTopPad: Dp = 12.dp,
    val dragHandleBottomPad: Dp = 24.dp,

    // Progress bar
    val progressBarHeight: Dp = 4.dp,
    val progressStepDotActiveWidth: Dp = 22.dp,
    val progressStepDotHeight: Dp = 5.dp,
    val progressStepDotInactiveWidth: Dp = 5.dp,
    val progressStepDotGap: Dp = 5.dp,
    val progressStepLabelGap: Dp = 7.dp,

    // Glass dialog
    val glassDialogRadius: Dp = 28.dp,
    val glassDialogWidthFraction: Float = 0.88f,
    val glassDialogPadH: Dp = 24.dp,
    val glassDialogPadV: Dp = 28.dp,
    val glassDialogIconCircleSize: Dp = 56.dp,
    val glassDialogIconSize: Dp = 26.dp,
    val glassDialogIconBottomGap: Dp = 16.dp,

    // Map background grid
    val mapGridSpacing: Dp = 28.dp,

    // Border widths
    val borderStandard: Dp = 1.5.dp,
    val borderThin: Dp = 1.dp,

    // Add overlay sheet
    val addOverlaySheetCornerTop: Dp = 28.dp,
    val addOverlaySheetPadH: Dp = 20.dp,
    val addOverlaySheetPadBottom: Dp = 48.dp,
    val addOverlayCardIconCircleSize: Dp = 56.dp,

    // Banner slider
    val bannerSliderMarginH: Dp = 20.dp,
    val bannerSliderRadius: Dp = 22.dp,
    val bannerSliderHeight: Dp = 162.dp,
    val bannerDotActiveWidth: Dp = 20.dp,
    val bannerDotInactiveWidth: Dp = 6.dp,
    val bannerDotHeight: Dp = 6.dp,

    // Notification dot (unread indicator on bell)
    val notifDotSize: Dp = 9.dp,
    val notifDotBorder: Dp = 2.5.dp,

    // In-app notification banner
    val inAppBannerRadius: Dp = 16.dp,
    val inAppBannerPadH: Dp = 16.dp,
    val inAppBannerPadV: Dp = 14.dp,

    // Empty state
    val emptyStateIconSize: Dp = 80.dp,
    val emptyStateIconRadius: Dp = 28.dp,

    // Error banner
    val errorBannerRadius: Dp = 14.dp,
    val errorBannerPadH: Dp = 16.dp,
    val errorBannerPadV: Dp = 12.dp,
)

val LocalRentoDimens = staticCompositionLocalOf<RentoDimens> { RentoDimens() }
