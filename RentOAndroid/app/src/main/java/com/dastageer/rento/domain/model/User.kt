package com.dastageer.rento.domain.model

data class User(
    val uid: String,
    val name: String,
    val email: String,
    val phone: String?,
    val photoUrl: String?,
    val emailVerified: Boolean,
    val isBlocked: Boolean,
    val blockReason: String?,
    val accountType: AccountType,
    val defaultMode: UserMode,
    val onboardingComplete: Boolean,
    val province: String,
    val city: String,
    val deviceLat: Double?,
    val deviceLng: Double?,
    val referralSource: List<String>,
    val currentPackageId: String?,
    val packageName: String?,
    val createdAt: Long,
)
