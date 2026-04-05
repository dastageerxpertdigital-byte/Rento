package com.dastageer.rento.domain.usecase.auth

import com.dastageer.rento.domain.model.RentoAuthException
import com.dastageer.rento.domain.repository.AuthRepository
import com.dastageer.rento.domain.repository.UserRepository

class LoginWithEmailUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthDestination> {
        val validationError = validateLoginInput(email, password)
        if (validationError != null) return Result.failure(validationError)

        return authRepository.loginWithEmail(email, password).mapCatching {
            val uid = authRepository.getCurrentUserId()!!
            val user = userRepository.getUser(uid).getOrThrow()
            resolveDestination(user, authRepository.isEmailVerified().getOrDefault(false))
        }
    }

    private fun validateLoginInput(email: String, password: String): RentoAuthException? = when {
        email.isBlank() -> RentoAuthException.InvalidEmail("Email is required.")
        !emailRegex.matches(email.trim()) -> RentoAuthException.InvalidEmail()
        password.isBlank() -> RentoAuthException.WrongPassword("Password is required.")
        else -> null
    }

    companion object {
        private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
