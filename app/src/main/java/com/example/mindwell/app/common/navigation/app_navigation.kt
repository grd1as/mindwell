package com.example.mindwell.app.common.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mindwell.app.presentation.screens.checkin.CheckinScreen
import com.example.mindwell.app.presentation.screens.forms.FormDetailScreen
import com.example.mindwell.app.presentation.screens.forms.FormsScreen
import com.example.mindwell.app.presentation.screens.home.HomeScreen
import com.example.mindwell.app.presentation.screens.login.LoginScreen

/**
 * Destinos de navegação do aplicativo.
 */
object AppDestinations {
    const val LOGIN = "login"
    const val HOME = "home"
    const val CHECK_IN = "check_in"
    const val FORMS = "forms"
    const val FORM_DETAIL = "form_detail/{formId}"
    const val RESOURCES = "resources"
    const val EVOLUTION = "evolution"
    const val SETTINGS = "settings"
    const val REPORT = "report"
    
    // Funções auxiliares para navegação com parâmetros
    fun formDetail(formId: Int) = "form_detail/$formId"
}

/**
 * Componente de navegação principal do aplicativo.
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppDestinations.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
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
        
        // Tela de Recursos
        composable(AppDestinations.RESOURCES) {
            // TODO: Implementar tela de Recursos
        }
        
        // Tela de Evolução
        composable(AppDestinations.EVOLUTION) {
            // TODO: Implementar tela de Evolução
        }
        
        // Tela de Configurações
        composable(AppDestinations.SETTINGS) {
            // TODO: Implementar tela de Configurações
        }
        
        // Tela de Denúncia/Report
        composable(AppDestinations.REPORT) {
            // TODO: Implementar tela de Denúncia/Report
        }
    }
} 