package com.example.mindwell.app.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindwell.app.presentation.screens.home.HomeViewModel

@Composable
fun ModernPersonalizedTips(
    tips: List<HomeViewModel.CustomTip>,
    isLoading: Boolean,
    onTipClick: (HomeViewModel.CustomTip) -> Unit,
    onRefresh: () -> Unit,
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
                            Color(0xFFF0F9FF),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
                .padding(20.dp)
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
                                        Color(0xFF3B82F6),
                                        Color(0xFF60A5FA)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💡",
                            fontSize = 20.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = "Dicas Personalizadas",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = "Criadas especialmente para você",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666)
                        )
                    }
                }
                
                Card(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(onClick = onRefresh),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF3B82F6).copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Atualizar dicas",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Card(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onTooltipRequest("tips_help") },
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF3B82F6).copy(alpha = 0.1f)
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
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = Color(0xFF3B82F6),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Preparando suas dicas...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF666666)
                        )
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(tips) { tip ->
                        ModernTipCard(
                            tip = tip,
                            onClick = { onTipClick(tip) }
                        )
                    }
                }
            }

            // Tooltip para dicas personalizadas
            if (activeTooltip == "tips_help") {
                Tooltip(
                    tooltipText = "Dicas personalizadas criadas por IA baseadas no seu histórico e preferências",
                    showTooltip = true,
                    onDismiss = onDismissTooltip
                )
            }
        }
    }
}

@Composable
fun ModernTipCard(
    tip: HomeViewModel.CustomTip,
    onClick: () -> Unit
) {
    val categoryColors = mapOf(
        "breathing" to listOf(Color(0xFF10B981), Color(0xFF34D399)),
        "meditation" to listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA)),
        "exercise" to listOf(Color(0xFFEF4444), Color(0xFFF87171)),
        "sleep" to listOf(Color(0xFF6366F1), Color(0xFF818CF8)),
        "journaling" to listOf(Color(0xFFEAB308), Color(0xFFFBBF24)),
        "mindfulness" to listOf(Color(0xFF06B6D4), Color(0xFF22D3EE))
    )
    
    val colors = categoryColors[tip.category] ?: listOf(Color(0xFF3B82F6), Color(0xFF60A5FA))
    
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            colors[0].copy(alpha = 0.08f),
                            colors[1].copy(alpha = 0.03f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícone da categoria
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            brush = Brush.linearGradient(colors),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val emoji = when (tip.category) {
                        "breathing" -> "🫁"
                        "meditation" -> "🧘"
                        "exercise" -> "💪"
                        "sleep" -> "😴"
                        "journaling" -> "📝"
                        "mindfulness" -> "🧠"
                        else -> "✨"
                    }
                    Text(
                        text = emoji,
                        fontSize = 16.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Conteúdo da dica
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Badge da categoria
                    Text(
                        text = tip.category.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = colors[0]
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Título da dica
                    Text(
                        text = tip.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color(0xFF1A1A1A),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
} 