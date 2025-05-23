package com.example.mindwell.app.common.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mindwell.app.domain.usecases.onboarding.GetOnboardingStateUseCase
import com.example.mindwell.app.presentation.screens.checkin.CheckinScreen
import com.example.mindwell.app.presentation.screens.evolution.EvolutionScreen
import com.example.mindwell.app.presentation.screens.forms.FormDetailScreen
import com.example.mindwell.app.presentation.screens.forms.FormsScreen
import com.example.mindwell.app.presentation.screens.home.HomeScreen
import com.example.mindwell.app.presentation.screens.login.LoginScreen
import com.example.mindwell.app.presentation.screens.onboarding.OnboardingScreen
import com.example.mindwell.app.presentation.screens.resources.ResourceDetailScreen
import com.example.mindwell.app.presentation.screens.resources.ResourcesScreen
import com.example.mindwell.app.presentation.screens.settings.SettingsScreen
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

/**
 * Destinos de navegação do aplicativo.
 */
object AppDestinations {
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val HOME = "home"
    const val CHECK_IN = "check_in"
    const val FORMS = "forms"
    const val FORM_DETAIL = "form_detail/{formId}"
    const val RESOURCES = "resources"
    const val RESOURCE_DETAIL = "resource_detail/{resourceId}"
    const val EVOLUTION = "evolution"
    const val SETTINGS = "settings"
    const val REPORT = "report"
    
    // Funções auxiliares para navegação com parâmetros
    fun formDetail(formId: Int) = "form_detail/$formId"
    fun resourceDetail(resourceId: String) = "resource_detail/$resourceId"
    
    // Lista de telas que não devem mostrar a bottom bar
    val routesWithoutBottomBar = listOf(ONBOARDING, LOGIN)
}

/**
 * Classe para determinar a tela inicial com base no estado do onboarding.
 */
class StartDestinationHelper @Inject constructor(
    private val getOnboardingStateUseCase: GetOnboardingStateUseCase
) {
    /**
     * Determina a tela inicial do aplicativo com base no estado de onboarding.
     * @return O destino inicial da navegação (onboarding ou login)
     */
    suspend fun getStartDestination(): String {
        return try {
            // Obtém apenas o primeiro resultado do flow e verifica se o onboarding foi completado
            val result = getOnboardingStateUseCase().firstOrNull()
            val isOnboardingCompleted = result?.getOrNull()?.isCompleted ?: false
            
            if (isOnboardingCompleted) {
                AppDestinations.LOGIN
            } else {
                AppDestinations.ONBOARDING
            }
        } catch (e: Exception) {
            // Em caso de erro, inicia pelo onboarding para garantir
            AppDestinations.ONBOARDING
        }
    }
}

/**
 * Componente de navegação principal do aplicativo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    initialScreen: String = AppDestinations.ONBOARDING // Por padrão, começa com onboarding
) {
    // Obtém a entrada atual da pilha de navegação
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    
    // Rota atual para destacar o item correto na bottom bar
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Determina se a bottom bar deve ser exibida
    val shouldShowBottomBar by remember(currentRoute) {
        derivedStateOf {
            currentRoute != null && 
            !AppDestinations.routesWithoutBottomBar.any { 
                currentRoute.startsWith(it)
            }
        }
    }
    
    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                AppBottomBar(navController, currentRoute)
            }
        }
    ) { paddingValues ->
        // NavHost envolto em Box com padding para respeitar o Scaffold
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(
                navController = navController,
                startDestination = initialScreen
            ) {
                // Tela de Onboarding
                composable(AppDestinations.ONBOARDING) {
                    OnboardingScreen(navController)
                }
                
                // Tela de Login
                composable(AppDestinations.LOGIN) {
                    LoginScreen(navController)
                }
                
                // Tela Home
                composable(AppDestinations.HOME) {
                    HomeScreen(navController)
                }
                
                // Tela de Check-in
                composable(AppDestinations.CHECK_IN) {
                    CheckinScreen(navController)
                }
                
                // Tela de Formulários
                composable(AppDestinations.FORMS) {
                    FormsScreen(navController)
                }
                
                // Tela de Detalhes do Formulário
                composable(AppDestinations.FORM_DETAIL) { backStackEntry ->
                    FormDetailScreen(navController)
                }
                
                // Tela de Recursos/Guias
                composable(AppDestinations.RESOURCES) {
                    ResourcesScreen(navController)
                }
                
                // Tela de Detalhes do Recurso
                composable(AppDestinations.RESOURCE_DETAIL) { backStackEntry ->
                    val resourceId = backStackEntry.arguments?.getString("resourceId") ?: ""
                    ResourceDetailScreen(navController, resourceId)
                }
                
                // Tela de Evolução
                composable(AppDestinations.EVOLUTION) {
                    EvolutionScreen(navController)
                }
                
                // Tela de Configurações
                composable(AppDestinations.SETTINGS) {
                    SettingsScreen(navController)
                }
                
                // Tela de Denúncia/Report
                composable(AppDestinations.REPORT) {
                    // TODO: Implementar tela de Denúncia/Report
                }
            }
        }
    }
} 