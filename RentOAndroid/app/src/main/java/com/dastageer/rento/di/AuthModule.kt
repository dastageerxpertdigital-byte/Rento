package com.dastageer.rento.di

import com.dastageer.rento.data.repository.AuthRepositoryImpl
import com.dastageer.rento.data.repository.UserRepositoryImpl
import com.dastageer.rento.domain.repository.AuthRepository
import com.dastageer.rento.domain.repository.UserRepository
import com.dastageer.rento.domain.usecase.auth.CheckEmailVerifiedUseCase
import com.dastageer.rento.domain.usecase.auth.LoginWithEmailUseCase
import com.dastageer.rento.domain.usecase.auth.LoginWithGoogleUseCase
import com.dastageer.rento.domain.usecase.auth.RegisterUseCase
import com.dastageer.rento.domain.usecase.auth.ResendVerificationUseCase
import com.dastageer.rento.domain.usecase.auth.SendPasswordResetUseCase
import com.dastageer.rento.domain.usecase.auth.SignOutUseCase
import com.dastageer.rento.domain.usecase.user.CreateUserDocumentUseCase
import com.dastageer.rento.domain.usecase.user.GetUserUseCase
import com.dastageer.rento.domain.usecase.user.SaveOnboardingUseCase
import com.dastageer.rento.presentation.auth.AuthViewModel
import com.dastageer.rento.presentation.auth.ForgotPasswordViewModel
import com.dastageer.rento.presentation.auth.OnboardingViewModel
import com.dastageer.rento.presentation.auth.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    factory { LoginWithEmailUseCase(get(), get()) }
    factory { LoginWithGoogleUseCase(get(), get()) }
    factory { RegisterUseCase(get(), get()) }
    factory { SendPasswordResetUseCase(get()) }
    factory { ResendVerificationUseCase(get()) }
    factory { CheckEmailVerifiedUseCase(get()) }
    factory { SignOutUseCase(get()) }
    factory { CreateUserDocumentUseCase(get()) }
    factory { GetUserUseCase(get()) }
    factory { SaveOnboardingUseCase(get()) }
    viewModel { AuthViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { OnboardingViewModel(get(), get()) }
    viewModel { SplashViewModel(get(), get()) }
}
