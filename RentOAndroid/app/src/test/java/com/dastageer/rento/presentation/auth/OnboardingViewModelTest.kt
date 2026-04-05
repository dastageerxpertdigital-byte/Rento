package com.dastageer.rento.presentation.auth

import com.dastageer.rento.domain.model.AccountType
import com.dastageer.rento.domain.model.UserMode
import com.dastageer.rento.domain.repository.AuthRepository
import com.dastageer.rento.domain.usecase.user.SaveOnboardingUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val saveOnboarding: SaveOnboardingUseCase = mockk()
    private val authRepository: AuthRepository = mockk()
    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OnboardingViewModel(saveOnboarding, authRepository)
    }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `nextStep step0 mode not selected emits validation error`() {
        viewModel.nextStep()
        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun `nextStep step0 mode selected advances to step 1`() {
        viewModel.selectMode(UserMode.LOOKING)
        viewModel.nextStep()
        assertEquals(1, viewModel.uiState.value.currentStep)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `nextStep step1 missing name emits validation error`() {
        viewModel.selectMode(UserMode.LOOKING)
        viewModel.nextStep()
        viewModel.nextStep()
        assertNotNull(viewModel.uiState.value.error)
        assertEquals(1, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `nextStep step1 all filled advances to step 2`() {
        viewModel.selectMode(UserMode.LOOKING)
        viewModel.nextStep()
        viewModel.updateName("John")
        viewModel.updateProvince("Punjab")
        viewModel.updateCity("Lahore")
        viewModel.nextStep()
        assertEquals(2, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `nextStep step2 no accountType emits validation error`() {
        viewModel.selectMode(UserMode.LOOKING)
        viewModel.nextStep()
        viewModel.updateName("John")
        viewModel.updateProvince("Punjab")
        viewModel.updateCity("Lahore")
        viewModel.nextStep()
        viewModel.nextStep()
        assertNotNull(viewModel.uiState.value.error)
        assertEquals(2, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `nextStep step2 accountType selected advances to step 3`() {
        viewModel.selectMode(UserMode.LOOKING)
        viewModel.nextStep()
        viewModel.updateName("John")
        viewModel.updateProvince("Punjab")
        viewModel.updateCity("Lahore")
        viewModel.nextStep()
        viewModel.selectAccountType(AccountType.INDIVIDUAL)
        viewModel.nextStep()
        assertEquals(3, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `prevStep decrements currentStep`() {
        viewModel.selectMode(UserMode.LOOKING)
        viewModel.nextStep()
        assertEquals(1, viewModel.uiState.value.currentStep)
        viewModel.prevStep()
        assertEquals(0, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `prevStep at step0 does not go negative`() {
        viewModel.prevStep()
        assertEquals(0, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `toggleReferral adds source`() {
        viewModel.toggleReferral("Social Media")
        assertTrue("Social Media" in viewModel.uiState.value.referralSources)
    }

    @Test
    fun `toggleReferral removes existing source`() {
        viewModel.toggleReferral("Social Media")
        viewModel.toggleReferral("Social Media")
        assertTrue("Social Media" !in viewModel.uiState.value.referralSources)
    }

    @Test
    fun `finish success sets step 4`() = runTest {
        every { authRepository.getCurrentUserId() } returns "uid123"
        coEvery { saveOnboarding(any(), any()) } returns Result.success(Unit)
        viewModel.selectMode(UserMode.LOOKING)
        viewModel.updateName("John")
        viewModel.updateProvince("Punjab")
        viewModel.updateCity("Lahore")
        viewModel.selectAccountType(AccountType.INDIVIDUAL)
        viewModel.finish()
        advanceUntilIdle()
        assertEquals(4, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `finish failure emits error`() = runTest {
        every { authRepository.getCurrentUserId() } returns "uid123"
        coEvery { saveOnboarding(any(), any()) } returns Result.failure(Exception("Save failed"))
        viewModel.finish()
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.error)
    }
}
