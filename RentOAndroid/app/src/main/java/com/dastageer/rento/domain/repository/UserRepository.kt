package com.dastageer.rento.domain.repository

import com.dastageer.rento.domain.model.OnboardingData
import com.dastageer.rento.domain.model.User

interface UserRepository {
    suspend fun createUserDocument(user: User): Result<Unit>
    suspend fun getUser(uid: String): Result<User>
    suspend fun updateFcmToken(uid: String, token: String): Result<Unit>
    suspend fun updateEmailVerified(uid: String): Result<Unit>
    suspend fun saveOnboarding(uid: String, onboarding: OnboardingData): Result<Unit>
    suspend fun getAdminEmail(): Result<String>
}
