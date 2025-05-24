package com.example.mindwell.app.presentation.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindwell.app.data.model.WeeklyCheckinDTO
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ModernWeeklyProgress(
    weeklyData: WeeklyCheckinDTO?,
    onTooltipRequest: (String) -> Unit,
    activeTooltip: String?,
    onDismissTooltip: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF4E6),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header com gradiente
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFF9500),
                                        Color(0xFFFFB84D)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🏆",
                            fontSize = 20.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = "Progresso Semanal",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = "Sua jornada de bem-estar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onTooltipRequest("progress_help") },
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFF9500).copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Ajuda",
                            tint = Color(0xFFFF9500),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Tooltip para progresso semanal
            if (activeTooltip == "progress_help") {
                Tooltip(
                    tooltipText = "Acompanhe seus check-ins diários da semana para manter a consistência",
                    showTooltip = true,
                    onDismiss = onDismissTooltip
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            if (weeklyData != null) {
                val completedDays = weeklyData.days.count { it.hasCheckin }
                
                // Dias da semana modernos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    weeklyData.days.forEachIndexed { index, day ->
                        ModernDayMarker(
                            date = day.date,
                            hasCheckin = day.hasCheckin,
                            dayIndex = index
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Mensagem motivacional
                val motivationalMessage = when (completedDays) {
                    0 -> "Vamos começar! 🚀"
                    in 1..2 -> "Ótimo começo! 💪"
                    in 3..4 -> "Você está indo bem! ⭐"
                    in 5..6 -> "Quase lá! Continue! 🔥"
                    7 -> "Parabéns! Semana completa! 🎉"
                    else -> "Continue assim! 👏"
                }
                
                Text(
                    text = motivationalMessage,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Bold
                )
                
            } else {
                // Estado de carregamento elegante
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            Color(0xFFF8FAFF),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color(0xFF6366F1),
                        strokeWidth = 3.dp
                    )
                }
            }
        }
    }
}

@Composable
fun ModernDayMarker(
    date: String,
    hasCheckin: Boolean,
    dayIndex: Int
) {
    val dayColors = listOf(
        Color(0xFFFF6B6B), // Domingo - Vermelho
        Color(0xFF4ECDC4), // Segunda - Azul claro
        Color(0xFF45B7D1), // Terça - Azul
        Color(0xFF96CEB4), // Quarta - Verde claro
        Color(0xFFFECA57), // Quinta - Amarelo
        Color(0xFFFF9FF3), // Sexta - Rosa
        Color(0xFFBD93F9)  // Sábado - Roxo
    )
    
    val dayOfWeek = try {
        val localDate = LocalDate.parse(date)
        val dayNumber = localDate.dayOfWeek.value // 1 = Segunda, 7 = Domingo
        
        // Mapear para português brasileiro
        when (dayNumber) {
            1 -> "Seg"
            2 -> "Ter"
            3 -> "Qua"
            4 -> "Qui"
            5 -> "Sex"
            6 -> "Sáb"
            7 -> "Dom"
            else -> "?"
        }
    } catch (e: Exception) {
        "?"
    }
    
    val dayColor = dayColors.getOrNull(dayIndex) ?: Color(0xFF6366F1)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = if (hasCheckin) {
                    dayColor
                } else {
                    Color(0xFFF8FAFF)
                }
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (hasCheckin) 4.dp else 1.dp
            ),
            border = if (!hasCheckin) BorderStroke(1.dp, Color(0xFFE2E8F0)) else null
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (hasCheckin) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Check-in realizado",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = "${dayIndex + 1}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9CA3AF),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Text(
            text = dayOfWeek.take(3),
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 10.sp,
                fontWeight = if (hasCheckin) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (hasCheckin) dayColor else Color(0xFF9CA3AF)
        )
    }
} 