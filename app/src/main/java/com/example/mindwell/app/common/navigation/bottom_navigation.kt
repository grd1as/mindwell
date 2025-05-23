package com.example.mindwell.app.common.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy

/**
 * Representa um item de navegação na barra inferior.
 */
data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

/**
 * Lista de itens da barra de navegação inferior.
 */
val bottomNavItems = listOf(
    BottomNavItem(
        route = AppDestinations.HOME,
        title = "Início",
        icon = Icons.Default.Home
    ),
    BottomNavItem(
        route = AppDestinations.CHECK_IN,
        title = "Check-in",
        icon = Icons.Default.Add
    ),
    BottomNavItem(
        route = AppDestinations.FORMS,
        title = "Quest",
        icon = Icons.Default.List
    ),
    BottomNavItem(
        route = AppDestinations.RESOURCES,
        title = "Guias",
        icon = Icons.Default.Settings
    ),
    BottomNavItem(
        route = AppDestinations.EVOLUTION,
        title = "Resultados",
        icon = Icons.Default.Info
    )
)

/**
 * Componente da barra de navegação inferior.
 * @param navController O controlador de navegação
 * @param currentRoute A rota atual para destacar o item selecionado
 */
@Composable
fun AppBottomBar(
    navController: NavController,
    currentRoute: String?
) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            val selected = currentRoute?.startsWith(item.route) == true
            
            NavigationBarItem(
                selected = selected,
                onClick = { 
                    if (!selected) {
                        navController.navigate(item.route) {
                            // Evita criar várias cópias do destino na pilha de volta
                            popUpTo(AppDestinations.HOME) {
                                saveState = true
                            }
                            // Reutiliza o estado da composição se possível
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) }
            )
        }
    }
} 