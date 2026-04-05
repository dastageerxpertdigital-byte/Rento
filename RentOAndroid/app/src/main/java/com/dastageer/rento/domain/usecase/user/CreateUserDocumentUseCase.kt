package com.dastageer.rento.domain.usecase.user

import com.dastageer.rento.domain.model.User
import com.dastageer.rento.domain.repository.UserRepository

class CreateUserDocumentUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(user: User): Result<Unit> =
        userRepository.createUserDocument(user)
}
