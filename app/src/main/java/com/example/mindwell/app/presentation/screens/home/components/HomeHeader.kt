package com.example.mindwell.app.presentation.screens.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModernHeader(
    greeting: String,
    greetingEmoji: String,
    onSettingsClick: () -> Unit,
    onTooltipRequest: () -> Unit,
    showTooltip: Boolean,
    onDismissTooltip: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6366F1), // Indigo vibrante
                        Color(0xFF8B5CF6), // Roxo moderno
                        Color(0xFFA855F7)  // Roxo pink
                    ),
                    startY = 0f,
                    endY = 400f
                ),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Saudação principal
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 28.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = greetingEmoji,
                        fontSize = 28.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Mensagem motivacional
                Text(
                    text = "Vamos cuidar do seu bem-estar hoje? ✨",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp
                    ),
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Botão de configurações moderno com efeito glassmorphism
            Box {
                Card(
                    modifier = Modifier
                        .size(48.dp)
                        .combinedClickable(
                            onClick = onSettingsClick,
                            onLongClick = onTooltipRequest
                        ),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Configurações",
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                    }
                }
                
                Tooltip(
                    tooltipText = "Configurações",
                    showTooltip = showTooltip,
                    onDismiss = onDismissTooltip
                )
            }
        }
    }
} 