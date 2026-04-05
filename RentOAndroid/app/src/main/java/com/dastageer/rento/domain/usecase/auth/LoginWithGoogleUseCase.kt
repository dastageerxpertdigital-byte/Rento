package com.dastageer.rento.domain.usecase.auth

import com.dastageer.rento.domain.model.AccountType
import com.dastageer.rento.domain.model.User
import com.dastageer.rento.domain.model.UserMode
import com.dastageer.rento.domain.repository.AuthRepository
import com.dastageer.rento.domain.repository.UserRepository

class LoginWithGoogleUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        idToken: String,
        displayName: String?,
        email: String?,
        photoUrl: String?,
    ): Result<AuthDestination> =
        authRepository.loginWithGoogle(idToken).mapCatching {
            val uid = authRepository.getCurrentUserId()!!
            val userResult = userRepository.getUser(uid)
            val user = if (userResult.isFailure) {
                val newUser = User(
                    uid = uid,
                    name = displayName ?: "",
                    email = email ?: "",
                    phone = null,
                    photoUrl = photoUrl,
                    emailVerified = true,
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
                newUser
            } else {
                userResult.getOrThrow()
            }
            resolveDestination(user, emailVerified = true)
        }
}
