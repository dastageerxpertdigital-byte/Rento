package com.dastageer.rento.domain.usecase.auth

import com.dastageer.rento.domain.model.User

enum class AuthDestination {
    HOME,
    ONBOARDING,
    VERIFY_EMAIL,
    BLOCKED,
}

internal fun resolveDestination(user: User, emailVerified: Boolean): AuthDestination = when {
    user.isBlocked -> AuthDestination.BLOCKED
    !emailVerified -> AuthDestination.VERIFY_EMAIL
    !user.onboardingComplete -> AuthDestination.ONBOARDING
    else -> AuthDestination.HOME
}
