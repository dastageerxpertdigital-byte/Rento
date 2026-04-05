package com.dastageer.rento.domain.usecase.auth

import com.dastageer.rento.domain.model.RentoAuthException
import com.dastageer.rento.domain.repository.AuthRepository
import com.dastageer.rento.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterUseCaseTest {

    private val authRepository: AuthRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val useCase = RegisterUseCase(authRepository, userRepository)

    @Test
    fun `invoke valid input calls register and createDoc`() = runTest {
        coEvery { authRepository.register("John", "a@b.com", "Pass1234") } returns Result.success(Unit)
        every { authRepository.getCurrentUserId() } returns "uid1"
        coEvery { userRepository.createUserDocument(any()) } returns Result.success(Unit)
        val result = useCase("John", "a@b.com", "Pass1234", "Pass1234")
        assertTrue(result.isSuccess)
        coVerify { userRepository.createUserDocument(any()) }
    }

    @Test
    fun `invoke short name returns error`() = runTest {
        val result = useCase("J", "a@b.com", "Pass1234", "Pass1234")
        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke invalid email returns error`() = runTest {
        val result = useCase("John", "bad", "Pass1234", "Pass1234")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RentoAuthException.InvalidEmail)
    }

    @Test
    fun `invoke short password returns WeakPassword`() = runTest {
        val result = useCase("John", "a@b.com", "short", "short")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RentoAuthException.WeakPassword)
    }

    @Test
    fun `invoke password mismatch returns error`() = runTest {
        val result = useCase("John", "a@b.com", "Pass1234", "Different1")
        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke blank name returns error`() = runTest {
        val result = useCase("", "a@b.com", "Pass1234", "Pass1234")
        assertTrue(result.isFailure)
    }
}
