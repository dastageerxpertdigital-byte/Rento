package com.dastageer.rento.data.model

import com.google.firebase.Timestamp

data class UserDto(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String? = null,
    val photoUrl: String? = null,
    val emailVerified: Boolean = false,
    val isBlocked: Boolean = false,
    val blockReason: String? = null,
    val userType: String = "user", // "user" | "admin"
    val accountType: String = "individual", // "individual" | "business"
    val defaultMode: String = "looking", // "looking" | "hosting"
    val onboardingComplete: Boolean = false,
    val province: String = "",
    val city: String = "",
    val deviceLat: Double? = null,
    val deviceLng: Double? = null,
    val referralSource: List<String> = emptyList(),
    val fcmToken: String? = null,
    val currentPackageId: String? = null,
    val packageName: String? = null,
    val packageExpiryDate: Timestamp? = null,
    val maxPublishedListings: Int = 2,
    val maxTotalListings: Int = 4,
    val dailyMessageLimit: Int = 20,
    val allowsSlider: Boolean = false,
    val maxPublishedRequests: Int = 2,
    val maxTotalRequests: Int = 4,
    val createdAt: Timestamp = Timestamp.now(),
)
