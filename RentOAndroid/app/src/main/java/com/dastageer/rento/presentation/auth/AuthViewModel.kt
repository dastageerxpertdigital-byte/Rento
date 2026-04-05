package com.dastageer.rento.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dastageer.rento.domain.usecase.auth.AuthDestination
import com.dastageer.rento.domain.usecase.auth.CheckEmailVerifiedUseCase
import com.dastageer.rento.domain.usecase.auth.LoginWithEmailUseCase
import com.dastageer.rento.domain.usecase.auth.LoginWithGoogleUseCase
import com.dastageer.rento.domain.usecase.auth.RegisterUseCase
import com.dastageer.rento.domain.usecase.auth.ResendVerificationUseCase
import com.dastageer.rento.domain.usecase.auth.SignOutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val destination: AuthDestination) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val registerUseCase: RegisterUseCase,
    private val resendVerificationUseCase: ResendVerificationUseCase,
    private val checkEmailVerifiedUseCase: CheckEmailVerifiedUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            loginWithEmailUseCase(email, password).fold(
                onSuccess = { destination -> _uiState.value = AuthUiState.Success(destination) },
                onFailure = { e -> _uiState.value = AuthUiState.Error(e.message ?: "Unknown error.") },
            )
        }
    }

    fun loginWithGoogle(idToken: String, displayName: String?, email: String?, photoUrl: String?) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            loginWithGoogleUseCase(idToken, displayName, email, photoUrl).fold(
                onSuccess = { destination -> _uiState.value = AuthUiState.Success(destination) },
                onFailure = { e -> _uiState.value = AuthUiState.Error(e.message ?: "Unknown error.") },
            )
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            registerUseCase(name, email, password, confirmPassword).fold(
                onSuccess = { _uiState.value = AuthUiState.Success(AuthDestination.VERIFY_EMAIL) },
                onFailure = { e -> _uiState.value = AuthUiState.Error(e.message ?: "Unknown error.") },
            )
        }
    }

    fun checkEmailVerified() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            checkEmailVerifiedUseCase().fold(
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
            resendVerificationUseCase().fold(
                onSuccess = { _uiState.value = AuthUiState.Idle },
                onFailure = { e -> _uiState.value = AuthUiState.Error(e.message ?: "Unknown error.") },
            )
        }
    }

    fun signOut() {
        viewModelScope.launch { signOutUseCase() }
        _uiState.value = AuthUiState.Idle
    }

    fun resetState() { _uiState.value = AuthUiState.Idle }
}
