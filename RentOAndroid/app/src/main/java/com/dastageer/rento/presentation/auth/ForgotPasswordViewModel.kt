package com.dastageer.rento.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dastageer.rento.domain.usecase.auth.SendPasswordResetUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ForgotPasswordUiState {
    object Idle : ForgotPasswordUiState()
    object Loading : ForgotPasswordUiState()
    data class Success(val sentToEmail: String) : ForgotPasswordUiState()
    data class Error(val message: String) : ForgotPasswordUiState()
    data class Throttled(val secondsRemaining: Int) : ForgotPasswordUiState()
}

class ForgotPasswordViewModel(
    private val sendPasswordResetUseCase: SendPasswordResetUseCase,
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
            sendPasswordResetUseCase(email).fold(
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
            for (remaining in 59 downTo 1) {
                _uiState.value = ForgotPasswordUiState.Throttled(remaining)
                delay(1_000)
            }
            _uiState.value = ForgotPasswordUiState.Success(lastSentEmail)
        }
    }

    override fun onCleared() {
        super.onCleared()
        throttleJob?.cancel()
    }
}
