package com.example.mindwell.app.common.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy

/**
 * Representa um item de navegação na barra inferior.
 */
data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val color: Color
)

/**
 * Lista de itens da barra de navegação inferior.
 */
val bottomNavItems = listOf(
    BottomNavItem(
        route = AppDestinations.HOME,
        title = "Início",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home,
        color = Color(0xFF6366F1) // Indigo
    ),
    BottomNavItem(
        route = AppDestinations.CHECK_IN,
        title = "Check-in",
        icon = Icons.Outlined.FavoriteBorder,
        selectedIcon = Icons.Filled.Favorite,
        color = Color(0xFF10B981) // Verde
    ),
    BottomNavItem(
        route = AppDestinations.FORMS,
        title = "Questionários",
        icon = Icons.Outlined.List,
        selectedIcon = Icons.Filled.List,
        color = Color(0xFFEF4444) // Vermelho
    ),
    BottomNavItem(
        route = AppDestinations.RESOURCES,
        title = "Recursos",
        icon = Icons.Outlined.Settings,
        selectedIcon = Icons.Filled.Settings,
        color = Color(0xFF8B5CF6) // Roxo
    ),
    BottomNavItem(
        route = AppDestinations.EVOLUTION,
        title = "Evolução",
        icon = Icons.Outlined.Info,
        selectedIcon = Icons.Filled.Info,
        color = Color(0xFF06B6D4) // Ciano
    )
)

/**
 * Componente moderno da barra de navegação inferior.
 * @param navController O controlador de navegação
 * @param currentRoute A rota atual para destacar o item selecionado
 */
@Composable
fun AppBottomBar(
    navController: NavController,
    currentRoute: String?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 12.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFFAFAFF)
                        )
                    )
                )
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentRoute?.startsWith(item.route) == true
                
                ModernNavItem(
                    item = item,
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
                    }
                )
            }
        }
    }
}

@Composable
fun ModernNavItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        // Container do ícone
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    if (selected) {
                        Brush.linearGradient(
                            colors = listOf(
                                item.color,
                                item.color.copy(alpha = 0.8f)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent
                            )
                        )
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (selected) item.selectedIcon else item.icon,
                contentDescription = item.title,
                tint = if (selected) Color.White else Color(0xFF9CA3AF),
                modifier = Modifier.size(22.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Label
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (selected) item.color else Color(0xFF9CA3AF)
        )
    }
} 