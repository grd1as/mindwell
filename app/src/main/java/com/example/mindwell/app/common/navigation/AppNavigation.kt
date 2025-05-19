package com.example.mindwell.app.common.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mindwell.app.data.network.TokenStore
import com.example.mindwell.app.domain.entities.AppTheme
import com.example.mindwell.app.domain.usecases.userpreferences.HasCompletedOnboardingUseCase
import com.example.mindwell.app.domain.usecases.userpreferences.MockHasCompletedOnboardingUseCase
import com.example.mindwell.app.presentation.screens.assessment.AssessmentScreen
import com.example.mindwell.app.presentation.screens.checkin.CheckInScreen
import com.example.mindwell.app.presentation.screens.home.HomeScreen
import com.example.mindwell.app.presentation.screens.login.LoginScreen
import com.example.mindwell.app.presentation.screens.metrics.MetricsScreen
import com.example.mindwell.app.presentation.screens.onboarding.OnboardingScreen
import com.example.mindwell.app.presentation.screens.resources.ResourceDetailScreen
import com.example.mindwell.app.presentation.screens.resources.ResourcesScreen
import com.example.mindwell.app.presentation.screens.settings.SettingsScreen
import kotlinx.coroutines.flow.firstOrNull

/**
 * Composable principal de navegação que define as rotas do aplicativo.
 */
@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val ctx = LocalContext.current

    var start by remember { mutableStateOf(AppDestinations.LOGIN) }
    var ready by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        start = if (TokenStore.load(ctx) == null)
            AppDestinations.LOGIN
        else
            AppDestinations.ONBOARDING
        ready = true
    }
    if (!ready) return

    NavHost(navController = nav, startDestination = start) {

        composable(AppDestinations.LOGIN)      { LoginScreen(nav) }
        composable(AppDestinations.ONBOARDING) { OnboardingScreen(navController = nav) }
        composable(AppDestinations.HOME)       { HomeScreen(navController = nav) }
        composable(AppDestinations.SETTINGS)   { SettingsScreen(navController = nav) }
        composable(AppDestinations.CHECK_IN)   { CheckInScreen(navController = nav) }
        composable(AppDestinations.ASSESSMENT) { AssessmentScreen(navController = nav) }
        composable(AppDestinations.RESOURCES)  { ResourcesScreen(navController = nav) }
        composable(AppDestinations.METRICS)    { MetricsScreen(navController = nav) }

        composable(
            route = AppDestinations.RESOURCE_DETAIL,
            arguments = listOf(navArgument("resourceId") { type = NavType.LongType })
        ) { backStackEntry ->
            val resId = backStackEntry.arguments?.getLong("resourceId") ?: 0L
            ResourceDetailScreen(navController = nav, resourceId = resId)
        }
    }
}

