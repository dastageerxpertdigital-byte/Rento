package com.dastageer.rento.domain.usecase.user

import com.dastageer.rento.domain.model.OnboardingData
import com.dastageer.rento.domain.repository.UserRepository

class SaveOnboardingUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(uid: String, data: OnboardingData): Result<Unit> =
        userRepository.saveOnboarding(uid, data)
}
