package com.dastageer.rento.domain.usecase.auth

import com.dastageer.rento.domain.model.AccountType
import com.dastageer.rento.domain.model.RentoAuthException
import com.dastageer.rento.domain.model.User
import com.dastageer.rento.domain.model.UserMode
import com.dastageer.rento.domain.repository.AuthRepository
import com.dastageer.rento.domain.repository.UserRepository

class RegisterUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
    ): Result<Unit> {
        val error = validateRegistrationInput(name, email, password, confirmPassword)
        if (error != null) return Result.failure(error)

        return authRepository.register(name, email, password).mapCatching {
            val uid = authRepository.getCurrentUserId()!!
            val newUser = User(
                uid = uid,
                name = name.trim(),
                email = email.trim(),
                phone = null,
                photoUrl = null,
                emailVerified = false,
                isBlocked = false,
                blockReason = null,
                accountType = AccountType.INDIVIDUAL,
                defaultMode = UserMode.LOOKING,
                onboardingComplete = false,
                province = "",
                city = "",
                deviceLat = null,
                deviceLng = null,
                referralSource = emptyList(),
                currentPackageId = null,
                packageName = null,
                createdAt = System.currentTimeMillis(),
            )
            userRepository.createUserDocument(newUser).getOrThrow()
        }
    }

    private fun validateRegistrationInput(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
    ): RentoAuthException? = when {
        name.isBlank() -> RentoAuthException.Unknown("Full name is required.")
        name.trim().length < 2 -> RentoAuthException.Unknown("Name must be at least 2 characters.")
        email.isBlank() -> RentoAuthException.InvalidEmail("Email is required.")
        !isValidEmail(email) -> RentoAuthException.InvalidEmail()
        password.length < 8 -> RentoAuthException.WeakPassword()
        password != confirmPassword -> RentoAuthException.Unknown("Passwords do not match.")
        else -> null
    }

    private fun isValidEmail(email: String): Boolean =
        Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(email.trim())
}
