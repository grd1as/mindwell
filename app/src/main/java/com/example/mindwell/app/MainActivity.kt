package com.example.mindwell.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.mindwell.app.common.design_system.theme.MindWellTheme
import com.example.mindwell.app.common.navigation.AppNavigation
import com.example.mindwell.app.presentation.MainViewModel
import com.example.mindwell.app.presentation.NavigationState
import com.example.mindwell.app.presentation.screens.LoadingScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Atividade principal do aplicativo MindWell.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MindWell)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            MindWellTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MindWellApp()
                }
            }
        }
    }
}

/**
 * Composable principal do aplicativo que gerencia a navegação inicial.
 */
@Composable
fun MindWellApp(
    main_view_model: MainViewModel = hiltViewModel()
) {
    val nav_controller = rememberNavController()
    
    when (val state = main_view_model.navigation_state) {
        is NavigationState.Loading -> {
            LoadingScreen(message = "Iniciando MindWell...")
        }
        
        is NavigationState.Ready -> {
            AppNavigation(
                navController = nav_controller,
                initialScreen = state.destination
            )
        }
        
        is NavigationState.Error -> {
            // Em caso de erro, mostra a mensagem por um momento e depois carrega o onboarding
            LoadingScreen(message = state.message)
            // Você pode adicionar aqui um delay e depois navegar para o onboarding
            // ou mostrar uma tela de erro específica
        }
    }
}
