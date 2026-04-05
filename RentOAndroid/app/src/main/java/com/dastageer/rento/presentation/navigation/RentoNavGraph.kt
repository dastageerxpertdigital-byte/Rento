package com.dastageer.rento.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dastageer.rento.presentation.auth.BlockedScreen
import com.dastageer.rento.presentation.auth.EmailVerificationScreen
import com.dastageer.rento.presentation.auth.ForgotPasswordScreen
import com.dastageer.rento.presentation.auth.LoginScreen
import com.dastageer.rento.presentation.auth.OnboardingScreen
import com.dastageer.rento.presentation.auth.RegisterScreen
import com.dastageer.rento.presentation.auth.SplashScreen
import com.dastageer.rento.presentation.auth.WelcomeScreen
import com.dastageer.rento.presentation.shared.animations.NavTransitions
import com.dastageer.rento.presentation.shared.icons.RentoIcons
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors

object AuthRoutes {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val LOGIN = "auth/login"
    const val REGISTER = "auth/register"
    const val FORGOT_PW = "auth/forgot_password"
    const val VERIFY_EMAIL = "auth/verify_email"
    const val BLOCKED = "auth/blocked"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
}

@Composable
fun RentoNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AuthRoutes.SPLASH,
        modifier = modifier,
        enterTransition = { NavTransitions.pushEnter },
        exitTransition = { NavTransitions.pushExit },
        popEnterTransition = { NavTransitions.popEnter },
        popExitTransition = { NavTransitions.popExit },
    ) {
        composable(AuthRoutes.SPLASH) {
            SplashScreen(
                onNavigateToWelcome = { navController.navigate(AuthRoutes.WELCOME) { popUpTo(AuthRoutes.SPLASH) { inclusive = true } } },
                onNavigateToHome = { navController.navigate(AuthRoutes.HOME) { popUpTo(AuthRoutes.SPLASH) { inclusive = true } } },
                onNavigateToOnboarding = { navController.navigate(AuthRoutes.ONBOARDING) { popUpTo(AuthRoutes.SPLASH) { inclusive = true } } },
                onNavigateToVerify = { navController.navigate(AuthRoutes.VERIFY_EMAIL) { popUpTo(AuthRoutes.SPLASH) { inclusive = true } } },
                onNavigateToBlocked = { navController.navigate(AuthRoutes.BLOCKED) { popUpTo(AuthRoutes.SPLASH) { inclusive = true } } },
            )
        }
        composable(AuthRoutes.WELCOME) {
            WelcomeScreen(
                onSignIn = { navController.navigate(AuthRoutes.LOGIN) },
                onLooking = { navController.navigate(AuthRoutes.REGISTER) },
                onHosting = { navController.navigate(AuthRoutes.REGISTER) },
            )
        }
        composable(AuthRoutes.LOGIN) {
            LoginScreen(
                onNavigateToHome = { navController.navigate(AuthRoutes.HOME) { popUpTo(AuthRoutes.WELCOME) { inclusive = true } } },
                onNavigateToOnboarding = { navController.navigate(AuthRoutes.ONBOARDING) { popUpTo(AuthRoutes.WELCOME) { inclusive = true } } },
                onNavigateToRegister = { navController.navigate(AuthRoutes.REGISTER) { popUpTo(AuthRoutes.LOGIN) { inclusive = true } } },
                onNavigateToForgotPw = { navController.navigate(AuthRoutes.FORGOT_PW) },
                onNavigateToVerify = { navController.navigate(AuthRoutes.VERIFY_EMAIL) },
                onNavigateToBlocked = { navController.navigate(AuthRoutes.BLOCKED) { popUpTo(AuthRoutes.WELCOME) { inclusive = true } } },
                onBack = { navController.popBackStack() },
            )
        }
        composable(AuthRoutes.REGISTER) {
            RegisterScreen(
                onNavigateToVerify = { navController.navigate(AuthRoutes.VERIFY_EMAIL) { popUpTo(AuthRoutes.WELCOME) { inclusive = false } } },
                onNavigateToOnboarding = { navController.navigate(AuthRoutes.ONBOARDING) { popUpTo(AuthRoutes.WELCOME) { inclusive = true } } },
                onNavigateToLogin = { navController.navigate(AuthRoutes.LOGIN) { popUpTo(AuthRoutes.LOGIN) { inclusive = true } } },
                onBack = { navController.popBackStack() },
            )
        }
        composable(AuthRoutes.FORGOT_PW) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }
        composable(AuthRoutes.VERIFY_EMAIL) {
            EmailVerificationScreen(
                onVerified = { navController.navigate(AuthRoutes.ONBOARDING) { popUpTo(AuthRoutes.SPLASH) { inclusive = true } } },
                onSignOut = { navController.navigate(AuthRoutes.WELCOME) { popUpTo(AuthRoutes.SPLASH) { inclusive = true } } },
            )
        }
        composable(AuthRoutes.BLOCKED) {
            BlockedScreen(
                onSignOut = { navController.navigate(AuthRoutes.WELCOME) { popUpTo(AuthRoutes.SPLASH) { inclusive = true } } },
            )
        }
        composable(AuthRoutes.ONBOARDING) {
            OnboardingScreen(
                onComplete = { navController.navigate(AuthRoutes.HOME) { popUpTo(AuthRoutes.SPLASH) { inclusive = true } } },
            )
        }
        composable(AuthRoutes.HOME) {
            val colors = LocalRentoColors.current
            Box(
                modifier = Modifier.fillMaxSize().background(colors.bg0),
                contentAlignment = Alignment.Center,
            ) {
                com.dastageer.rento.presentation.shared.components.EmptyState(
                    icon = RentoIcons.Home,
                    title = "Home",
                    subtitle = "Module 03 — Home screen coming soon.",
                )
            }
        }
    }
}
