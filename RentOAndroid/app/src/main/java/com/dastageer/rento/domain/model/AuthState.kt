package com.dastageer.rento.domain.model

sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(
        val uid: String,
        val emailVerified: Boolean,
    ) : AuthState()
}
