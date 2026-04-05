package com.dastageer.rento.presentation.auth

import com.dastageer.rento.domain.usecase.auth.AuthDestination
import com.dastageer.rento.domain.usecase.auth.CheckEmailVerifiedUseCase
import com.dastageer.rento.domain.usecase.auth.LoginWithEmailUseCase
import com.dastageer.rento.domain.usecase.auth.LoginWithGoogleUseCase
import com.dastageer.rento.domain.usecase.auth.RegisterUseCase
import com.dastageer.rento.domain.usecase.auth.ResendVerificationUseCase
import com.dastageer.rento.domain.usecase.auth.SignOutUseCase
import com.dastageer.rento.domain.model.RentoAuthException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val loginWithEmail: LoginWithEmailUseCase = mockk()
    private val loginWithGoogle: LoginWithGoogleUseCase = mockk()
    private val register: RegisterUseCase = mockk()
    private val resendVerification: ResendVerificationUseCase = mockk()
    private val checkEmailVerified: CheckEmailVerifiedUseCase = mockk()
    private val signOut: SignOutUseCase = mockk(relaxed = true)

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(loginWithEmail, loginWithGoogle, register, resendVerification, checkEmailVerified, signOut)
    }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `loginWithEmail success emits Success with correct destination`() = runTest {
        coEvery { loginWithEmail("a@b.com", "pass1234") } returns Result.success(AuthDestination.HOME)
        viewModel.loginWithEmail("a@b.com", "pass1234")
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Success && state.destination == AuthDestination.HOME)
    }

    @Test
    fun `loginWithEmail invalidEmail emits Error`() = runTest {
        coEvery { loginWithEmail("", "pass") } returns Result.failure(RentoAuthException.InvalidEmail("Email is required."))
        viewModel.loginWithEmail("", "pass")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is AuthUiState.Error)
    }

    @Test
    fun `loginWithEmail loading emits Loading state`() = runTest {
        coEvery { loginWithEmail(any(), any()) } coAnswers {
            delay(60_000)
            Result.success(AuthDestination.HOME)
        }
        viewModel.loginWithEmail("a@b.com", "pass")
        testDispatcher.scheduler.runCurrent()
        assertEquals(AuthUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `register success emits VerifyEmail destination`() = runTest {
        coEvery { register("Name", "a@b.com", "pass1234", "pass1234") } returns Result.success(Unit)
        viewModel.register("Name", "a@b.com", "pass1234", "pass1234")
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Success && state.destination == AuthDestination.VERIFY_EMAIL)
    }

    @Test
    fun `register passwordMismatch emits Error`() = runTest {
        coEvery { register("Name", "a@b.com", "pass1234", "diff") } returns Result.failure(RentoAuthException.Unknown("Passwords do not match."))
        viewModel.register("Name", "a@b.com", "pass1234", "diff")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is AuthUiState.Error)
    }

    @Test
    fun `checkEmailVerified verified emits Onboarding`() = runTest {
        coEvery { checkEmailVerified() } returns Result.success(true)
        viewModel.checkEmailVerified()
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Success && state.destination == AuthDestination.ONBOARDING)
    }

    @Test
    fun `checkEmailVerified notVerified emits Idle`() = runTest {
        coEvery { checkEmailVerified() } returns Result.success(false)
        viewModel.checkEmailVerified()
        advanceUntilIdle()
        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `resendVerification success emits Idle`() = runTest {
        coEvery { resendVerification() } returns Result.success(Unit)
        viewModel.resendVerification()
        advanceUntilIdle()
        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `signOut clears state`() = runTest {
        viewModel.signOut()
        advanceUntilIdle()
        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
        coVerify { signOut() }
    }

    @Test
    fun `resetState sets Idle`() {
        viewModel.resetState()
        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
    }
}
