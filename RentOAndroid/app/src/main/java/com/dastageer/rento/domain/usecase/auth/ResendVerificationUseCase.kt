package com.dastageer.rento.domain.usecase.auth

import com.dastageer.rento.domain.repository.AuthRepository

class ResendVerificationUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Result<Unit> =
        authRepository.resendVerification()
}
