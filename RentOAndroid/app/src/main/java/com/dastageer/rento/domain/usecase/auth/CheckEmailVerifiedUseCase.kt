package com.dastageer.rento.domain.usecase.auth

import com.dastageer.rento.domain.repository.AuthRepository

class CheckEmailVerifiedUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Result<Boolean> =
        authRepository.isEmailVerified()
}
