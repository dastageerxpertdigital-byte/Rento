package com.dastageer.rento.domain.usecase.auth

import com.dastageer.rento.domain.repository.AuthRepository

class SignOutUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke() {
        authRepository.signOut()
    }
}
