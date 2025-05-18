package com.example.mindwell.app.common.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mindwell.app.R

/**
 * Modelo de item da barra de navegação
 */
data class BottomNavItem(
    val route: String,
    val titleResId: Int,
    val icon: @Composable () -> Unit
)

/**
 * Composable da barra de navegação inferior
 */
@Composable
fun BottomNavigationBar(navController: NavController) {
    // Definição dos itens da barra de navegação
    val navItems = listOf(
        BottomNavItem(
            route = AppDestinations.HOME,
            titleResId = R.string.home,
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) }
        ),
        BottomNavItem(
            route = AppDestinations.CHECK_IN,
            titleResId = R.string.check_in,
            icon = { Icon(imageVector = Icons.Default.Face, contentDescription = null) }
        ),
        BottomNavItem(
            route = AppDestinations.ASSESSMENT,
            titleResId = R.string.assessment,
            icon = { Icon(imageVector = Icons.Default.List, contentDescription = null) }
        ),
        BottomNavItem(
            route = AppDestinations.RESOURCES,
            titleResId = R.string.resources,
            icon = { Icon(imageVector = Icons.Default.Info, contentDescription = null) }
        ),
        BottomNavItem(
            route = AppDestinations.METRICS,
            titleResId = R.string.metrics,
            icon = { Icon(imageVector = Icons.Default.Favorite, contentDescription = null) }
        )
    )
    
    // Obter rota atual para destacar o item selecionado
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    NavigationBar {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Evitar múltiplas cópias da mesma rota no backstack
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Evitar múltiplas cópias da mesma rota no backstack
                        launchSingleTop = true
                        // Restaurar estado ao retornar a esta rota
                        restoreState = true
                    }
                },
                icon = { item.icon() },
                label = { Text(text = stringResource(id = item.titleResId)) }
            )
        }
    }
} 