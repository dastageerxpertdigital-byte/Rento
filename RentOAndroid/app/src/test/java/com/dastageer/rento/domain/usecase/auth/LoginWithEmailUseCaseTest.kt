package com.dastageer.rento.domain.usecase.auth

import com.dastageer.rento.domain.model.AccountType
import com.dastageer.rento.domain.model.RentoAuthException
import com.dastageer.rento.domain.model.User
import com.dastageer.rento.domain.model.UserMode
import com.dastageer.rento.domain.repository.AuthRepository
import com.dastageer.rento.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginWithEmailUseCaseTest {

    private val authRepository: AuthRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val useCase = LoginWithEmailUseCase(authRepository, userRepository)

    private fun testUser(
        isBlocked: Boolean = false,
        onboardingComplete: Boolean = true,
    ) = User(
        uid = "uid1", name = "Test", email = "a@b.com", phone = null, photoUrl = null,
        emailVerified = true, isBlocked = isBlocked, blockReason = null,
        accountType = AccountType.INDIVIDUAL, defaultMode = UserMode.LOOKING,
        onboardingComplete = onboardingComplete, province = "Punjab", city = "Lahore",
        deviceLat = null, deviceLng = null, referralSource = emptyList(),
        currentPackageId = null, packageName = null, createdAt = 0L,
    )

    @Test
    fun `invoke valid input calls repository and returns Home`() = runTest {
        coEvery { authRepository.loginWithEmail("a@b.com", "pass1234") } returns Result.success(Unit)
        every { authRepository.getCurrentUserId() } returns "uid1"
        coEvery { userRepository.getUser("uid1") } returns Result.success(testUser())
        coEvery { authRepository.isEmailVerified() } returns Result.success(true)
        val result = useCase("a@b.com", "pass1234")
        assertTrue(result.isSuccess)
        assertEquals(AuthDestination.HOME, result.getOrNull())
    }

    @Test
    fun `invoke empty email returns InvalidEmail`() = runTest {
        val result = useCase("", "pass")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RentoAuthException.InvalidEmail)
    }

    @Test
    fun `invoke invalid email format returns InvalidEmail`() = runTest {
        val result = useCase("notanemail", "pass")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RentoAuthException.InvalidEmail)
    }

    @Test
    fun `invoke empty password returns WrongPassword`() = runTest {
        val result = useCase("a@b.com", "")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RentoAuthException.WrongPassword)
    }

    @Test
    fun `invoke blocked user returns Blocked destination`() = runTest {
        coEvery { authRepository.loginWithEmail("a@b.com", "pass1234") } returns Result.success(Unit)
        every { authRepository.getCurrentUserId() } returns "uid1"
        coEvery { userRepository.getUser("uid1") } returns Result.success(testUser(isBlocked = true))
        coEvery { authRepository.isEmailVerified() } returns Result.success(true)
        val result = useCase("a@b.com", "pass1234")
        assertEquals(AuthDestination.BLOCKED, result.getOrNull())
    }

    @Test
    fun `invoke unverified email returns VerifyEmail`() = runTest {
        coEvery { authRepository.loginWithEmail("a@b.com", "pass1234") } returns Result.success(Unit)
        every { authRepository.getCurrentUserId() } returns "uid1"
        coEvery { userRepository.getUser("uid1") } returns Result.success(testUser())
        coEvery { authRepository.isEmailVerified() } returns Result.success(false)
        val result = useCase("a@b.com", "pass1234")
        assertEquals(AuthDestination.VERIFY_EMAIL, result.getOrNull())
    }

    @Test
    fun `invoke no onboarding returns Onboarding`() = runTest {
        coEvery { authRepository.loginWithEmail("a@b.com", "pass1234") } returns Result.success(Unit)
        every { authRepository.getCurrentUserId() } returns "uid1"
        coEvery { userRepository.getUser("uid1") } returns Result.success(testUser(onboardingComplete = false))
        coEvery { authRepository.isEmailVerified() } returns Result.success(true)
        val result = useCase("a@b.com", "pass1234")
        assertEquals(AuthDestination.ONBOARDING, result.getOrNull())
    }
}
