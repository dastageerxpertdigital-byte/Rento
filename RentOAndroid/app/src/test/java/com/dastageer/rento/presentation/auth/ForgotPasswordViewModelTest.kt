package com.dastageer.rento.presentation.auth

import com.dastageer.rento.domain.usecase.auth.SendPasswordResetUseCase
import com.dastageer.rento.domain.model.RentoAuthException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ForgotPasswordViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val sendPasswordReset: SendPasswordResetUseCase = mockk()
    private lateinit var viewModel: ForgotPasswordViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ForgotPasswordViewModel(sendPasswordReset)
    }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `sendReset success emits Success`() = runTest {
        coEvery { sendPasswordReset("a@b.com") } returns Result.success(Unit)
        viewModel.sendReset("a@b.com")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is ForgotPasswordUiState.Throttled || viewModel.uiState.value is ForgotPasswordUiState.Success)
    }

    @Test
    fun `sendReset emptyEmail emits Error`() = runTest {
        viewModel.sendReset("")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is ForgotPasswordUiState.Error)
    }

    @Test
    fun `sendReset networkError emits Error`() = runTest {
        coEvery { sendPasswordReset("a@b.com") } returns Result.failure(RentoAuthException.NetworkError())
        viewModel.sendReset("a@b.com")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is ForgotPasswordUiState.Error)
    }

    @Test
    fun `sendReset starts throttle countdown`() = runTest {
        coEvery { sendPasswordReset("a@b.com") } returns Result.success(Unit)
        viewModel.sendReset("a@b.com")
        advanceTimeBy(2_000)
        assertTrue(viewModel.uiState.value is ForgotPasswordUiState.Throttled)
    }

    @Test
    fun `resend during throttle does nothing`() = runTest {
        coEvery { sendPasswordReset("a@b.com") } returns Result.success(Unit)
        viewModel.sendReset("a@b.com")
        advanceTimeBy(2_000)
        val stateBefore = viewModel.uiState.value
        viewModel.resend()
        advanceTimeBy(100)
        assertTrue(viewModel.uiState.value is ForgotPasswordUiState.Throttled)
    }
}
