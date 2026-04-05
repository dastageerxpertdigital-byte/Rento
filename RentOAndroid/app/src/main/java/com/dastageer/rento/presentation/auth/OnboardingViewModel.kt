package com.dastageer.rento.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dastageer.rento.domain.model.AccountType
import com.dastageer.rento.domain.model.OnboardingData
import com.dastageer.rento.domain.model.UserMode
import com.dastageer.rento.domain.repository.AuthRepository
import com.dastageer.rento.domain.usecase.user.SaveOnboardingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val currentStep: Int = 0,
    val defaultMode: UserMode? = null,
    val name: String = "",
    val phone: String = "",
    val dateOfBirth: String? = null,
    val province: String = "",
    val city: String = "",
    val deviceLat: Double? = null,
    val deviceLng: Double? = null,
    val accountType: AccountType? = null,
    val referralSources: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

class OnboardingViewModel(
    private val saveOnboardingUseCase: SaveOnboardingUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun selectMode(mode: UserMode) { _uiState.update { it.copy(defaultMode = mode) } }
    fun updateName(name: String) { _uiState.update { it.copy(name = name) } }
    fun updatePhone(phone: String) { _uiState.update { it.copy(phone = phone) } }
    fun updateDob(dob: String?) { _uiState.update { it.copy(dateOfBirth = dob) } }
    fun updateProvince(p: String) { _uiState.update { it.copy(province = p, city = "") } }
    fun updateCity(c: String) { _uiState.update { it.copy(city = c) } }
    fun updateLocation(lat: Double, lng: Double) {
        _uiState.update { it.copy(deviceLat = lat, deviceLng = lng) }
    }

    fun selectAccountType(type: AccountType) { _uiState.update { it.copy(accountType = type) } }

    fun toggleReferral(source: String) {
        _uiState.update {
            val updated = if (source in it.referralSources) it.referralSources - source else it.referralSources + source
            it.copy(referralSources = updated)
        }
    }

    fun nextStep() {
        val s = _uiState.value
        val validationError = when (s.currentStep) {
            0 -> if (s.defaultMode == null) "Please choose how you'll use RentO." else null
            1 -> when {
                s.name.isBlank() -> "Full name is required."
                s.province.isBlank() -> "Please select your province."
                s.city.isBlank() -> "Please select your city."
                else -> null
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
                defaultMode = s.defaultMode ?: UserMode.LOOKING,
                name = s.name.trim(),
                phone = s.phone.trim(),
                dateOfBirth = s.dateOfBirth,
                province = s.province,
                city = s.city,
                deviceLat = s.deviceLat,
                deviceLng = s.deviceLng,
                accountType = s.accountType ?: AccountType.INDIVIDUAL,
                referralSources = s.referralSources.toList(),
            )
            saveOnboardingUseCase(uid, data).fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, currentStep = 4) } },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } },
            )
        }
    }
}
