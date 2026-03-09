package com.dastageer.rento.data.repository

import com.dastageer.rento.domain.model.OnboardingData
import com.dastageer.rento.domain.model.User
import com.dastageer.rento.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun createUserDocument(user: User): Result<Unit> = runCatching {
        val dto = user.toDto()
        firestore.collection("users").document(user.uid).set(dto).await()
    }

    override suspend fun getUser(uid: String): Result<User> = runCatching {
        val snapshot = firestore.collection("users").document(uid).get().await()
        snapshot.toUser() ?: throw Exception("User document not found for uid=$uid")
    }

    override suspend fun updateFcmToken(uid: String, token: String): Result<Unit> = runCatching {
        firestore.collection("users").document(uid).update("fcmToken", token).await()
    }

    override suspend fun updateEmailVerified(uid: String): Result<Unit> = runCatching {
        firestore.collection("users").document(uid).update("emailVerified", true).await()
    }

    override suspend fun saveOnboarding(uid: String, onboarding: OnboardingData): Result<Unit> =
        runCatching {
            val updates = mapOf(
                "onboardingComplete" to true,
                "defaultMode"        to onboarding.defaultMode.name.lowercase(),
                "name"               to onboarding.name,
                "phone"              to onboarding.phone,
                "dateOfBirth"        to (onboarding.dateOfBirth ?: ""),
                "province"           to onboarding.province,
                "city"               to onboarding.city,
                "deviceLat"          to onboarding.deviceLat,
                "deviceLng"          to onboarding.deviceLng,
                "accountType"        to onboarding.accountType.name.lowercase(),
                "referralSource"     to onboarding.referralSources,
            )
            firestore.collection("users").document(uid).update(updates).await()
        }

    override suspend fun getAdminEmail(): Result<String> = runCatching {
        val doc = firestore.collection("config").document("app").get().await()
        doc.getString("adminEmail") ?: "admin@rentopk.com"
    }
}
