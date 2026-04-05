package com.dastageer.rento.domain.usecase.auth

import com.dastageer.rento.domain.repository.AuthRepository

class SendPasswordResetUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String): Result<Unit> =
        authRepository.sendPasswordReset(email)
}
