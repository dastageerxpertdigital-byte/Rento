package com.dastageer.rento.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dastageer.rento.domain.repository.AuthRepository
import com.dastageer.rento.domain.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    sealed class SplashDestination {
        object Welcome : SplashDestination()
        object Home : SplashDestination()
        object Onboarding : SplashDestination()
        object VerifyEmail : SplashDestination()
        object Blocked : SplashDestination()
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
                    user.isBlocked -> SplashDestination.Blocked
                    !user.onboardingComplete -> SplashDestination.Onboarding
                    else -> SplashDestination.Home
                }
            },
            onFailure = {
                _destination.value = SplashDestination.Welcome
            },
        )
    }
}
