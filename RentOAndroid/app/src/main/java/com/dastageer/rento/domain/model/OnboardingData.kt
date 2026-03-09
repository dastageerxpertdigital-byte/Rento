package com.dastageer.rento.domain.model

data class OnboardingData(
    val defaultMode: UserMode,
    val name: String,
    val phone: String,
    val dateOfBirth: String?,
    val province: String,
    val city: String,
    val deviceLat: Double?,
    val deviceLng: Double?,
    val accountType: AccountType,
    val referralSources: List<String>,
)
