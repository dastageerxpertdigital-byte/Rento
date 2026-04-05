package com.dastageer.rento.domain.usecase.user

import com.dastageer.rento.domain.model.User
import com.dastageer.rento.domain.repository.UserRepository

class GetUserUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(uid: String): Result<User> =
        userRepository.getUser(uid)
}
