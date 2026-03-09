# RentO — Android App
## Module 02 — Authentication & Onboarding
### Complete Engineering Specification

> **Version:** 1.0.0
> **Status:** Active — Single Source of Truth for Module 02
> **Branch:** `feature/module-02-auth`
> **Depends on:** Module 01 complete ✅ (`feature/module-01-design-system` merged to `main`)
> **Audience:** Android Agent
>
> ⚠️ **AGENT LAW:** Every screen in this module is derived verbatim from the prototype (`design_references.jsx`) and `REQUIREMENTS_SPECIFICATION_v2_3.md`. Do not improvise any layout, colour, padding, animation, or interaction. If anything is ambiguous — stop and ask. Zero guessing.

---

## Table of Contents

1. [Module Overview](#1-module-overview)
2. [File Structure](#2-file-structure)
3. [Task List](#3-task-list)
4. [Architecture — Layers & Contracts](#4-architecture--layers--contracts)
   - [4.1 Domain Layer — Models](#41-domain-layer--models)
   - [4.2 Domain Layer — Repository Interfaces](#42-domain-layer--repository-interfaces)
   - [4.3 Data Layer — Firestore User Document](#43-data-layer--firestore-user-document)
   - [4.4 Data Layer — `AuthRepositoryImpl`](#44-data-layer--authrepositoryimpl)
   - [4.5 Data Layer — `UserRepositoryImpl`](#45-data-layer--userrepositoryimpl)
   - [4.6 Presentation Layer — ViewModels](#46-presentation-layer--viewmodels)
   - [4.7 Koin Module](#47-koin-module)
   - [4.8 Navigation Graph](#48-navigation-graph)
5. [Task M02-T01 — Domain Models](#task-m02-t01--domain-models)
6. [Task M02-T02 — Repository Interfaces](#task-m02-t02--repository-interfaces)
7. [Task M02-T03 — AuthRepositoryImpl](#task-m02-t03--authrepositoryimpl)
8. [Task M02-T04 — UserRepositoryImpl](#task-m02-t04--userrepositoryimpl)
9. [Task M02-T05 — AuthViewModel](#task-m02-t05--authviewmodel)
10. [Task M02-T06 — ForgotPasswordViewModel](#task-m02-t06--forgotpasswordviewmodel)
11. [Task M02-T07 — OnboardingViewModel](#task-m02-t07--onboardingviewmodel)
12. [Task M02-T08 — Koin DI Module](#task-m02-t08--koin-di-module)
13. [Task M02-T09 — Navigation Graph](#task-m02-t09--navigation-graph)
14. [Task M02-T10 — Screen: SplashScreen](#task-m02-t10--screen-splashscreen)
15. [Task M02-T11 — Screen: WelcomeScreen](#task-m02-t11--screen-welcomescreen)
16. [Task M02-T12 — Screen: LoginScreen](#task-m02-t12--screen-loginscreen)
17. [Task M02-T13 — Screen: RegisterScreen](#task-m02-t13--screen-registerscreen)
18. [Task M02-T14 — Screen: ForgotPasswordScreen](#task-m02-t14--screen-forgotpasswordscreen)
19. [Task M02-T15 — Screen: EmailVerificationScreen](#task-m02-t15--screen-emailverificationscreen)
20. [Task M02-T16 — Screen: BlockedScreen](#task-m02-t16--screen-blockedscreen)
21. [Task M02-T17 — Screen: OnboardingScreen (Step 1 — Intent)](#task-m02-t17--screen-onboardingscreen-step-1--intent)
22. [Task M02-T18 — Screen: OnboardingScreen (Step 2 — Personal Info)](#task-m02-t18--screen-onboardingscreen-step-2--personal-info)
23. [Task M02-T19 — Screen: OnboardingScreen (Step 3 — Account Type)](#task-m02-t19--screen-onboardingscreen-step-3--account-type)
24. [Task M02-T20 — Screen: OnboardingScreen (Step 4 — Referral)](#task-m02-t20--screen-onboardingscreen-step-4--referral)
25. [Task M02-T21 — Shared UI: GoogleSignInButton + Divider](#task-m02-t21--shared-ui-googlesigninbutton--divider)
26. [Task M02-T22 — Shared UI: AuthHeader + AuthBackButton](#task-m02-t22--shared-ui-authheader--authbackbutton)
27. [Task M02-T23 — FCM Token Registration](#task-m02-t23--fcm-token-registration)
28. [Task M02-T24 — String Resources](#task-m02-t24--string-resources)
29. [Task M02-T25 — Unit Tests](#task-m02-t25--unit-tests)
30. [Task M02-T26 — Build Gate](#task-m02-t26--build-gate)
31. [Journey Coverage Checklist](#31-journey-coverage-checklist)
32. [CODE_REVIEW_MODULE_02.md Template](#32-code_review_module_02md-template)

---

## 1. Module Overview

Module 02 establishes the **complete authentication pipeline** for RentO:

- Cold start routing (connectivity → force update check → auth state)
- Email/password registration and login
- Google One Tap Sign-In
- Email verification flow
- Forgot password with 60-second resend throttle
- Blocked user gate
- **4-step onboarding** (intent → personal info → account type → referral source) — shown only once on first login/registration
- FCM token saved to Firestore on every successful sign-in

All screens use Module 01 design system components exclusively. No Material defaults are used for any UI element that has a RentO equivalent.

**Screens produced:** 10 screens (Splash, Welcome, Login, Register, ForgotPassword, EmailVerification, Blocked, Onboarding ×4 steps unified in one composable)

---

## 2. File Structure

```
app/src/main/java/com/rento/app/
├── domain/
│   ├── model/
│   │   ├── User.kt
│   │   └── AuthState.kt
│   ├── repository/
│   │   ├── AuthRepository.kt
│   │   └── UserRepository.kt
│   └── usecase/
│       ├── auth/
│       │   ├── LoginWithEmailUseCase.kt
│       │   ├── LoginWithGoogleUseCase.kt
│       │   ├── RegisterUseCase.kt
│       │   ├── SendPasswordResetUseCase.kt
│       │   ├── ResendVerificationUseCase.kt
│       │   ├── CheckEmailVerifiedUseCase.kt
│       │   └── SignOutUseCase.kt
│       └── user/
│           ├── CreateUserDocumentUseCase.kt
│           ├── GetUserUseCase.kt
│           └── SaveOnboardingUseCase.kt
├── data/
│   ├── model/
│   │   └── UserDto.kt
│   └── repository/
│       ├── AuthRepositoryImpl.kt
│       └── UserRepositoryImpl.kt
├── presentation/
│   ├── auth/
│   │   ├── AuthViewModel.kt
│   │   ├── ForgotPasswordViewModel.kt
│   │   ├── OnboardingViewModel.kt
│   │   ├── SplashScreen.kt
│   │   ├── WelcomeScreen.kt
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   ├── ForgotPasswordScreen.kt
│   │   ├── EmailVerificationScreen.kt
│   │   ├── BlockedScreen.kt
│   │   ├── OnboardingScreen.kt
│   │   └── components/
│   │       ├── GoogleSignInButton.kt
│   │       ├── AuthDivider.kt
│   │       ├── AuthHeader.kt
│   │       └── AuthBackButton.kt
│   └── navigation/
│       └── RentoNavGraph.kt           ← replaces stub from Module 01
└── di/
    └── AuthModule.kt

app/src/main/res/values/strings.xml    ← all Module 02 strings appended here

app/src/test/java/com/rento/app/
├── domain/usecase/auth/
│   ├── LoginWithEmailUseCaseTest.kt
│   ├── LoginWithGoogleUseCaseTest.kt
│   ├── RegisterUseCaseTest.kt
│   ├── SendPasswordResetUseCaseTest.kt
│   ├── ResendVerificationUseCaseTest.kt
│   └── CheckEmailVerifiedUseCaseTest.kt
├── domain/usecase/user/
│   ├── CreateUserDocumentUseCaseTest.kt
│   └── SaveOnboardingUseCaseTest.kt
└── presentation/auth/
    ├── AuthViewModelTest.kt
    ├── ForgotPasswordViewModelTest.kt
    └── OnboardingViewModelTest.kt
```

---

## 3. Task List

| ID | Task | File(s) | Status |
|----|------|---------|--------|
| M02-T01 | Domain models — `User`, `AuthState` | `domain/model/` | ☐ |
| M02-T02 | Repository interfaces — `AuthRepository`, `UserRepository` | `domain/repository/` | ☐ |
| M02-T03 | `AuthRepositoryImpl` (Firebase Auth + FCM token) | `data/repository/` | ☐ |
| M02-T04 | `UserRepositoryImpl` (Firestore user doc) | `data/repository/` | ☐ |
| M02-T05 | `AuthViewModel` + all use cases | `presentation/auth/`, `domain/usecase/` | ☐ |
| M02-T06 | `ForgotPasswordViewModel` + use case | `presentation/auth/`, `domain/usecase/` | ☐ |
| M02-T07 | `OnboardingViewModel` + use cases | `presentation/auth/`, `domain/usecase/` | ☐ |
| M02-T08 | Koin DI module — `AuthModule.kt` | `di/AuthModule.kt` | ☐ |
| M02-T09 | Navigation graph — `RentoNavGraph.kt` (auth routes) | `presentation/navigation/` | ☐ |
| M02-T10 | Screen: `SplashScreen` | `SplashScreen.kt` | ☐ |
| M02-T11 | Screen: `WelcomeScreen` | `WelcomeScreen.kt` | ☐ |
| M02-T12 | Screen: `LoginScreen` | `LoginScreen.kt` | ☐ |
| M02-T13 | Screen: `RegisterScreen` | `RegisterScreen.kt` | ☐ |
| M02-T14 | Screen: `ForgotPasswordScreen` | `ForgotPasswordScreen.kt` | ☐ |
| M02-T15 | Screen: `EmailVerificationScreen` | `EmailVerificationScreen.kt` | ☐ |
| M02-T16 | Screen: `BlockedScreen` | `BlockedScreen.kt` | ☐ |
| M02-T17 | Screen: `OnboardingScreen` Step 1 — Intent | `OnboardingScreen.kt` | ☐ |
| M02-T18 | Screen: `OnboardingScreen` Step 2 — Personal Info | `OnboardingScreen.kt` | ☐ |
| M02-T19 | Screen: `OnboardingScreen` Step 3 — Account Type | `OnboardingScreen.kt` | ☐ |
| M02-T20 | Screen: `OnboardingScreen` Step 4 — Referral | `OnboardingScreen.kt` | ☐ |
| M02-T21 | Shared UI: `GoogleSignInButton`, `AuthDivider` | `components/` | ☐ |
| M02-T22 | Shared UI: `AuthHeader`, `AuthBackButton` | `components/` | ☐ |
| M02-T23 | FCM token service — `RentoFCMService` (token-save only) | `RentoFCMService.kt` | ☐ |
| M02-T24 | String resources — all Module 02 strings | `strings.xml` | ☐ |
| M02-T25 | Unit tests — all ViewModels + use cases | `*Test.kt` | ☐ |
| M02-T26 | Build gate — lint + detekt + build + tests + coverage | — | ☐ |

---

## 4. Architecture — Layers & Contracts

### 4.1 Domain Layer — Models

Domain layer has **zero Android imports** (no `Context`, no `Activity`, no Firebase SDK types). Only Kotlin stdlib and `kotlinx.coroutines`.

```
User:
  uid: String
  name: String
  email: String
  phone: String?
  photoUrl: String?
  emailVerified: Boolean
  isBlocked: Boolean
  blockReason: String?
  accountType: AccountType          // enum: INDIVIDUAL | BUSINESS
  defaultMode: UserMode             // enum: LOOKING | HOSTING
  onboardingComplete: Boolean
  province: String
  city: String
  deviceLat: Double?
  deviceLng: Double?
  referralSource: List<String>
  currentPackageId: String?
  packageName: String?
  createdAt: Long                   // epoch millis
```

```
AuthState:
  Sealed class:
    Unauthenticated
    Authenticated(uid: String, emailVerified: Boolean)
```

### 4.2 Domain Layer — Repository Interfaces

```kotlin
interface AuthRepository {
    suspend fun loginWithEmail(email: String, password: String): Result<Unit>
    suspend fun loginWithGoogle(idToken: String): Result<Unit>
    suspend fun register(name: String, email: String, password: String): Result<Unit>
    suspend fun resendVerification(): Result<Unit>
    suspend fun isEmailVerified(): Result<Boolean>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun signOut()
    fun getCurrentUserId(): String?
    fun isLoggedIn(): Boolean
    fun getAuthState(): Flow<AuthState>
}

interface UserRepository {
    suspend fun createUserDocument(user: User): Result<Unit>
    suspend fun getUser(uid: String): Result<User>
    suspend fun updateFcmToken(uid: String, token: String): Result<Unit>
    suspend fun updateEmailVerified(uid: String): Result<Unit>
    suspend fun saveOnboarding(uid: String, onboarding: OnboardingData): Result<Unit>
    suspend fun getAdminEmail(): Result<String>   // from Firestore config/app.adminEmail
}
```

### 4.3 Data Layer — Firestore User Document

Exact schema written to `users/{uid}` on registration:

```kotlin
data class UserDto(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String? = null,
    val photoUrl: String? = null,
    val emailVerified: Boolean = false,
    val isBlocked: Boolean = false,
    val blockReason: String? = null,
    val userType: String = "user",             // "user" | "admin"
    val accountType: String = "individual",    // "individual" | "business"
    val defaultMode: String = "looking",       // "looking" | "hosting"
    val onboardingComplete: Boolean = false,
    val province: String = "",
    val city: String = "",
    val deviceLat: Double? = null,
    val deviceLng: Double? = null,
    val referralSource: List<String> = emptyList(),
    val fcmToken: String? = null,
    val currentPackageId: String? = null,
    val packageName: String? = null,
    val packageExpiryDate: com.google.firebase.Timestamp? = null,
    val maxPublishedListings: Int = 2,
    val maxTotalListings: Int = 4,
    val dailyMessageLimit: Int = 20,
    val allowsSlider: Boolean = false,
    val maxPublishedRequests: Int = 2,
    val maxTotalRequests: Int = 4,
    val createdAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now(),
)
```

### 4.4 Data Layer — `AuthRepositoryImpl`

Key implementation rules:
- All Firebase calls use `.await()` (Kotlin coroutines, never callbacks inside suspend functions).
- `loginWithEmail` → `FirebaseAuth.signInWithEmailAndPassword().await()` → on success: fetch FCM token → write to Firestore.
- `loginWithGoogle` → `FirebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()` → check if new user (via `result.additionalUserInfo?.isNewUser`) → if new: `createUserDocument` → if existing: check blocked state.
- `register` → `createUserWithEmailAndPassword().await()` → `sendEmailVerification().await()` → `createUserDocument`.
- Error mapping: Firebase error codes mapped to `RentoAuthException` sealed class (see M02-T03).
- `getAuthState()`: returns `callbackFlow { FirebaseAuth.addAuthStateListener { ... } }`.

### 4.5 Data Layer — `UserRepositoryImpl`

- `createUserDocument`: writes `UserDto` to `users/{uid}` with `set()`.
- `getUser`: reads from `users/{uid}`, maps `DocumentSnapshot` → `User` domain model.
- `updateFcmToken`: `update("fcmToken", token)`.
- `updateEmailVerified`: `update("emailVerified", true)`.
- `saveOnboarding`: `update(mapOf("onboardingComplete" to true, "defaultMode" to onboarding.defaultMode, "accountType" to onboarding.accountType, "province" to ..., "city" to ..., ...))`.
- `getAdminEmail`: reads `config/app` document → `adminEmail` field.

### 4.6 Presentation Layer — ViewModels

All ViewModels consume repository interfaces (never implementations). Injected via Koin.

**`AuthViewModel`** state:
```kotlin
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val destination: AuthDestination) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

enum class AuthDestination {
    HOME,           // authenticated + emailVerified + onboardingComplete
    ONBOARDING,     // authenticated + emailVerified + !onboardingComplete
    VERIFY_EMAIL,   // authenticated + !emailVerified
    BLOCKED,        // user.isBlocked = true
}
```

**`ForgotPasswordViewModel`** state:
```kotlin
sealed class ForgotPasswordUiState {
    object Idle : ForgotPasswordUiState()
    object Loading : ForgotPasswordUiState()
    data class Success(val sentToEmail: String) : ForgotPasswordUiState()
    data class Error(val message: String) : ForgotPasswordUiState()
    data class Throttled(val secondsRemaining: Int) : ForgotPasswordUiState()
}
```

**`OnboardingViewModel`** state:
```kotlin
data class OnboardingUiState(
    val currentStep: Int = 0,               // 0-indexed, 0..3
    val defaultMode: UserMode? = null,      // Step 1
    val name: String = "",                  // Step 2
    val phone: String = "",
    val dateOfBirth: String? = null,
    val province: String = "",
    val city: String = "",
    val accountType: AccountType? = null,   // Step 3
    val referralSources: Set<String> = emptySet(), // Step 4
    val isLoading: Boolean = false,
    val error: String? = null,
)
```

### 4.7 Koin Module

All bindings declared in `di/AuthModule.kt`. No DI elsewhere for this module.

```kotlin
val authModule = module {
    single<AuthRepository>    { AuthRepositoryImpl(get(), get()) }   // FirebaseAuth, Firestore
    single<UserRepository>    { UserRepositoryImpl(get()) }          // Firestore
    factory { LoginWithEmailUseCase(get()) }
    factory { LoginWithGoogleUseCase(get(), get()) }  // AuthRepo + UserRepo
    factory { RegisterUseCase(get(), get()) }
    factory { SendPasswordResetUseCase(get()) }
    factory { ResendVerificationUseCase(get()) }
    factory { CheckEmailVerifiedUseCase(get()) }
    factory { SignOutUseCase(get()) }
    factory { CreateUserDocumentUseCase(get()) }
    factory { GetUserUseCase(get()) }
    factory { SaveOnboardingUseCase(get()) }
    viewModel { AuthViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ForgotPasswordViewModel(get(), get()) }
    viewModel { OnboardingViewModel(get(), get()) }
}
```

Registered in `RentoApplication.startKoin { modules(authModule) }`.

### 4.8 Navigation Graph

Auth routes in the nav graph. Full graph also includes placeholders for Home (Module 03).

```
splash              → (connectivity check) → welcome | home | onboarding | verify_email | blocked
welcome             → auth/login | auth/register
auth/login          → home | auth/register | auth/forgot_password | auth/verify_email | auth/blocked | onboarding
auth/register       → onboarding | auth/login | auth/verify_email
auth/forgot_password → auth/login
auth/verify_email   → onboarding | home
auth/blocked        → (sign out only → welcome)
onboarding          → home (clearBackStack)
home                → (Module 03 placeholder)
```

`popUpTo("splash") { inclusive = true }` applied on all transitions to `home` and from `onboarding → home`.

---

## Task M02-T01 — Domain Models

**Files:**
- `domain/model/User.kt`
- `domain/model/AuthState.kt`
- `domain/model/OnboardingData.kt`
- `domain/model/enums.kt`

### Sub-tasks
- [ ] **M02-T01-A** Create `enums.kt`:

```kotlin
package com.rento.app.domain.model

enum class UserMode { LOOKING, HOSTING }

enum class AccountType { INDIVIDUAL, BUSINESS }
```

- [ ] **M02-T01-B** Create `User.kt`:

```kotlin
package com.rento.app.domain.model

data class User(
    val uid: String,
    val name: String,
    val email: String,
    val phone: String?,
    val photoUrl: String?,
    val emailVerified: Boolean,
    val isBlocked: Boolean,
    val blockReason: String?,
    val accountType: AccountType,
    val defaultMode: UserMode,
    val onboardingComplete: Boolean,
    val province: String,
    val city: String,
    val deviceLat: Double?,
    val deviceLng: Double?,
    val referralSource: List<String>,
    val currentPackageId: String?,
    val packageName: String?,
    val createdAt: Long,
)
```

- [ ] **M02-T01-C** Create `AuthState.kt`:

```kotlin
package com.rento.app.domain.model

sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(
        val uid: String,
        val emailVerified: Boolean,
    ) : AuthState()
}
```

- [ ] **M02-T01-D** Create `OnboardingData.kt`:

```kotlin
package com.rento.app.domain.model

data class OnboardingData(
    val defaultMode: UserMode,
    val name: String,
    val phone: String,
    val dateOfBirth: String?,
    val province: String,
    val city: String,
    val deviceLat: Double?,
    val deviceLng: Double?,
    val accountType: AccountType,
    val referralSources: List<String>,
)
```

- [ ] **M02-T01-E** Create `RentoAuthException.kt` — maps Firebase error codes to human-readable domain exceptions:

```kotlin
package com.rento.app.domain.model

sealed class RentoAuthException(message: String) : Exception(message) {
    class InvalidEmail(msg: String = "Invalid email address.") : RentoAuthException(msg)
    class WrongPassword(msg: String = "Incorrect password.") : RentoAuthException(msg)
    class UserNotFound(msg: String = "No account found with this email.") : RentoAuthException(msg)
    class EmailAlreadyInUse(msg: String = "An account already exists with this email.") : RentoAuthException(msg)
    class WeakPassword(msg: String = "Password must be at least 8 characters.") : RentoAuthException(msg)
    class TooManyRequests(msg: String = "Too many attempts. Please wait and try again.") : RentoAuthException(msg)
    class NetworkError(msg: String = "Network error. Please check your connection.") : RentoAuthException(msg)
    class GooglePlayServicesUnavailable(msg: String = "Google Play Services unavailable.") : RentoAuthException(msg)
    class Unknown(msg: String = "An unexpected error occurred. Please try again.") : RentoAuthException(msg)
}
```

- [ ] **M02-T01-F** Verify: domain package has **zero imports** from `android.*`, `com.google.firebase.*`, or any Android SDK. Run `grep -r "import android\.\|import com.google.firebase" app/src/main/java/com/rento/app/domain/` — must produce empty output.

---

## Task M02-T02 — Repository Interfaces

**Files:**
- `domain/repository/AuthRepository.kt`
- `domain/repository/UserRepository.kt`

### Sub-tasks
- [ ] **M02-T02-A** Create `AuthRepository.kt`:

```kotlin
package com.rento.app.domain.repository

import com.rento.app.domain.model.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun loginWithEmail(email: String, password: String): Result<Unit>
    suspend fun loginWithGoogle(idToken: String): Result<Unit>
    suspend fun register(name: String, email: String, password: String): Result<Unit>
    suspend fun resendVerification(): Result<Unit>
    suspend fun isEmailVerified(): Result<Boolean>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun signOut()
    fun getCurrentUserId(): String?
    fun isLoggedIn(): Boolean
    fun getAuthState(): Flow<AuthState>
}
```

- [ ] **M02-T02-B** Create `UserRepository.kt`:

```kotlin
package com.rento.app.domain.repository

import com.rento.app.domain.model.OnboardingData
import com.rento.app.domain.model.User

interface UserRepository {
    suspend fun createUserDocument(user: User): Result<Unit>
    suspend fun getUser(uid: String): Result<User>
    suspend fun updateFcmToken(uid: String, token: String): Result<Unit>
    suspend fun updateEmailVerified(uid: String): Result<Unit>
    suspend fun saveOnboarding(uid: String, onboarding: OnboardingData): Result<Unit>
    suspend fun getAdminEmail(): Result<String>
}
```

---

## Task M02-T03 — `AuthRepositoryImpl`

**File:** `data/repository/AuthRepositoryImpl.kt`

### Sub-tasks
- [ ] **M02-T03-A** Create `data/model/UserDto.kt` with exact Firestore schema matching section 4.3 above.

- [ ] **M02-T03-B** Create `AuthRepositoryImpl` implementing `AuthRepository`:

```kotlin
class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {
    // All method stubs to be fully implemented per rules below
}
```

- [ ] **M02-T03-C** `loginWithEmail` implementation:

```kotlin
override suspend fun loginWithEmail(email: String, password: String): Result<Unit> =
    runCatching {
        firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
        saveFcmToken()
    }.mapFirebaseException()
```

- [ ] **M02-T03-D** `loginWithGoogle` implementation:

```kotlin
override suspend fun loginWithGoogle(idToken: String): Result<Unit> =
    runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = firebaseAuth.signInWithCredential(credential).await()
        val isNew = result.additionalUserInfo?.isNewUser ?: false
        if (isNew) {
            val user = firebaseAuth.currentUser!!
            // Domain user creation delegated to CreateUserDocumentUseCase — see M02-T05
        }
        saveFcmToken()
    }.mapFirebaseException()
```

> ⚠️ Note: `loginWithGoogle` creates the Firestore user document if the user is new. This coordination is handled in `LoginWithGoogleUseCase`, not inside the repository directly. The repository is responsible only for Firebase Auth and FCM token — not Firestore user document creation. See M02-T05 for the use case coordination.

- [ ] **M02-T03-E** `register` implementation:

```kotlin
override suspend fun register(name: String, email: String, password: String): Result<Unit> =
    runCatching {
        val result = firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
        result.user?.sendEmailVerification()?.await()
        // Firestore doc creation delegated to RegisterUseCase — not here
    }.mapFirebaseException()
```

- [ ] **M02-T03-F** `resendVerification`:

```kotlin
override suspend fun resendVerification(): Result<Unit> =
    runCatching {
        firebaseAuth.currentUser?.sendEmailVerification()?.await()
            ?: throw RentoAuthException.Unknown("No authenticated user.")
    }.mapFirebaseException()
```

- [ ] **M02-T03-G** `isEmailVerified`:

```kotlin
override suspend fun isEmailVerified(): Result<Boolean> =
    runCatching {
        firebaseAuth.currentUser?.reload()?.await()
        firebaseAuth.currentUser?.isEmailVerified ?: false
    }.mapFirebaseException()
```

- [ ] **M02-T03-H** `sendPasswordReset`:

```kotlin
override suspend fun sendPasswordReset(email: String): Result<Unit> =
    runCatching {
        firebaseAuth.sendPasswordResetEmail(email.trim()).await()
    }.mapFirebaseException()
```

- [ ] **M02-T03-I** `signOut`, `getCurrentUserId`, `isLoggedIn`:

```kotlin
override suspend fun signOut() { firebaseAuth.signOut() }
override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid
override fun isLoggedIn(): Boolean = firebaseAuth.currentUser != null
```

- [ ] **M02-T03-J** `getAuthState`:

```kotlin
override fun getAuthState(): Flow<AuthState> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        trySend(
            if (user == null) AuthState.Unauthenticated
            else AuthState.Authenticated(user.uid, user.isEmailVerified)
        )
    }
    firebaseAuth.addAuthStateListener(listener)
    awaitClose { firebaseAuth.removeAuthStateListener(listener) }
}
```

- [ ] **M02-T03-K** `saveFcmToken` private helper:

```kotlin
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
```

- [ ] **M02-T03-L** `mapFirebaseException()` extension on `Result`:

```kotlin
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
```

---

## Task M02-T04 — `UserRepositoryImpl`

**File:** `data/repository/UserRepositoryImpl.kt`

### Sub-tasks
- [ ] **M02-T04-A** Implement `createUserDocument`:

```kotlin
override suspend fun createUserDocument(user: User): Result<Unit> = runCatching {
    val dto = user.toDto()
    firestore.collection("users").document(user.uid).set(dto).await()
}
```

- [ ] **M02-T04-B** Implement mapping extensions `User.toDto()` and `DocumentSnapshot.toUser()`. These live in a private extensions file `data/repository/UserMapper.kt`.

- [ ] **M02-T04-C** Implement `getUser`:

```kotlin
override suspend fun getUser(uid: String): Result<User> = runCatching {
    val snapshot = firestore.collection("users").document(uid).get().await()
    snapshot.toUser() ?: throw Exception("User document not found for uid=$uid")
}
```

- [ ] **M02-T04-D** Implement `updateFcmToken`, `updateEmailVerified`, `saveOnboarding`:

```kotlin
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
```

- [ ] **M02-T04-E** Implement `getAdminEmail`:

```kotlin
override suspend fun getAdminEmail(): Result<String> = runCatching {
    val doc = firestore.collection("config").document("app").get().await()
    doc.getString("adminEmail") ?: "admin@rentopk.com"
}
```

---

## Task M02-T05 — `AuthViewModel`

**File:** `presentation/auth/AuthViewModel.kt`

### Use Cases (all fully implemented — no pass-through stubs)

- [ ] **M02-T05-A** `LoginWithEmailUseCase`: validates email format + non-empty password, then calls `authRepository.loginWithEmail`. Also calls `userRepository.getUser` to check `isBlocked` and `onboardingComplete` and returns `AuthDestination`.

```kotlin
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
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
            RentoAuthException.InvalidEmail("Enter a valid email address.")
        password.isBlank() -> RentoAuthException.WrongPassword("Password is required.")
        else -> null
    }
}
```

> ⚠️ Validation uses `android.util.Patterns.EMAIL_ADDRESS` — this is the **only** permitted Android import in use cases. An alternative regex may be used to keep the domain layer Android-free. Prefer the regex approach if strict domain purity is required:
> `private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")`.

- [ ] **M02-T05-B** `resolveDestination` shared utility (private to use cases, placed in `domain/usecase/auth/AuthDestinationResolver.kt`):

```kotlin
internal fun resolveDestination(user: User, emailVerified: Boolean): AuthDestination = when {
    user.isBlocked          -> AuthDestination.BLOCKED
    !emailVerified          -> AuthDestination.VERIFY_EMAIL
    !user.onboardingComplete -> AuthDestination.ONBOARDING
    else                     -> AuthDestination.HOME
}
```

- [ ] **M02-T05-C** `LoginWithGoogleUseCase`: handles new vs returning user branching:

```kotlin
class LoginWithGoogleUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(idToken: String): Result<AuthDestination> =
        runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = /* delegated to AuthRepository.loginWithGoogle */ // see note
            // After auth succeeds:
            val uid = authRepository.getCurrentUserId()!!
            val userResult = userRepository.getUser(uid)
            val user = if (userResult.isFailure) {
                // New Google user — create document
                val firebaseUser = GoogleAuthHelper.currentUser()  // helper returns FirebaseUser
                val newUser = User(
                    uid = uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    phone = null, photoUrl = firebaseUser.photoUrl?.toString(),
                    emailVerified = true,  // Google accounts are always verified
                    isBlocked = false, blockReason = null,
                    accountType = AccountType.INDIVIDUAL,
                    defaultMode = UserMode.LOOKING,
                    onboardingComplete = false,
                    province = "", city = "",
                    deviceLat = null, deviceLng = null,
                    referralSource = emptyList(),
                    currentPackageId = null, packageName = null,
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
```

> ⚠️ `GoogleAuthHelper` is a thin adapter in the `data/` layer that wraps `FirebaseAuth.getInstance().currentUser`. It is injected into the use case via the data layer; the domain model never directly imports FirebaseUser.

- [ ] **M02-T05-D** `RegisterUseCase`: validates all fields, registers, creates Firestore doc:

```kotlin
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
                uid = uid, name = name.trim(), email = email.trim(),
                phone = null, photoUrl = null,
                emailVerified = false, isBlocked = false, blockReason = null,
                accountType = AccountType.INDIVIDUAL,
                defaultMode = UserMode.LOOKING,
                onboardingComplete = false,
                province = "", city = "",
                deviceLat = null, deviceLng = null,
                referralSource = emptyList(),
                currentPackageId = null, packageName = null,
                createdAt = System.currentTimeMillis(),
            )
            userRepository.createUserDocument(newUser).getOrThrow()
        }
    }

    private fun validateRegistrationInput(
        name: String, email: String, password: String, confirmPassword: String,
    ): RentoAuthException? = when {
        name.isBlank()          -> RentoAuthException.Unknown("Full name is required.")
        name.trim().length < 2  -> RentoAuthException.Unknown("Name must be at least 2 characters.")
        email.isBlank()         -> RentoAuthException.InvalidEmail("Email is required.")
        !isValidEmail(email)    -> RentoAuthException.InvalidEmail()
        password.length < 8     -> RentoAuthException.WeakPassword()
        password != confirmPassword -> RentoAuthException.Unknown("Passwords do not match.")
        else -> null
    }

    private fun isValidEmail(email: String): Boolean =
        Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(email.trim())
}
```

- [ ] **M02-T05-E** `CheckEmailVerifiedUseCase`, `ResendVerificationUseCase`, `SignOutUseCase`, `SendPasswordResetUseCase`: each wraps its `AuthRepository` counterpart and adds any required validation or coordination. These are only use cases if they contain logic — `SignOutUseCase` just calls `signOut()` (and is the only pass-through permitted, as it acts as a coordination point for clearing local state in future modules).

- [ ] **M02-T05-F** `AuthViewModel` full implementation:

```kotlin
class AuthViewModel(
    private val loginWithEmail: LoginWithEmailUseCase,
    private val loginWithGoogle: LoginWithGoogleUseCase,
    private val register: RegisterUseCase,
    private val resendVerification: ResendVerificationUseCase,
    private val checkEmailVerified: CheckEmailVerifiedUseCase,
    private val sendPasswordReset: SendPasswordResetUseCase,   // ← used only on ForgotPassword; ForgotPasswordViewModel is separate
    private val signOut: SignOutUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            loginWithEmail.invoke(email, password).fold(
                onSuccess = { destination -> _uiState.value = AuthUiState.Success(destination) },
                onFailure = { e -> _uiState.value = AuthUiState.Error(e.message ?: "Unknown error.") },
            )
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            loginWithGoogle.invoke(idToken).fold(
                onSuccess = { destination -> _uiState.value = AuthUiState.Success(destination) },
                onFailure = { e -> _uiState.value = AuthUiState.Error(e.message ?: "Unknown error.") },
            )
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            register.invoke(name, email, password, confirmPassword).fold(
                onSuccess  = { _uiState.value = AuthUiState.Success(AuthDestination.VERIFY_EMAIL) },
                onFailure  = { e -> _uiState.value = AuthUiState.Error(e.message ?: "Unknown error.") },
            )
        }
    }

    fun checkEmailVerified() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            checkEmailVerified.invoke().fold(
                onSuccess = { verified ->
                    if (verified) _uiState.value = AuthUiState.Success(AuthDestination.ONBOARDING)
                    else _uiState.value = AuthUiState.Idle
                },
                onFailure = { e -> _uiState.value = AuthUiState.Error(e.message ?: "Unknown error.") },
            )
        }
    }

    fun resendVerification() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            resendVerification.invoke().fold(
                onSuccess = { _uiState.value = AuthUiState.Idle },
                onFailure = { e -> _uiState.value = AuthUiState.Error(e.message ?: "Unknown error.") },
            )
        }
    }

    fun signOut() {
        viewModelScope.launch { signOut.invoke() }
        _uiState.value = AuthUiState.Idle
    }

    fun resetState() { _uiState.value = AuthUiState.Idle }
}
```

---

## Task M02-T06 — `ForgotPasswordViewModel`

**File:** `presentation/auth/ForgotPasswordViewModel.kt`

### Sub-tasks
- [ ] **M02-T06-A** Implement `ForgotPasswordViewModel` with 60-second resend throttle:

```kotlin
class ForgotPasswordViewModel(
    private val sendPasswordReset: SendPasswordResetUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Idle)
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private var lastSentEmail: String = ""
    private var throttleJob: Job? = null

    fun sendReset(email: String) {
        if (email.isBlank()) {
            _uiState.value = ForgotPasswordUiState.Error("Please enter your email address.")
            return
        }
        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState.Loading
            sendPasswordReset.invoke(email).fold(
                onSuccess = {
                    lastSentEmail = email
                    _uiState.value = ForgotPasswordUiState.Success(email)
                    startThrottle()
                },
                onFailure = { e ->
                    _uiState.value = ForgotPasswordUiState.Error(e.message ?: "Unknown error.")
                },
            )
        }
    }

    fun resend() {
        if (_uiState.value is ForgotPasswordUiState.Throttled) return
        sendReset(lastSentEmail)
    }

    private fun startThrottle() {
        throttleJob?.cancel()
        throttleJob = viewModelScope.launch {
            repeat(60) { secondsLeft ->
                delay(1_000)
                val remaining = 59 - secondsLeft
                if (remaining > 0) _uiState.value = ForgotPasswordUiState.Throttled(remaining)
                // At 0: throttle expires — state returns to Success so resend button becomes active
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        throttleJob?.cancel()
    }
}
```

---

## Task M02-T07 — `OnboardingViewModel`

**File:** `presentation/auth/OnboardingViewModel.kt`

### Sub-tasks
- [ ] **M02-T07-A** `SaveOnboardingUseCase`: validates all required onboarding fields, then calls `userRepository.saveOnboarding`.

- [ ] **M02-T07-B** `GetUserUseCase`: fetches user from `UserRepository`.

- [ ] **M02-T07-C** Implement `OnboardingViewModel`:

```kotlin
class OnboardingViewModel(
    private val saveOnboarding: SaveOnboardingUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    // Step 1
    fun selectMode(mode: UserMode) {
        _uiState.update { it.copy(defaultMode = mode) }
    }

    // Step 2
    fun updateName(name: String)   { _uiState.update { it.copy(name = name) } }
    fun updatePhone(phone: String) { _uiState.update { it.copy(phone = phone) } }
    fun updateDob(dob: String?)    { _uiState.update { it.copy(dateOfBirth = dob) } }
    fun updateProvince(p: String)  { _uiState.update { it.copy(province = p, city = "") } }
    fun updateCity(c: String)      { _uiState.update { it.copy(city = c) } }
    fun updateLocation(lat: Double, lng: Double) {
        _uiState.update { it.copy(deviceLat = lat, deviceLng = lng) }
    }

    // Step 3
    fun selectAccountType(type: AccountType) {
        _uiState.update { it.copy(accountType = type) }
    }

    // Step 4
    fun toggleReferral(source: String) {
        _uiState.update {
            val updated = if (source in it.referralSources)
                it.referralSources - source
            else
                it.referralSources + source
            it.copy(referralSources = updated)
        }
    }

    // Navigation
    fun nextStep() {
        val s = _uiState.value
        val validationError = when (s.currentStep) {
            0 -> if (s.defaultMode == null) "Please choose how you'll use RentO." else null
            1 -> when {
                s.name.isBlank()      -> "Full name is required."
                s.province.isBlank()  -> "Please select your province."
                s.city.isBlank()      -> "Please select your city."
                else                  -> null
            }
            2 -> if (s.accountType == null) "Please select your account type." else null
            else -> null
        }
        if (validationError != null) {
            _uiState.update { it.copy(error = validationError) }
            return
        }
        _uiState.update { it.copy(currentStep = it.currentStep + 1, error = null) }
    }

    fun prevStep() {
        _uiState.update { it.copy(currentStep = (it.currentStep - 1).coerceAtLeast(0), error = null) }
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }

    fun finish() {
        val s = _uiState.value
        val uid = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val data = OnboardingData(
                defaultMode    = s.defaultMode ?: UserMode.LOOKING,
                name           = s.name.trim(),
                phone          = s.phone.trim(),
                dateOfBirth    = s.dateOfBirth,
                province       = s.province,
                city           = s.city,
                deviceLat      = s.deviceLat,
                deviceLng      = s.deviceLng,
                accountType    = s.accountType ?: AccountType.INDIVIDUAL,
                referralSources = s.referralSources.toList(),
            )
            saveOnboarding.invoke(uid, data).fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, currentStep = 4) } }, // step 4 = done
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } },
            )
        }
    }
}
```

---

## Task M02-T08 — Koin DI Module

**File:** `di/AuthModule.kt`

### Sub-tasks
- [ ] **M02-T08-A** Implement full `authModule` as defined in section 4.7.

- [ ] **M02-T08-B** Add FirebaseAuth and FirebaseFirestore singletons to a shared `FirebaseModule.kt` (if not already present from Module 01 bootstrap):

```kotlin
val firebaseModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseMessaging.getInstance() }
}
```

- [ ] **M02-T08-C** Register both modules in `RentoApplication`:

```kotlin
startKoin {
    androidContext(this@RentoApplication)
    modules(firebaseModule, authModule)
}
```

- [ ] **M02-T08-D** Verify: run `./gradlew assembleDebug` — zero Koin binding errors.

---

## Task M02-T09 — Navigation Graph

**File:** `presentation/navigation/RentoNavGraph.kt`

This **replaces** the stub `RentoNavGraph` created in Module 01 entirely.

### Sub-tasks
- [ ] **M02-T09-A** Define all route constants:

```kotlin
object AuthRoutes {
    const val SPLASH         = "splash"
    const val WELCOME        = "welcome"
    const val LOGIN          = "auth/login"
    const val REGISTER       = "auth/register"
    const val FORGOT_PW      = "auth/forgot_password"
    const val VERIFY_EMAIL   = "auth/verify_email"
    const val BLOCKED        = "auth/blocked"
    const val ONBOARDING     = "onboarding"
    const val HOME           = "home"          // placeholder — implemented in Module 03
}
```

- [ ] **M02-T09-B** Implement `RentoNavGraph`:

```kotlin
@Composable
fun RentoNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController   = navController,
        startDestination = AuthRoutes.SPLASH,
        modifier        = modifier,
        enterTransition  = { NavTransitions.pushEnter },
        exitTransition   = { NavTransitions.pushExit },
        popEnterTransition = { NavTransitions.popEnter },
        popExitTransition  = { NavTransitions.popExit },
    ) {
        composable(AuthRoutes.SPLASH) {
            SplashScreen(
                onNavigateToWelcome   = { navController.navigate(AuthRoutes.WELCOME)   { popUpTo(AuthRoutes.SPLASH) { inclusive = true } } },
                onNavigateToHome      = { navController.navigate(AuthRoutes.HOME)       { popUpTo(AuthRoutes.SPLASH) { inclusive = true } } },
                onNavigateToOnboarding= { navController.navigate(AuthRoutes.ONBOARDING){ popUpTo(AuthRoutes.SPLASH) { inclusive = true } } },
                onNavigateToVerify    = { navController.navigate(AuthRoutes.VERIFY_EMAIL){ popUpTo(AuthRoutes.SPLASH) { inclusive = true } } },
                onNavigateToBlocked   = { navController.navigate(AuthRoutes.BLOCKED)   { popUpTo(AuthRoutes.SPLASH) { inclusive = true } } },
            )
        }
        composable(AuthRoutes.WELCOME) {
            WelcomeScreen(
                onSignIn   = { navController.navigate(AuthRoutes.LOGIN) },
                onLooking  = { navController.navigate(AuthRoutes.REGISTER) },
                onHosting  = { navController.navigate(AuthRoutes.REGISTER) },
            )
        }
        composable(AuthRoutes.LOGIN) {
            LoginScreen(
                onNavigateToHome       = { navController.navigate(AuthRoutes.HOME)       { popUpTo(AuthRoutes.WELCOME) { inclusive = true } } },
                onNavigateToOnboarding = { navController.navigate(AuthRoutes.ONBOARDING) { popUpTo(AuthRoutes.WELCOME) { inclusive = true } } },
                onNavigateToRegister   = { navController.navigate(AuthRoutes.REGISTER)   { popUpTo(AuthRoutes.LOGIN)   { inclusive = true } } },
                onNavigateToForgotPw   = { navController.navigate(AuthRoutes.FORGOT_PW) },
                onNavigateToVerify     = { navController.navigate(AuthRoutes.VERIFY_EMAIL) },
                onNavigateToBlocked    = { navController.navigate(AuthRoutes.BLOCKED)    { popUpTo(AuthRoutes.WELCOME) { inclusive = true } } },
                onBack                 = { navController.popBackStack() },
            )
        }
        composable(AuthRoutes.REGISTER) {
            RegisterScreen(
                onNavigateToVerify   = { navController.navigate(AuthRoutes.VERIFY_EMAIL) { popUpTo(AuthRoutes.WELCOME) { inclusive = false } } },
                onNavigateToOnboarding = { navController.navigate(AuthRoutes.ONBOARDING){ popUpTo(AuthRoutes.WELCOME) { inclusive = true } } },
                onNavigateToLogin    = { navController.navigate(AuthRoutes.LOGIN)       { popUpTo(AuthRoutes.LOGIN)   { inclusive = true } } },
                onBack               = { navController.popBackStack() },
            )
        }
        composable(AuthRoutes.FORGOT_PW) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }
        composable(AuthRoutes.VERIFY_EMAIL) {
            EmailVerificationScreen(
                onVerified  = { navController.navigate(AuthRoutes.ONBOARDING){ popUpTo(AuthRoutes.SPLASH) { inclusive = true } } },
                onSignOut   = { navController.navigate(AuthRoutes.WELCOME)   { popUpTo(AuthRoutes.SPLASH) { inclusive = true } } },
            )
        }
        composable(AuthRoutes.BLOCKED) {
            BlockedScreen(
                onSignOut = { navController.navigate(AuthRoutes.WELCOME) { popUpTo(AuthRoutes.SPLASH) { inclusive = true } } },
            )
        }
        composable(AuthRoutes.ONBOARDING) {
            OnboardingScreen(
                onComplete = { navController.navigate(AuthRoutes.HOME) { popUpTo(AuthRoutes.SPLASH) { inclusive = true } } },
            )
        }
        composable(AuthRoutes.HOME) {
            // Module 03 placeholder — renders EmptyState until HomeScreen is implemented
            Box(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.rentoColors.bg0),
                contentAlignment = Alignment.Center,
            ) {
                EmptyState(
                    icon     = RentoIcons.Home,
                    title    = "Home",
                    subtitle = "Module 03 — Home screen coming soon.",
                )
            }
        }
    }
}
```

---

## Task M02-T10 — Screen: `SplashScreen`

**File:** `presentation/auth/SplashScreen.kt`
**Route:** `splash`
**Prototype reference:** `function SBar()` + splash logic

### Exact Specification
```
Background: RentoColors.bg0 (full screen, no mesh — plain dark)
Content: Box fillMaxSize, contentAlignment = Center

Centre logo block (Column, CenterHorizontally, no gap):
  Animated container (floatY animation applied via Modifier.offset):
    Box: 92dp × 92dp
    Background: gradientPrimary brush
    Shape: RoundedCornerShape(30dp)
    Border: 1.5dp, RentoColors.primaryRing
    Icon: RentoIcons.Home, 46dp, Color.White
    Shadow: elevation 12dp, tinted RentoColors.primaryRing

  Spacer(16dp)

  "RentO" — Fraunces 32sp SemiBold, GradientText style

Boot sequence (no progress indicator — just delay):
  The SplashScreen itself does NOT check auth state.
  It calls onReady after a 1.8-second initial delay.
  All routing logic lives in SplashViewModel.
```

### ViewModel: `SplashViewModel`

```kotlin
class SplashViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    sealed class SplashDestination {
        object Welcome      : SplashDestination()
        object Home         : SplashDestination()
        object Onboarding   : SplashDestination()
        object VerifyEmail  : SplashDestination()
        object Blocked      : SplashDestination()
    }

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1_800)
            checkAuthAndRoute()
        }
    }

    private suspend fun checkAuthAndRoute() {
        if (!authRepository.isLoggedIn()) {
            _destination.value = SplashDestination.Welcome
            return
        }
        val uid = authRepository.getCurrentUserId()!!
        val emailVerified = authRepository.isEmailVerified().getOrDefault(false)
        if (!emailVerified) {
            _destination.value = SplashDestination.VerifyEmail
            return
        }
        userRepository.getUser(uid).fold(
            onSuccess = { user ->
                _destination.value = when {
                    user.isBlocked          -> SplashDestination.Blocked
                    !user.onboardingComplete -> SplashDestination.Onboarding
                    else                     -> SplashDestination.Home
                }
            },
            onFailure = {
                // Firestore fetch failed (offline?). Route to Welcome as safe fallback.
                _destination.value = SplashDestination.Welcome
            },
        )
    }
}
```

Add `SplashViewModel` to `authModule` Koin binding:
```kotlin
viewModel { SplashViewModel(get(), get()) }
```

### Sub-tasks
- [ ] **M02-T10-A** Implement `SplashViewModel` (add to `AuthModule.kt`).
- [ ] **M02-T10-B** Implement `SplashScreen` composable. Collect `SplashViewModel.destination` in a `LaunchedEffect`:

```kotlin
val destination by viewModel.destination.collectAsStateWithLifecycle()
LaunchedEffect(destination) {
    when (val d = destination) {
        is SplashDestination.Welcome     -> onNavigateToWelcome()
        is SplashDestination.Home        -> onNavigateToHome()
        is SplashDestination.Onboarding  -> onNavigateToOnboarding()
        is SplashDestination.VerifyEmail -> onNavigateToVerify()
        is SplashDestination.Blocked     -> onNavigateToBlocked()
        null                             -> Unit
    }
}
```
- [ ] **M02-T10-C** `floatY` animation: apply `rememberFloatYOffset()` from Module 01 `Animations.kt` to the logo container `Modifier.offset { IntOffset(0, (floatY.value * density).roundToInt()) }`.
- [ ] **M02-T10-D** `@Preview` dark + light.

---

## Task M02-T11 — Screen: `WelcomeScreen`

**File:** `presentation/auth/WelcomeScreen.kt`
**Route:** `welcome`
**Prototype reference:** `function Welcome({nav})`

### Exact Specification
```
Background: MeshBackground (fills entire screen)
Content: Column(fillMaxSize, CenterHorizontally, SpaceBetween)

TOP SECTION (weight = 1f, centred vertically):
  StatusBar at very top

  Logo + Hero Block (Column, CenterHorizontally, padding top=64dp):
    Logo box (floatY animated):
      92dp × 92dp, gradientPrimary brush, RoundedCornerShape(30dp)
      Border: 1.5dp RentoColors.primaryRing
      Icon: RentoIcons.Home, 46dp, Color.White
      Shadow: elevation 12dp, RentoColors.primaryRing tint

    Spacer(20dp)

    Hero text (centred):
      Line 1: "Your Space" — Fraunces 50sp SemiBold, RentoColors.t0
      Line 2: GradientText("Awaits You", style = displayXL)
      Font: Fraunces 50sp SemiBold on both lines
      TextAlign: Center

    Spacer(14dp)

    Subtitle text:
      "Rooms, homes & shared spaces that truly feel like home — across Pakistan."
      14sp, RentoColors.t1, TextAlign.Center, maxWidth = 272dp

INTENT CARDS SECTION (Column, 12dp gap, padding horizontal=20dp, top=44dp, bottom=24dp):
  su animation: each card enters with fadeInSlideUpModifier(delay = 0 for card 1, 100ms for card 2)

  LOOKING card (onClick → onLooking):
    Background: RentoColors.bg2
    Border:     1.5dp, RentoColors.border2
    Shape:      RoundedCornerShape(24dp)
    Padding:    20dp
    Row(CenterVertically, 18dp gap):
      Icon box: 54dp × 54dp, RoundedCornerShape(18dp), RentoColors.primaryTint fill, 1.5dp RentoColors.primaryRing border
        Icon: RentoIcons.Search, 26dp, RentoColors.primary
      Column:
        "I'm Looking" — 16sp, Bold, RentoColors.t0
        "Find accommodation that fits your life" — 13sp, RentoColors.t2, top=4dp
      Spacer(weight=1f)
      RentoIcons.Chevron, 20dp, RentoColors.t3
    Pressed: scale(0.985), spring(DampingRatioMediumBouncy)

  HOSTING card (onClick → onHosting):
    Same structure.
    Icon box: RentoColors.primaryTint fill (same as looking — both use primary tint per prototype)
      Icon: RentoIcons.Home, 26dp, RentoColors.primary
    "I'm Hosting" — 16sp, Bold, RentoColors.t0
    "List your space and find the right tenant" — 13sp, RentoColors.t2

BOTTOM SECTION (padding bottom=32dp, horizontal=20dp):
  "Have an account? Sign In":
    Row(CenterHorizontally):
      "Have an account? " — 14sp, RentoColors.t2
      "Sign In" — 14sp, Bold, RentoColors.primary, clickable → onSignIn

  Spacer(16dp)

  Footer:
    Row(CenterHorizontally, 8dp gap):
      "Terms of Service" — 11sp, RentoColors.t3, underline (no navigation in V1 — opens WebView)
      "·" — 11sp, RentoColors.t3
      "Privacy Policy" — 11sp, RentoColors.t3, underline
```

### Sub-tasks
- [ ] **M02-T11-A** Implement `WelcomeScreen`.
- [ ] **M02-T11-B** Apply `floatY` animation to logo box using `rememberFloatYOffset()` from Module 01.
- [ ] **M02-T11-C** Apply staggered `fadeInSlideUpModifier` to each intent card. Cards become visible on `LaunchedEffect(Unit) { visible = true }`.
- [ ] **M02-T11-D** Press scale via `interactionSource.collectIsPressedAsState()` + `animateFloatAsState(spring(...))`.
- [ ] **M02-T11-E** `@Preview` dark + light.
- [ ] **M02-T11-F** Strings: see M02-T24 for complete list.

---

## Task M02-T12 — Screen: `LoginScreen`

**File:** `presentation/auth/LoginScreen.kt`
**Route:** `auth/login`
**Prototype reference:** `function SignIn({nav})`

### Exact Specification
```
Background: MeshBackground
Content: Column(fillMaxSize, verticalScroll=rememberScrollState())
  Padding: horizontal=20dp

StatusBar

AuthBackButton(top=13dp) → onBack

Header block (top=32dp, bottom=32dp):
  "Welcome Back" — Fraunces 32sp SemiBold, RentoColors.t0
    su animation (slideUp + fadeIn, 320ms, FastOutSlowInEasing)
  "Sign in to continue to your account." — 14sp, RentoColors.t2, top=8dp
    su animation (delay 80ms)

FIELDS:
  BoxedInputField:
    label = null (no SectionLabel — fields are self-explanatory with icon)
    placeholder = stringResource(R.string.auth_email_placeholder) = "Email address"
    leadingIcon = RentoIcons.Mail
    keyboardType = KeyboardType.Email
    imeAction = ImeAction.Next

  Spacer(16dp)

  BoxedInputField:
    placeholder = "Password"
    leadingIcon = RentoIcons.Lock
    isPassword = true
    imeAction = ImeAction.Done
    onImeAction = { viewModel.login(email, password) }

  "Forgot Password?" (Row, End alignment, top=10dp):
    14sp → no: 12sp, Bold, RentoColors.primary
    clickable → onNavigateToForgotPw

  Spacer(24dp)

  Error banner (ErrorBanner, visible when uiState = Error):
    message = uiState.error
    bottom margin: 16dp

  PrimaryButton("Sign In"):
    isLoading = uiState is AuthUiState.Loading
    onClick = { viewModel.loginWithEmail(email, password) }

  Spacer(28dp)

  AuthDivider()   ← "or" divider from M02-T21

  Spacer(20dp)

  GoogleSignInButton(
    onClick = { /* launch Google One Tap, pass idToken to viewModel.loginWithGoogle */ }
  )

  Spacer(32dp)

  "Don't have an account? Sign Up" row:
    "Don't have an account? " — 14sp, RentoColors.t2
    "Sign Up" — 14sp, Bold, RentoColors.primary → onNavigateToRegister

  Spacer(32dp)
```

### Google Sign-In Integration
```kotlin
// In LoginScreen composable:
val googleSignInLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartIntentSenderForResult()
) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        val credential = Identity.getSignInClient(context)
            .getSignInCredentialFromIntent(result.data)
        val idToken = credential.googleIdToken
        if (idToken != null) viewModel.loginWithGoogle(idToken)
    }
}

fun launchGoogleSignIn() {
    val request = GetSignInIntentRequest.builder()
        .setServerClientId(stringResource(R.string.google_web_client_id))
        .build()
    Identity.getSignInClient(context)
        .getSignInIntent(request)
        .addOnSuccessListener { pendingIntent ->
            googleSignInLauncher.launch(
                IntentSenderRequest.Builder(pendingIntent).build()
            )
        }
        .addOnFailureListener { /* snackbar: Play Services unavailable */ }
}
```

### Auth State Side Effects

```kotlin
LaunchedEffect(uiState) {
    when (val s = uiState) {
        is AuthUiState.Success -> when (s.destination) {
            AuthDestination.HOME         -> onNavigateToHome()
            AuthDestination.ONBOARDING   -> onNavigateToOnboarding()
            AuthDestination.VERIFY_EMAIL -> onNavigateToVerify()
            AuthDestination.BLOCKED      -> onNavigateToBlocked()
        }
        else -> Unit
    }
}
```

### Sub-tasks
- [ ] **M02-T12-A** Implement `LoginScreen`. Collect `viewModel.uiState` via `collectAsStateWithLifecycle()`.
- [ ] **M02-T12-B** Apply `su` (slide-up fade-in) to title and subtitle using `fadeInSlideUpModifier`.
- [ ] **M02-T12-C** Wire Google Sign-In launcher. `R.string.google_web_client_id` must be added to `strings.xml` (value = OAuth client ID from `google-services.json` — the **web client** ID, not the Android client ID).
- [ ] **M02-T12-D** `ErrorBanner` appears with `AnimatedVisibility` wrapping — fades in when error non-null.
- [ ] **M02-T12-E** On `AuthUiState.Loading`: `PrimaryButton(isLoading=true)` — button disabled automatically.
- [ ] **M02-T12-F** `viewModel.resetState()` called in `DisposableEffect(Unit) { onDispose { viewModel.resetState() } }`.
- [ ] **M02-T12-G** `@Preview` dark + light. Two preview variants: default state, error state.

---

## Task M02-T13 — Screen: `RegisterScreen`

**File:** `presentation/auth/RegisterScreen.kt`
**Route:** `auth/register`
**Prototype reference:** `function SignUp({nav})`

### Exact Specification
```
Background: MeshBackground
Content: Column(fillMaxSize, verticalScroll, padding horizontal=20dp)

StatusBar
AuthBackButton(top=13dp) → onBack

Header block (top=32dp, bottom=32dp):
  "Create Account" — Fraunces 32sp SemiBold, RentoColors.t0, su animation
  "Join thousands finding their perfect space." — 14sp, RentoColors.t2, top=8dp, su (80ms delay)

FIELDS (each separated by 16dp Spacer):
  BoxedInputField:
    placeholder = "Full Name"
    leadingIcon = RentoIcons.User
    keyboardType = KeyboardType.Text
    imeAction = ImeAction.Next
    maxLength = 60

  BoxedInputField:
    placeholder = "Email address"
    leadingIcon = RentoIcons.Mail
    keyboardType = KeyboardType.Email
    imeAction = ImeAction.Next

  BoxedInputField:
    placeholder = "Password"
    leadingIcon = RentoIcons.Lock
    isPassword = true
    imeAction = ImeAction.Next

  BoxedInputField:
    placeholder = "Confirm Password"
    leadingIcon = RentoIcons.Lock
    isPassword = true
    imeAction = ImeAction.Done
    onImeAction = { viewModel.register(name, email, pw, confirm) }

Spacer(8dp)

Password requirements row (visible once password.length >= 1):
  AnimatedVisibility(visible = password.isNotEmpty()):
    Row(8dp gap, wrap):
      RentoChip("8+ chars",   selected = password.length >= 8,          onClick = {})
      RentoChip("Uppercase",  selected = password.any { it.isUpperCase() }, onClick = {})
      RentoChip("Number",     selected = password.any { it.isDigit() },  onClick = {})
  ← chips are non-interactive (no onClick behaviour) — they are read-only validators

Spacer(16dp)

ErrorBanner (AnimatedVisibility, same as LoginScreen)

Spacer(8dp)

PrimaryButton("Create Account"):
  isLoading = uiState is AuthUiState.Loading
  onClick = { viewModel.register(name, email, password, confirm) }

Spacer(20dp)

Terms acceptance note:
  "By creating an account you agree to our " — 12sp, RentoColors.t2
  "Terms of Service" — 12sp, RentoColors.primary, underline
  " and " — 12sp, RentoColors.t2
  "Privacy Policy" — 12sp, RentoColors.primary, underline
  Inline flow (InlineTextContent or AnnotatedString with clickable spans)

Spacer(20dp)

AuthDivider()

Spacer(20dp)

GoogleSignInButton(onClick = { /* same Google One Tap flow */ })

Spacer(28dp)

"Already have an account? Sign In":
  "Already have an account? " — 14sp, RentoColors.t2
  "Sign In" — 14sp, Bold, RentoColors.primary → onNavigateToLogin

Spacer(32dp)
```

### Sub-tasks
- [ ] **M02-T13-A** Implement `RegisterScreen`. Uses same `AuthViewModel` as `LoginScreen`.
- [ ] **M02-T13-B** Password requirement chips are `RentoChip` with `enabled=false` (non-clickable, colour-change only). The `selected` param drives the colour — no `onClick` side effect.
- [ ] **M02-T13-C** Password requirement row `AnimatedVisibility(visible = password.isNotEmpty())` — `expandVertically() + fadeIn()` entry.
- [ ] **M02-T13-D** Terms text uses `AnnotatedString` with `SpanStyle` for the coloured underlined parts. Tap detection via `Modifier.pointerInput` with `detectTapGestures` on the offsets (or `ClickableText` if simpler). In V1, tapping Terms/Privacy opens a `Toast` "Coming soon" — no WebView yet.
- [ ] **M02-T13-E** Google Sign-In same implementation as `LoginScreen` — extract `launchGoogleSignIn` to a shared helper function within the auth package.
- [ ] **M02-T13-F** Auth state → navigation side effect: on `VERIFY_EMAIL` → `onNavigateToVerify()`; on `ONBOARDING` → `onNavigateToOnboarding()`.
- [ ] **M02-T13-G** `@Preview` dark + light, default + error + loading.

---

## Task M02-T14 — Screen: `ForgotPasswordScreen`

**File:** `presentation/auth/ForgotPasswordScreen.kt`
**Route:** `auth/forgot_password`
**Prototype reference:** Section 32.4 journey spec

### Exact Specification — Idle State
```
Background: MeshBackground
Content: Column(fillMaxSize, scroll, padding horizontal=20dp)

StatusBar
AuthBackButton(top=13dp) → onBack

Illustration block (centred, top=60dp, bottom=40dp):
  Outer circle: 80dp, RentoColors.bg3, RoundedCornerShape(100dp), border 1.5dp RentoColors.border2
  Inner icon: RentoIcons.Mail, 36dp, RentoColors.primary
  floatY animation applied to entire block

"Forgot Password?" — Fraunces 28sp SemiBold, RentoColors.t0, su animation
Spacer(8dp)
"Enter your email and we'll send a reset link." — 14sp, RentoColors.t2, su (80ms delay)

Spacer(32dp)

BoxedInputField:
  placeholder = "Email address"
  leadingIcon = RentoIcons.Mail
  keyboardType = KeyboardType.Email
  imeAction = ImeAction.Done
  onImeAction = { viewModel.sendReset(email) }

Spacer(16dp)

ErrorBanner (AnimatedVisibility when uiState is Error)

Spacer(16dp)

PrimaryButton("Send Reset Link"):
  isLoading = uiState is ForgotPasswordUiState.Loading
  onClick = { viewModel.sendReset(email) }
```

### Exact Specification — Success State
```
Full content replaced (crossfade transition):
  AnimatedContent(targetState = isSuccess):

  Illustration: 80dp circle, RentoColors.primaryTint, RentoColors.primaryRing border
    Icon: RentoIcons.Check, 36dp, RentoColors.primary
    BounceEffect(trigger = isSuccess) wrapping the entire block

  Spacer(20dp)

  "Check your inbox" — Fraunces 26sp SemiBold, RentoColors.t0, centred
  Spacer(8dp)
  "We've sent a link to {sentToEmail}. Check spam too." — 14sp, RentoColors.t2, centred

  Spacer(32dp)

  "Resend Email" — OutlinePrimaryButton:
    enabled = uiState !is Throttled
    When Throttled: text becomes "Resend in {secondsRemaining}s" — 13sp, RentoColors.t2
    When active: text = "Resend Email", RentoColors.primary

  Spacer(20dp)

  "Back to Sign In" — 13sp, Bold, RentoColors.primary, centred → onBack
```

### Sub-tasks
- [ ] **M02-T14-A** Implement `ForgotPasswordScreen`. Uses `ForgotPasswordViewModel`.
- [ ] **M02-T14-B** State-driven content: use `AnimatedContent` on `isSuccess = uiState is Success` — `fadeIn/fadeOut` transition, 260ms.
- [ ] **M02-T14-C** `BounceEffect` on check icon triggered once when entering Success state.
- [ ] **M02-T14-D** Throttle counter display: observe `Throttled(secondsRemaining)` state, format as `"Resend in ${secondsRemaining}s"`.
- [ ] **M02-T14-E** `@Preview`: idle state dark, success state dark, throttled state dark.

---

## Task M02-T15 — Screen: `EmailVerificationScreen`

**File:** `presentation/auth/EmailVerificationScreen.kt`
**Route:** `auth/verify_email`

### Exact Specification
```
Background: MeshBackground
Content: Column(fillMaxSize, scroll, padding horizontal=20dp, CenterHorizontally)

StatusBar

Illustration block (centred, top=80dp, bottom=32dp):
  80dp circle, RentoColors.bg3 fill, 1.5dp RentoColors.border2 border
  RentoIcons.Mail, 36dp, RentoColors.primary
  floatY animation

"Verify your email" — Fraunces 28sp SemiBold, RentoColors.t0, centred
Spacer(8dp)
"We've sent a verification email. Open it and tap the link to continue." — 14sp, RentoColors.t2, centred, maxWidth=272dp
Spacer(8dp)
"{currentUserEmail}" — 14sp, Bold, RentoColors.primary, centred

Spacer(40dp)

PrimaryButton("I've Verified"):
  isLoading = uiState is Loading
  onClick = { viewModel.checkEmailVerified() }

Spacer(14dp)

"Resend Email" — OutlinePrimaryButton:
  enabled = resendEnabled (throttled 60s same as ForgotPassword)
  When throttled: text = "Resend in {n}s"
  onClick = { viewModel.resendVerification() }

Spacer(40dp)

"Wrong account?" — 13sp, RentoColors.t2, centred
"Sign Out" — 13sp, Bold, RentoColors.primary → { viewModel.signOut(); onSignOut() }

ErrorBanner (bottom of screen, AnimatedVisibility when error non-null)
```

### Auto-poll behaviour
```kotlin
// Inside EmailVerificationScreen composable:
LaunchedEffect(Unit) {
    repeat(10) {
        delay(5_000)
        viewModel.checkEmailVerified()
    }
}
```

### Sub-tasks
- [ ] **M02-T15-A** Implement `EmailVerificationScreen`. Uses `AuthViewModel`.
- [ ] **M02-T15-B** Retrieve current user email from `FirebaseAuth.getInstance().currentUser?.email` inside the composable (via `remember { FirebaseAuth.getInstance().currentUser?.email ?: "" }`).
- [ ] **M02-T15-C** On `AuthUiState.Success(ONBOARDING)` → `onVerified()`.
- [ ] **M02-T15-D** Resend throttle: managed locally in the composable with a `LaunchedEffect`:

```kotlin
var resendEnabled by remember { mutableStateOf(true) }
var resendCountdown by remember { mutableIntStateOf(0) }

fun startResendThrottle() {
    resendEnabled = false
    // Launch coroutine counting down from 60
}
```
- [ ] **M02-T15-E** `@Preview` dark + light.

---

## Task M02-T16 — Screen: `BlockedScreen`

**File:** `presentation/auth/BlockedScreen.kt`
**Route:** `auth/blocked`

### Exact Specification
```
Background: MeshBackground
Content: Column(fillMaxSize, CenterVertically, CenterHorizontally, padding=24dp)

Illustration block:
  80dp circle, RentoColors.redTint fill, 1.5dp RentoColors.red border
  RentoIcons.Shield, 36dp, RentoColors.red

Spacer(24dp)

"Account Blocked" — Fraunces 26sp SemiBold, RentoColors.t0, centred

Spacer(12dp)

"Your account has been suspended. For assistance contact:" — 14sp, RentoColors.t2, centred

Spacer(6dp)

"{adminEmail}" — 14sp, Bold, RentoColors.primary, centred
(adminEmail loaded from Firestore config/app on screen entry — show placeholder "support@rentopk.com" while loading)

Spacer(40dp)

GhostButton("Sign Out") → { viewModel.signOut(); onSignOut() }
```

### Sub-tasks
- [ ] **M02-T16-A** Implement `BlockedScreen`. Uses a lightweight `BlockedViewModel` or the existing `AuthViewModel`.
- [ ] **M02-T16-B** Load admin email via `UserRepository.getAdminEmail()` in `LaunchedEffect(Unit)`. Show loading placeholder "support@rentopk.com" until loaded.
- [ ] **M02-T16-C** `@Preview` dark + light.

---

## Task M02-T17 — `OnboardingScreen` Step 1 — Intent

**File:** `presentation/auth/OnboardingScreen.kt`
**Route:** `onboarding`

The `OnboardingScreen` is a **single composable** that hosts all 4 steps driven by `OnboardingViewModel.uiState.currentStep`. It is NOT 4 separate routes. Navigation between steps is internal (no back stack changes).

### Shared Onboarding Shell
```
Background: RentoColors.bg0 (NOT MeshBackground — clean dark for focus)
Content: Column(fillMaxSize)

StatusBar

Progress step bar:
  ProgressStepBar(currentStep = uiState.currentStep, totalSteps = 4)
  Padding: top=16dp, horizontal=20dp
  NOTE: step 4 (index 3) is the last — all 4 dots visible

Content area (weight=1f, verticalScroll):
  AnimatedContent(targetState = uiState.currentStep):
    step 0 → Step1Content()
    step 1 → Step2Content()
    step 2 → Step3Content()
    step 3 → Step4Content()
  TransitionSpec: slideInHorizontally (right) + fadeIn when step increases,
                  slideInHorizontally (left) + fadeIn when step decreases

Bottom button area (padding=20dp, no scroll):
  Error banner (AnimatedVisibility)
  Spacer(12dp)
  PrimaryButton(ctaText):
    step 0-2: "Continue"
    step 3: "Finish"
    isLoading: step 3 + uiState.isLoading
    onClick: if (step < 3) viewModel.nextStep() else viewModel.finish()
  If step > 0: GhostButton("Back", top=10dp) → viewModel.prevStep()
```

### Step 1 Content — Intent Selection
```
Column(padding horizontal=20dp):
  Spacer(32dp)
  "How will you use RentO?" — Fraunces 28sp SemiBold, RentoColors.t0, su animation
  Spacer(8dp)
  "Choose your primary goal — you can always switch later." — 14sp, RentoColors.t2
  Spacer(36dp)

  Two selection cards (Column, 16dp gap):
    Same card design as WelcomeScreen intent cards (DarkBg2 fill, 24dp corner, etc.)
    LOOKING card selected state:
      Border: 1.5dp RentoColors.primary (instead of border2)
      Icon box: RentoColors.primaryTint fill + RentoColors.primaryRing border (already this — highlight via border)
      Trailing: RentoIcons.Check, 20dp, RentoColors.primary replaces Chevron when selected
    HOSTING card selected state:
      Same treatment

    animateColorAsState(220ms) on border colour: border2 → primary
    Selection: single — tapping either deselects the other

  Spacer(24dp)

  SectionLabel("BOTH MODES ALWAYS AVAILABLE")
  Spacer(6dp)
  "Your choice sets your default feed. You can switch anytime from the home screen."
    12sp, RentoColors.t2
```

### Sub-tasks
- [ ] **M02-T17-A** Implement `OnboardingScreen` shell with `AnimatedContent` step switcher.
- [ ] **M02-T17-B** Implement Step 1 content. Selection state from `uiState.defaultMode`.
- [ ] **M02-T17-C** Border animation: `animateColorAsState` on card border colour when selection changes.
- [ ] **M02-T17-D** `@Preview` step 0 selected Looking, step 0 selected Hosting, step 0 unselected.

---

## Task M02-T18 — `OnboardingScreen` Step 2 — Personal Info

### Exact Specification — Step 2 Content
```
Column(padding horizontal=20dp):
  Spacer(24dp)
  "Tell us about yourself" — Fraunces 28sp SemiBold, su
  "This helps hosts and seekers know who they're dealing with." — 14sp, RentoColors.t2, top=8dp
  Spacer(28dp)

  SectionLabel("FULL NAME")
  Spacer(6dp)
  UnderlineInputField:
    value = uiState.name
    placeholder = "Your full name"
    maxLength = 60
    keyboardType = Text, imeAction = Next

  Spacer(20dp)

  SectionLabel("PHONE NUMBER")
  Spacer(6dp)
  UnderlineInputField:
    value = uiState.phone
    placeholder = "+92 3XX XXXXXXX"
    keyboardType = Phone
    maxLength = 15
    imeAction = Next

  Spacer(20dp)

  SectionLabel("DATE OF BIRTH")
  Spacer(6dp)
  "Date of birth (optional)" row (tappable, opens DatePickerDialog):
    Row(CenterVertically, 12dp gap):
      RentoIcons.Clock, 20dp, RentoColors.t2
      Text: if dateOfBirth==null "Select date (optional)" 14sp RentoColors.t3
            else formattedDate 14sp RentoColors.t0
    Underline drawn via Modifier.drawBehind (same style as UnderlineInputField idle)
    DatePickerDialog (Material DatePicker, Material3) — open on tap
      Confirm sets formatted string "DD MMM YYYY" e.g. "14 Mar 1995"

  Spacer(24dp)

  SectionLabel("PROVINCE")
  Spacer(6dp)
  Province picker (tappable row → ModalBottomSheet with province list):
    Row(CenterVertically, 12dp gap):
      RentoIcons.Pin, 20dp, RentoColors.t2
      Text: if province.isBlank() "Select province" 14sp RentoColors.t3
            else province 14sp RentoColors.t0
      Spacer(weight=1f)
      RentoIcons.Chevron, 18dp, RentoColors.t3
    Underline

  Spacer(16dp)

  SectionLabel("CITY")
  Spacer(6dp)
  City picker (same tappable row → ModalBottomSheet with city chips filtered by province):
    enabled only when province is selected
    Row same as province row

  Spacer(16dp)

  "Use current location" link:
    Row(8dp gap, CenterVertically):
      RentoIcons.Pin, 16dp, RentoColors.primary
      "Use current location" — 13sp, RentoColors.primary, Bold
    onClick: request ACCESS_FINE_LOCATION permission → if granted: reverse geocode via
             Geocoder to fill province + city + save lat/lng to viewModel
```

### Province & City Data
All hardcoded in `res/values/strings.xml` arrays. Provinces and cities are static data — no network call.

```xml
<!-- Province list — 4 provinces + territories (Pakistan) -->
<string-array name="provinces">
    <item>Sindh</item>
    <item>Punjab</item>
    <item>Khyber Pakhtunkhwa</item>
    <item>Balochistan</item>
    <item>Islamabad Capital Territory</item>
    <item>Azad Kashmir</item>
    <item>Gilgit-Baltistan</item>
</string-array>

<!-- Cities per province — accessed as provinceNameCities -->
<!-- Sindh Cities -->
<string-array name="cities_Sindh">
    <item>Karachi</item><item>Hyderabad</item><item>Sukkur</item>
    <item>Larkana</item><item>Mirpur Khas</item><item>Nawabshah</item>
    <item>Thatta</item><item>Jacobabad</item>
</string-array>

<!-- Punjab Cities -->
<string-array name="cities_Punjab">
    <item>Lahore</item><item>Faisalabad</item><item>Rawalpindi</item>
    <item>Gujranwala</item><item>Sialkot</item><item>Multan</item>
    <item>Bahawalpur</item><item>Sargodha</item><item>Sheikhupura</item>
    <item>Rahim Yar Khan</item>
</string-array>

<!-- KPK Cities -->
<string-array name="cities_Khyber Pakhtunkhwa">
    <item>Peshawar</item><item>Abbottabad</item><item>Mardan</item>
    <item>Mingora</item><item>Kohat</item><item>Dera Ismail Khan</item>
</string-array>

<!-- Balochistan Cities -->
<string-array name="cities_Balochistan">
    <item>Quetta</item><item>Turbat</item><item>Khuzdar</item><item>Hub</item>
</string-array>

<!-- ICT Cities -->
<string-array name="cities_Islamabad Capital Territory">
    <item>Islamabad</item>
</string-array>

<!-- AJK Cities -->
<string-array name="cities_Azad Kashmir">
    <item>Muzaffarabad</item><item>Mirpur</item><item>Rawalakot</item>
</string-array>

<!-- GB Cities -->
<string-array name="cities_Gilgit-Baltistan">
    <item>Gilgit</item><item>Skardu</item><item>Chilas</item>
</string-array>
```

City lookup:
```kotlin
fun getCitiesForProvince(context: Context, province: String): List<String> {
    val arrayName = "cities_${province}"
    val resId = context.resources.getIdentifier(arrayName, "array", context.packageName)
    return if (resId != 0) context.resources.getStringArray(resId).toList() else emptyList()
}
```

### Sub-tasks
- [ ] **M02-T18-A** Implement Step 2 content.
- [ ] **M02-T18-B** Province picker: `ModalBottomSheet` with `LazyColumn` of province names. Each province: tappable `Row` with name text + `RentoIcons.Check` (visible when selected). On select: `viewModel.updateProvince(province)`, dismiss sheet.
- [ ] **M02-T18-C** City picker: same `ModalBottomSheet` with `LazyColumn` of cities filtered to selected province. Disabled (greyed out) if province empty.
- [ ] **M02-T18-D** Location permission: `rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission())`. On granted: use `LocationServices.getFusedLocationProviderClient(context).lastLocation.await()`. Reverse geocode with `Geocoder` (first address). Extract `adminArea` → province, `locality` → city.
- [ ] **M02-T18-E** `@Preview` step 2 empty, step 2 filled.

---

## Task M02-T19 — `OnboardingScreen` Step 3 — Account Type

### Exact Specification — Step 3 Content
```
Column(padding horizontal=20dp):
  Spacer(32dp)
  "Account Type" — Fraunces 28sp SemiBold
  "This helps tailor your experience." — 14sp, RentoColors.t2, top=8dp
  Spacer(36dp)

  Two selection cards (Column, 16dp gap) — same selection card style as Step 1:

  INDIVIDUAL card (onClick → viewModel.selectAccountType(INDIVIDUAL)):
    Icon: RentoIcons.User, 26dp, RentoColors.primary
    Title: "Individual" — 16sp, Bold, RentoColors.t0
    Subtitle: "I'm a private individual looking or listing my own space." — 13sp, RentoColors.t2

  BUSINESS card (onClick → viewModel.selectAccountType(BUSINESS)):
    Icon: RentoIcons.Building, 26dp, RentoColors.primary
    Title: "Business / Agency" — 16sp, Bold, RentoColors.t0
    Subtitle: "I represent a business, agency, or manage multiple properties." — 13sp, RentoColors.t2

  Same selection behaviour as Step 1:
    Trailing Check icon when selected
    Border colour animates: border2 → primary
```

### Sub-tasks
- [ ] **M02-T19-A** Implement Step 3 content. Reuse the same selection card composable extracted in Step 1 (`OnboardingSelectionCard`) — do NOT create a duplicate card composable.
- [ ] **M02-T19-B** `OnboardingSelectionCard` is a private composable within `OnboardingScreen.kt` — it is not a shared component (only used here).
- [ ] **M02-T19-C** `@Preview` step 3 unselected, Individual selected, Business selected.

---

## Task M02-T20 — `OnboardingScreen` Step 4 — Referral

### Exact Specification — Step 4 Content
```
Column(padding horizontal=20dp):
  Spacer(32dp)
  "How did you find us?" — Fraunces 28sp SemiBold
  "Optional — helps us improve how we reach more people." — 14sp, RentoColors.t2, top=8dp
  Spacer(32dp)

  SectionLabel("SELECT ALL THAT APPLY")
  Spacer(12dp)

  FlowRow (wrapping chip grid, gap=10dp H, gap=10dp V):
    6 RentoChips (multi-select — unlike Steps 1 and 3, any combination is valid):
      "Social Media"
      "Friend / Word of Mouth"
      "Google Search"
      "TV / Radio"
      "App Store"
      "Other"
    Each chip: selected = source in uiState.referralSources
    onClick = viewModel.toggleReferral(source)

  Spacer(16dp)

  SectionLabel("(OPTIONAL — SKIP TO FINISH)")
```

> Note: "Finish" button on Step 4 is always enabled (empty referralSources is valid). The step is truly optional.

### `finish()` sequence (in `OnboardingViewModel`):
1. Validate name is non-blank, province and city non-blank (all required since Step 2 validated them — but double-check as defensive guard).
2. Call `saveOnboarding.invoke(uid, onboardingData)`.
3. On success: `currentStep = 4` (signals completion to screen).
4. Screen: `LaunchedEffect(uiState.currentStep) { if (currentStep == 4) onComplete() }`.

### Sub-tasks
- [ ] **M02-T20-A** Implement Step 4 content.
- [ ] **M02-T20-B** Use `FlowRow` from `androidx.compose.foundation.layout.FlowRow` (available in Compose 1.5+). Confirm it's available via the BOM version set in Module 01 (`compose-bom:2024.10.00`).
- [ ] **M02-T20-C** Completion trigger:

```kotlin
// In OnboardingScreen composable:
LaunchedEffect(uiState.currentStep) {
    if (uiState.currentStep == 4) onComplete()
}
```
- [ ] **M02-T20-D** `@Preview` step 4 empty, step 4 with 3 sources selected.

---

## Task M02-T21 — Shared UI: `GoogleSignInButton` + `AuthDivider`

**File:** `presentation/auth/components/GoogleSignInButton.kt`
**File:** `presentation/auth/components/AuthDivider.kt`

### `GoogleSignInButton` Exact Specification
```
Row(CenterVertically, fillMaxWidth):
  Background: RentoColors.bg2
  Border:     1.5dp, RentoColors.border
  Shape:      RoundedCornerShape(100dp)
  Padding:    15dp V / 20dp H

  Google "G" logo (must be actual Google branding — SVG / drawable):
    22dp × 22dp
    Full colour Google "G" — NOT an icon from RentoIcons (Google branding rule)
    Use: ic_google_logo.xml (vector drawable added to res/drawable/)
    The SVG paths for the Google "G" in full colour:
      Red:   path M12 10.2V14H16.45C16.22 15.38 15.07 17.85 12 17.85C9.36 17.85 7.2 15.66 7.2 13S9.36 8.15 12 8.15C13.5 8.15 14.52 8.8 15.11 9.36L18.11 6.36C16.23 4.55 13.74 3.5 12 3.5C7.53 3.5 3.9 7.13 3.9 11.5S7.53 19.5 12 19.5C17.1 19.5 20.2 15.92 20.2 11.5C20.2 10.94 20.14 10.6 20.04 10.2H12Z
      (Use a standard Google "G" drawable — file provided as res/drawable/ic_google.xml with correct multi-colour paths)

  Spacer(12dp)

  "Continue with Google" — 15sp, Medium, RentoColors.t0

  Spacer(weight=1f)

Pressed: scale(0.985), spring
```

> ⚠️ The Google "G" logo must follow Google branding guidelines — do not alter its colours or shape. Use the standard coloured Google logo drawable.

### `ic_google.xml` (add to `res/drawable/`):
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="22dp" android:height="22dp"
    android:viewportWidth="48" android:viewportHeight="48">
    <path android:fillColor="#EA4335"
        android:pathData="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"/>
    <path android:fillColor="#4285F4"
        android:pathData="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"/>
    <path android:fillColor="#FBBC05"
        android:pathData="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"/>
    <path android:fillColor="#34A853"
        android:pathData="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"/>
</vector>
```

### `AuthDivider` Exact Specification
```
Row(CenterVertically, fillMaxWidth):
  Divider: HorizontalDivider, weight=1f, 1dp, RentoColors.border2
  Spacer(12dp)
  "or" — 12sp, RentoColors.t2
  Spacer(12dp)
  Divider: HorizontalDivider, weight=1f, 1dp, RentoColors.border2
```

### Sub-tasks
- [ ] **M02-T21-A** Add `ic_google.xml` vector to `res/drawable/`.
- [ ] **M02-T21-B** Implement `GoogleSignInButton`.
- [ ] **M02-T21-C** Implement `AuthDivider`.
- [ ] **M02-T21-D** `@Preview` both components dark + light.

---

## Task M02-T22 — Shared UI: `AuthHeader` + `AuthBackButton`

**File:** `presentation/auth/components/AuthHeader.kt`
**File:** `presentation/auth/components/AuthBackButton.kt`

### `AuthBackButton` Exact Specification
```
Box:
  42dp × 42dp
  Background: RentoColors.bg2
  Border:     1.5dp, RentoColors.border2
  Shape:      RoundedCornerShape(15dp)

  Icon: RentoIcons.Back, 20dp, RentoColors.t0
  Content description: "Go back"

Press: scale(0.95), 200ms
```

### `AuthHeader` Exact Specification

Convenience composable for the title + subtitle block used on Login, Register, ForgotPassword, EmailVerification screens:

```kotlin
@Composable
fun AuthHeader(
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
)
```

```
Column(modifier):
  Text(title):
    Style: RentoDisplayL (Fraunces 32sp SemiBold)
    Color: RentoColors.t0
  if subtitle != null:
    Spacer(8dp)
    Text(subtitle):
      14sp, RentoColors.t2
```

> Note: `su` animation (slide-up fade-in) is applied by the **caller** using `fadeInSlideUpModifier`, not baked into `AuthHeader`.

### Sub-tasks
- [ ] **M02-T22-A** Implement `AuthBackButton` and `AuthHeader`.
- [ ] **M02-T22-B** `@Preview` both components dark + light.

---

## Task M02-T23 — FCM Token Registration

**File:** `RentoFCMService.kt`

This task covers only the **token-save** portion of the FCM service. Full notification handling (foreground banner, routing) is Module 13's responsibility.

### Sub-tasks
- [ ] **M02-T23-A** Create `RentoFCMService : FirebaseMessagingService`:

```kotlin
class RentoFCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update("fcmToken", token)
            .addOnFailureListener { e ->
                // Log non-fatal to Crashlytics
                FirebaseCrashlytics.getInstance().recordException(e)
            }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // STUB NOTE: Full handling implemented in Module 13.
        // This is NOT a TODO — it is a known deferred implementation.
        // This method body intentionally empty until Module 13.
    }
}
```

- [ ] **M02-T23-B** Register service in `AndroidManifest.xml`:

```xml
<service
    android:name=".RentoFCMService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

- [ ] **M02-T23-C** Create FCM notification channel on app startup (required API ≥ 26):

```kotlin
// In RentoApplication.onCreate():
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val channel = NotificationChannel(
        "rento_default",
        "RentO Notifications",
        NotificationManager.IMPORTANCE_HIGH,
    ).apply {
        description = "Property listings, chat messages, and account alerts."
        enableLights(true)
        lightColor = 0xFF2ECC8A.toInt()
    }
    getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
}
```

---

## Task M02-T24 — String Resources

All strings appended to `res/values/strings.xml`. Naming convention: `{screen}_{element}_{description}`.

```xml
<!-- ─── Auth — Common ──────────────────────────────────────────────────── -->
<string name="auth_email_placeholder">Email address</string>
<string name="auth_password_placeholder">Password</string>
<string name="auth_confirm_password_placeholder">Confirm Password</string>
<string name="auth_full_name_placeholder">Your full name</string>
<string name="auth_back_cd">Go back</string>
<string name="auth_or_divider">or</string>
<string name="auth_google_button">Continue with Google</string>
<string name="google_web_client_id">YOUR_WEB_CLIENT_ID_HERE</string>
<!-- ⚠️ Replace google_web_client_id with actual value from google-services.json
         web_client_id (not the Android client_id) before build. -->

<!-- ─── Splash ──────────────────────────────────────────────────────────── -->
<string name="splash_app_name">RentO</string>

<!-- ─── Welcome ─────────────────────────────────────────────────────────── -->
<string name="welcome_hero_line1">Your Space</string>
<string name="welcome_hero_line2">Awaits You</string>
<string name="welcome_subtitle">Rooms, homes &amp; shared spaces that truly feel like home — across Pakistan.</string>
<string name="welcome_looking_title">I\'m Looking</string>
<string name="welcome_looking_subtitle">Find accommodation that fits your life</string>
<string name="welcome_hosting_title">I\'m Hosting</string>
<string name="welcome_hosting_subtitle">List your space and find the right tenant</string>
<string name="welcome_have_account">Have an account?</string>
<string name="welcome_sign_in_link">Sign In</string>
<string name="welcome_terms">Terms of Service</string>
<string name="welcome_privacy">Privacy Policy</string>
<string name="welcome_terms_separator">·</string>

<!-- ─── Login ───────────────────────────────────────────────────────────── -->
<string name="login_title">Welcome Back</string>
<string name="login_subtitle">Sign in to continue to your account.</string>
<string name="login_forgot_password">Forgot Password?</string>
<string name="login_button">Sign In</string>
<string name="login_no_account">Don\'t have an account?</string>
<string name="login_sign_up_link">Sign Up</string>

<!-- ─── Register ─────────────────────────────────────────────────────────── -->
<string name="register_title">Create Account</string>
<string name="register_subtitle">Join thousands finding their perfect space.</string>
<string name="register_button">Create Account</string>
<string name="register_have_account">Already have an account?</string>
<string name="register_sign_in_link">Sign In</string>
<string name="register_terms_prefix">By creating an account you agree to our </string>
<string name="register_terms_link">Terms of Service</string>
<string name="register_terms_and"> and </string>
<string name="register_privacy_link">Privacy Policy</string>
<string name="register_req_chars">8+ chars</string>
<string name="register_req_uppercase">Uppercase</string>
<string name="register_req_number">Number</string>

<!-- ─── Forgot Password ──────────────────────────────────────────────────── -->
<string name="forgot_title">Forgot Password?</string>
<string name="forgot_subtitle">Enter your email and we\'ll send a reset link.</string>
<string name="forgot_button">Send Reset Link</string>
<string name="forgot_success_title">Check your inbox</string>
<string name="forgot_success_body">We\'ve sent a link to %1$s. Check spam too.</string>
<string name="forgot_resend">Resend Email</string>
<string name="forgot_resend_throttled">Resend in %1$ds</string>
<string name="forgot_back_to_signin">Back to Sign In</string>

<!-- ─── Email Verification ───────────────────────────────────────────────── -->
<string name="verify_title">Verify your email</string>
<string name="verify_subtitle">We\'ve sent a verification email. Open it and tap the link to continue.</string>
<string name="verify_button">I\'ve Verified</string>
<string name="verify_resend">Resend Email</string>
<string name="verify_resend_throttled">Resend in %1$ds</string>
<string name="verify_wrong_account">Wrong account?</string>
<string name="verify_sign_out">Sign Out</string>

<!-- ─── Blocked ───────────────────────────────────────────────────────────── -->
<string name="blocked_title">Account Blocked</string>
<string name="blocked_subtitle">Your account has been suspended. For assistance contact:</string>
<string name="blocked_admin_fallback">support@rentopk.com</string>
<string name="blocked_sign_out">Sign Out</string>

<!-- ─── Onboarding — Step 1 ──────────────────────────────────────────────── -->
<string name="onboarding_step1_title">How will you use RentO?</string>
<string name="onboarding_step1_subtitle">Choose your primary goal — you can always switch later.</string>
<string name="onboarding_step1_looking_title">I\'m Looking</string>
<string name="onboarding_step1_looking_subtitle">Find accommodation that fits your life</string>
<string name="onboarding_step1_hosting_title">I\'m Hosting</string>
<string name="onboarding_step1_hosting_subtitle">List your space and find the right tenant</string>
<string name="onboarding_step1_note_label">BOTH MODES ALWAYS AVAILABLE</string>
<string name="onboarding_step1_note">Your choice sets your default feed. You can switch anytime from the home screen.</string>

<!-- ─── Onboarding — Step 2 ──────────────────────────────────────────────── -->
<string name="onboarding_step2_title">Tell us about yourself</string>
<string name="onboarding_step2_subtitle">This helps hosts and seekers know who they\'re dealing with.</string>
<string name="onboarding_step2_name_label">FULL NAME</string>
<string name="onboarding_step2_phone_label">PHONE NUMBER</string>
<string name="onboarding_step2_phone_placeholder">+92 3XX XXXXXXX</string>
<string name="onboarding_step2_dob_label">DATE OF BIRTH</string>
<string name="onboarding_step2_dob_placeholder">Select date (optional)</string>
<string name="onboarding_step2_province_label">PROVINCE</string>
<string name="onboarding_step2_province_placeholder">Select province</string>
<string name="onboarding_step2_city_label">CITY</string>
<string name="onboarding_step2_city_placeholder">Select city</string>
<string name="onboarding_step2_location_link">Use current location</string>

<!-- ─── Onboarding — Step 3 ──────────────────────────────────────────────── -->
<string name="onboarding_step3_title">Account Type</string>
<string name="onboarding_step3_subtitle">This helps tailor your experience.</string>
<string name="onboarding_step3_individual_title">Individual</string>
<string name="onboarding_step3_individual_subtitle">I\'m a private individual looking or listing my own space.</string>
<string name="onboarding_step3_business_title">Business / Agency</string>
<string name="onboarding_step3_business_subtitle">I represent a business, agency, or manage multiple properties.</string>

<!-- ─── Onboarding — Step 4 ──────────────────────────────────────────────── -->
<string name="onboarding_step4_title">How did you find us?</string>
<string name="onboarding_step4_subtitle">Optional — helps us improve how we reach more people.</string>
<string name="onboarding_step4_label">SELECT ALL THAT APPLY</string>
<string name="onboarding_step4_skip_note">(OPTIONAL — SKIP TO FINISH)</string>
<string name="onboarding_step4_social">Social Media</string>
<string name="onboarding_step4_friend">Friend / Word of Mouth</string>
<string name="onboarding_step4_google">Google Search</string>
<string name="onboarding_step4_tv">TV / Radio</string>
<string name="onboarding_step4_appstore">App Store</string>
<string name="onboarding_step4_other">Other</string>

<!-- ─── Onboarding — Navigation Buttons ──────────────────────────────────── -->
<string name="onboarding_continue">Continue</string>
<string name="onboarding_back">Back</string>
<string name="onboarding_finish">Finish</string>

<!-- ─── Auth Errors ───────────────────────────────────────────────────────── -->
<string name="auth_error_invalid_email">Invalid email address.</string>
<string name="auth_error_wrong_password">Incorrect password.</string>
<string name="auth_error_user_not_found">No account found with this email.</string>
<string name="auth_error_email_in_use">An account already exists with this email.</string>
<string name="auth_error_weak_password">Password must be at least 8 characters.</string>
<string name="auth_error_too_many_requests">Too many attempts. Please wait and try again.</string>
<string name="auth_error_network">Network error. Please check your connection.</string>
<string name="auth_error_google_play">Google Play Services unavailable.</string>
<string name="auth_error_generic">An unexpected error occurred. Please try again.</string>
<string name="auth_error_name_required">Full name is required.</string>
<string name="auth_error_passwords_mismatch">Passwords do not match.</string>
<string name="auth_error_province_required">Please select your province.</string>
<string name="auth_error_city_required">Please select your city.</string>
<string name="auth_error_mode_required">Please choose how you\'ll use RentO.</string>
<string name="auth_error_account_type_required">Please select your account type.</string>
<string name="auth_error_email_required">Email is required.</string>
<string name="auth_error_password_required">Password is required.</string>

<!-- ─── Notification Channel ────────────────────────────────────────────── -->
<string name="notif_channel_name">RentO Notifications</string>
<string name="notif_channel_description">Property listings, chat messages, and account alerts.</string>
```

---

## Task M02-T25 — Unit Tests

### `AuthViewModelTest.kt`
```
loginWithEmail_success_emitsSuccessWithCorrectDestination
loginWithEmail_invalidEmail_emitsError
loginWithEmail_wrongPassword_emitsError
loginWithEmail_networkError_emitsError
loginWithEmail_loading_emitsLoadingState
loginWithGoogle_newUser_emitsOnboardingDestination
loginWithGoogle_returningUser_emitsHomeDestination
loginWithGoogle_blockedUser_emitsBlockedDestination
register_success_emitsVerifyEmailDestination
register_passwordMismatch_emitsError
register_weakPassword_emitsError
register_emailAlreadyInUse_emitsError
checkEmailVerified_verified_emitsOnboarding
checkEmailVerified_notVerified_emitsIdle
resendVerification_success_emitsIdle
signOut_clearsState
```

### `ForgotPasswordViewModelTest.kt`
```
sendReset_success_emitsSuccess
sendReset_emptyEmail_emitsError
sendReset_networkError_emitsError
sendReset_startsThrottleCountdown
resend_durringThrottle_doesNothing
resend_afterThrottle_sendsAgain
throttle_countsDownFrom60To0
```

### `OnboardingViewModelTest.kt`
```
nextStep_step0_modeNotSelected_emitsValidationError
nextStep_step0_modeSelected_advancesToStep1
nextStep_step1_missingName_emitsValidationError
nextStep_step1_missingProvince_emitsValidationError
nextStep_step1_allFilled_advancesToStep2
nextStep_step2_noAccountType_emitsValidationError
nextStep_step2_accountTypeSelected_advancesToStep3
prevStep_decrementsCurrentStep
prevStep_atStep0_doesNotGoNegative
toggleReferral_addsSource
toggleReferral_removesExistingSource
finish_success_setsStep4
finish_failure_emitsError
```

### `LoginWithEmailUseCaseTest.kt`
```
invoke_validInput_callsRepository
invoke_emptyEmail_returnsInvalidEmail
invoke_invalidEmailFormat_returnsInvalidEmail
invoke_emptyPassword_returnsWrongPassword
invoke_repositoryFailure_propagatesError
invoke_blockedUser_returnsBlockedDestination
invoke_unverifiedEmail_returnsVerifyEmailDestination
invoke_noOnboarding_returnsOnboardingDestination
invoke_complete_returnsHomeDestination
```

### `RegisterUseCaseTest.kt`
```
invoke_validInput_callsRegisterAndCreateDoc
invoke_shortName_returnsError
invoke_invalidEmail_returnsError
invoke_shortPassword_returnsWeakPassword
invoke_passwordMismatch_returnsError
invoke_emailAlreadyInUse_propagatesError
```

### `SendPasswordResetUseCaseTest.kt`
```
invoke_validEmail_callsRepository
invoke_emptyEmail_returnsError
```

### Sub-tasks
- [ ] **M02-T25-A** Create all test files with tests listed above using `MockK`, `Turbine`, and `kotlinx-coroutines-test`.
- [ ] **M02-T25-B** All repository calls mocked — zero real Firebase calls in unit tests.
- [ ] **M02-T25-C** Use `StandardTestDispatcher` for all ViewModel tests. Set `Dispatchers.Main` via `@BeforeEach` with `Dispatchers.setMain(testDispatcher)`.
- [ ] **M02-T25-D** Run `./gradlew test` — all tests pass. Paste output.
- [ ] **M02-T25-E** Run `./gradlew koverReport` — coverage ≥ 80% on `com.rento.app.presentation.auth` and `com.rento.app.domain.usecase`. Paste summary.

---

## Task M02-T26 — Build Gate

- [ ] **M02-T26-A** Run `./gradlew lint` — zero new warnings. Document any suppressed warnings with reason.
- [ ] **M02-T26-B** Run `./gradlew detekt` — zero code smell violations.
- [ ] **M02-T26-C** Run `./gradlew assembleDebug` → `BUILD SUCCESSFUL`. Paste output.
- [ ] **M02-T26-D** Run `./gradlew test` → all tests pass. Paste: `X tests completed, 0 failed`.
- [ ] **M02-T26-E** Run `./gradlew koverReport` → paste coverage ≥ 80%.
- [ ] **M02-T26-F** Update `ANDROID_PROGRESS.md` with all task statuses and evidence.
- [ ] **M02-T26-G** Create `CODE_REVIEW_MODULE_02.md` using the template in Section 32 below. Fill in all checklist items honestly.

---

## 31. Journey Coverage Checklist

Before marking Module 02 complete, confirm every applicable journey is implemented:

| Journey | Location | Status |
|---------|----------|--------|
| Email/password login | `LoginScreen` → `AuthViewModel` | ☐ |
| Google Sign-In (login) | `LoginScreen` → `AuthViewModel.loginWithGoogle` | ☐ |
| Email/password registration | `RegisterScreen` → `AuthViewModel.register` | ☐ |
| Google Sign-In (register — new user) | `RegisterScreen` → `LoginWithGoogleUseCase` | ☐ |
| Email verification pending | `EmailVerificationScreen` auto-poll + manual check | ☐ |
| Email verification — resend (60s throttle) | `EmailVerificationScreen` | ☐ |
| Forgot password — send reset link | `ForgotPasswordScreen` | ☐ |
| Forgot password — success state | `ForgotPasswordScreen` check illustration | ☐ |
| Forgot password — resend (60s throttle) | `ForgotPasswordScreen` countdown display | ☐ |
| Blocked user gate | `BlockedScreen` → sign out | ☐ |
| Splash routing — already authenticated | `SplashViewModel` all 4 branches | ☐ |
| Onboarding Step 1 — intent | `OnboardingScreen` step 0 | ☐ |
| Onboarding Step 2 — personal info | `OnboardingScreen` step 1 + province/city pickers | ☐ |
| Onboarding Step 2 — use current location | Location permission + geocoder | ☐ |
| Onboarding Step 3 — account type | `OnboardingScreen` step 2 | ☐ |
| Onboarding Step 4 — referral (optional) | `OnboardingScreen` step 3 | ☐ |
| Onboarding → Home navigation | `OnboardingScreen` finish → clearBackStack | ☐ |
| FCM token saved on login | `AuthRepositoryImpl.saveFcmToken` | ☐ |
| FCM token refresh | `RentoFCMService.onNewToken` | ☐ |
| All auth error states shown | `ErrorBanner` on Login, Register, ForgotPassword | ☐ |

---

## 32. CODE_REVIEW_MODULE_02.md Template

Create `CODE_REVIEW_MODULE_02.md` in the project root after Module 02 is complete.

```markdown
# Code Review — Module 02: Authentication & Onboarding
**Date:** YYYY-MM-DD
**Reviewer:** AI Agent (Automated)
**Branch:** feature/module-02-auth
**Spec version:** ANDROID_MODULE_02.md v1.0.0

---

## ✅ Architecture Compliance
- [ ] Domain layer has zero Android imports (verified with grep)
- [ ] ViewModels do not hold Activity/Context references
- [ ] Repository interfaces in domain, implementations in data
- [ ] No pass-through UseCases (except SignOutUseCase — documented exception)
- [ ] Koin modules declared for all new classes
- [ ] `AuthRepositoryImpl` and `UserRepositoryImpl` injected via interfaces, not concrete types

---

## ✅ Design Reference Verification

| Screen | Prototype Ref | BG ✓ | Radii ✓ | Font/Size/Weight ✓ | Icons ✓ | Animations ✓ | States ✓ |
|--------|---------------|------|---------|-------------------|---------|--------------|---------|
| SplashScreen | (Section 6.1) | | | | | | |
| WelcomeScreen | function Welcome() | | | | | | |
| LoginScreen | function SignIn() | | | | | | |
| RegisterScreen | function SignUp() | | | | | | |
| ForgotPasswordScreen | Section 32.4 | | | | | | |
| EmailVerificationScreen | Section 6.1 | | | | | | |
| BlockedScreen | Section 6.1 | | | | | | |
| OnboardingScreen Step 1 | Section 7.1 | | | | | | |
| OnboardingScreen Step 2 | Section 7.1 | | | | | | |
| OnboardingScreen Step 3 | Section 7.1 | | | | | | |
| OnboardingScreen Step 4 | Section 7.1 | | | | | | |

For any ✗ — describe discrepancy:
> DISCREPANCY: [Screen] — [property] — EXPECTED: [value] — ACTUAL: [value]

---

## ✅ Design System Compliance
- [ ] Zero hardcoded colour values — all from `LocalRentoColors.current.*`
- [ ] Zero hardcoded font sizes — all from `LocalRentoTypography.current.*`
- [ ] Zero hardcoded dimension constants — all from `LocalRentoDimens.current.*`
- [ ] Fraunces for display/headings, Plus Jakarta Sans for body/UI — verified
- [ ] All icons from `RentoIcons.*` — no Material Icons
- [ ] Google "G" logo uses `ic_google.xml` drawable (branding compliant)
- [ ] `MeshBackground` used on Welcome, Login, Register, ForgotPassword, Verify, Blocked
- [ ] `RentoColors.bg0` (plain dark) used on Splash, Onboarding
- [ ] `floatY` animation on Splash logo, Welcome logo, ForgotPassword illustration, Verify illustration
- [ ] `su` (fadeInSlideUp) animation on Welcome cards, Login/Register/ForgotPw headers
- [ ] `BounceEffect` on ForgotPassword success check icon
- [ ] `AnimatedContent` step transitions on OnboardingScreen
- [ ] `ErrorBanner` with `AnimatedVisibility` on Login, Register, ForgotPassword, Verify

---

## ✅ Glassmorphic Dialogs Compliance
No destructive actions in Module 02 — N/A.

---

## ✅ Journey Coverage
- [ ] All 20 journeys in Section 31 implemented and tested manually
- [ ] Shimmer: N/A for this module (no Firestore list screens)
- [ ] Empty state: N/A for this module

---

## ✅ Performance Checks
- [ ] No Firebase/network calls inside Composable bodies
- [ ] `collectAsStateWithLifecycle()` used (not `collectAsState()`) for all Flow collections
- [ ] `LaunchedEffect` used for side effects (navigation, auto-poll, throttle)
- [ ] `DisposableEffect` for auth state listener cleanup (inside `callbackFlow`)
- [ ] `remember` wrapping expensive computations
- [ ] No memory leaks — `viewModelScope` used for all coroutines

---

## ✅ Code Quality
- [ ] All strings in `strings.xml`
- [ ] Unit tests: all ViewModels — success + failure + loading paths covered
- [ ] `./gradlew test` → ✅ PASSING
- [ ] `./gradlew assembleDebug` → ✅ PASSING
- [ ] `./gradlew detekt` → 0 violations
- [ ] `./gradlew koverReport` → ≥ 80%

---

## ⚠️ Lint Findings (Non-Blocking)
| ID | File | Line | Finding | Severity |
|----|------|------|---------|----------|

---

## 📝 Notes
**DO NOT change design specifications based on lint findings.**
**DO NOT resolve design discrepancies without client approval.**
**Visual deviations from design_references.jsx are always blockers.**
```

---

*End of Module 02 — Authentication & Onboarding v1.0.0*
*Depends on: Module 01 complete. Next module: Module 03 — Home & Discovery.*
