package com.dastageer.rento.data.repository

import com.dastageer.rento.domain.model.AuthState
import com.dastageer.rento.domain.model.RentoAuthException
import com.dastageer.rento.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    override suspend fun loginWithEmail(email: String, password: String): Result<Unit> =
        runCatching {
            firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
            saveFcmToken()
        }.mapFirebaseException()

    override suspend fun loginWithGoogle(idToken: String): Result<Unit> =
        runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val isNew = result.additionalUserInfo?.isNewUser ?: false
            if (isNew) {
                // Domain user creation delegated to CreateUserDocumentUseCase — see M02-T05
            }
            saveFcmToken()
        }.mapFirebaseException()

    override suspend fun register(name: String, email: String, password: String): Result<Unit> =
        runCatching {
            val result = firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
            result.user?.sendEmailVerification()?.await()
            // Firestore doc creation delegated to RegisterUseCase — not here
            Unit
        }.mapFirebaseException()

    override suspend fun resendVerification(): Result<Unit> =
        runCatching {
            val user = firebaseAuth.currentUser
                ?: throw RentoAuthException.Unknown("No authenticated user.")
            user.sendEmailVerification().await()
            Unit
        }.mapFirebaseException()

    override suspend fun isEmailVerified(): Result<Boolean> =
        runCatching {
            firebaseAuth.currentUser?.reload()?.await()
            firebaseAuth.currentUser?.isEmailVerified ?: false
        }.mapFirebaseException()

    override suspend fun sendPasswordReset(email: String): Result<Unit> =
        runCatching {
            firebaseAuth.sendPasswordResetEmail(email.trim()).await()
            Unit
        }.mapFirebaseException()

    override suspend fun signOut() { firebaseAuth.signOut() }

    override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    override fun isLoggedIn(): Boolean = firebaseAuth.currentUser != null

    override fun getAuthState(): Flow<AuthState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            trySend(
                if (user == null) {
                    AuthState.Unauthenticated
                } else {
                    AuthState.Authenticated(user.uid, user.isEmailVerified)
                }
            )
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    private suspend fun saveFcmToken() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        try {
            val token = FirebaseMessaging.getInstance().token.await()
            firestore.collection("users").document(uid)
                .update("fcmToken", token).await()
        } catch (_: Exception) {
            // Non-fatal: FCM token save failure does not break login
            // Token will be refreshed by RentoFCMService.onNewToken()
        }
    }
}

private fun <T> Result<T>.mapFirebaseException(): Result<T> = this.recoverCatching { e ->
    when {
        e is FirebaseAuthInvalidCredentialsException &&
            e.errorCode == "ERROR_INVALID_EMAIL" -> throw RentoAuthException.InvalidEmail()
        e is FirebaseAuthInvalidCredentialsException -> throw RentoAuthException.WrongPassword()
        e is FirebaseAuthInvalidUserException -> throw RentoAuthException.UserNotFound()
        e is FirebaseAuthUserCollisionException -> throw RentoAuthException.EmailAlreadyInUse()
        e is FirebaseAuthWeakPasswordException -> throw RentoAuthException.WeakPassword()
        e.message?.contains("TOO_MANY_REQUESTS") == true -> throw RentoAuthException.TooManyRequests()
        e is java.io.IOException || e.message?.contains("network") == true ->
            throw RentoAuthException.NetworkError()
        else -> throw RentoAuthException.Unknown(e.message ?: "Unknown error.")
    }
}
