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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mindwell.app.domain.entities.AppTheme
import com.example.mindwell.app.domain.usecases.userpreferences.HasCompletedOnboardingUseCase
import com.example.mindwell.app.domain.usecases.userpreferences.MockHasCompletedOnboardingUseCase
import com.example.mindwell.app.presentation.screens.assessment.AssessmentScreen
import com.example.mindwell.app.presentation.screens.checkin.CheckInScreen
import com.example.mindwell.app.presentation.screens.home.HomeScreen
import com.example.mindwell.app.presentation.screens.metrics.MetricsScreen
import com.example.mindwell.app.presentation.screens.onboarding.OnboardingScreen
import com.example.mindwell.app.presentation.screens.resources.ResourceDetailScreen
import com.example.mindwell.app.presentation.screens.resources.ResourcesScreen
import kotlinx.coroutines.flow.firstOrNull

/**
 * Composable principal de navegação que define as rotas do aplicativo.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    // Verifica se o usuário já completou o onboarding usando o caso de uso
    val hasCompletedOnboardingUseCase: HasCompletedOnboardingUseCase = MockHasCompletedOnboardingUseCase()
    
    var startDestination by remember { mutableStateOf(AppDestinations.ONBOARDING) }
    var isInitialized by remember { mutableStateOf(false) }
    
    // Verificar se o onboarding já foi concluído
    LaunchedEffect(Unit) {
        val hasCompleted = hasCompletedOnboardingUseCase().firstOrNull() ?: false
        if (hasCompleted) {
            startDestination = AppDestinations.HOME
        }
        isInitialized = true
    }
    
    if (!isInitialized) {
        // Mostrar uma tela de carregamento ou deixar em branco enquanto verifica
        return
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppDestinations.ONBOARDING) {
            OnboardingScreen(navController = navController)
        }
        
        composable(AppDestinations.HOME) {
            HomeScreen(navController = navController)
        }
        
        composable(AppDestinations.CHECK_IN) {
            CheckInScreen(navController = navController)
        }
        
        composable(AppDestinations.ASSESSMENT) {
            AssessmentScreen(navController = navController)
        }
        
        composable(AppDestinations.RESOURCES) {
            ResourcesScreen(navController = navController)
        }
        
        composable(AppDestinations.METRICS) {
            MetricsScreen(navController = navController)
        }
        
        // Rotas detalhadas
        composable(
            route = AppDestinations.RESOURCE_DETAIL,
            arguments = listOf(
                navArgument("resourceId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val resourceId = backStackEntry.arguments?.getLong("resourceId") ?: 0L
            ResourceDetailScreen(
                navController = navController,
                resourceId = resourceId
            )
        }
    }
} 