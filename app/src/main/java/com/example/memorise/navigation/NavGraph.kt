package com.example.memorise.navigation

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.memorise.ui.screen.landing.LandingScreen
import com.example.memorise.ui.screen.onboarding.*
import com.example.memorise.ui.screen.sigin.SignInScreen
import com.example.memorise.ui.screen.signup.SignUpScreen

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "landing"
    ) {

        composable("landing") {
            LandingScreen(
                onNavigate = {
                    navController.navigate("Onboarding1")
                }
            )
        }

        composable("Onboarding1") {
            OnboardingScreen1(
                onNext = { navController.navigate("Onboarding2") },
                onSkip = { navController.navigate("signin") }
            )
        }

        composable("Onboarding2") {
            OnboardingScreen2(
                onNext = { navController.navigate("onboarding3") },
                onSkip = { navController.navigate("signin") }
            )
        }

        composable("onboarding3") {
            OnboardingScreen3(
                onNext = { navController.navigate("onboarding4") },
                onSkip = { navController.navigate("signin") }
            )
        }

        composable("onboarding4") {
            OnboardingScreen4(
                onNext = { navController.navigate("Onboarding5") },
                onSkip = { navController.navigate("signin") }
            )
        }

        composable("Onboarding5") {
            OnboardingScreen5(
                onSignUp = { navController.navigate("signup") },
                onLogin = { navController.navigate("signin") }
            )
        }

        composable("signin") {
            SignInScreen(
                onSignInSuccess = { navController.navigate("home") }
            )
        }

        composable("signup") {
            SignUpScreen(
                onLoginClick = { navController.navigate("signin") }
            )
        }
    }
}
