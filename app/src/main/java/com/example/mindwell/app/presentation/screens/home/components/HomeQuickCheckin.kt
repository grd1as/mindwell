package com.example.mindwell.app.presentation.screens.home.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernQuickCheckin(
    selectedEmoji: String,
    selectedFeeling: String,
    feelings: List<String>,
    showFeelingDropdown: Boolean,
    showSubmitSuccess: Boolean,
    onEmojiSelected: (String) -> Unit,
    onFeelingSelected: (String) -> Unit,
    onDropdownToggle: (Boolean) -> Unit,
    onSubmit: () -> Unit,
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
                            Color(0xFFF8FAFF),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header do check-in com gradiente
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFF6B6B),
                                    Color(0xFFFF8E8E)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💗",
                        fontSize = 20.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "Check-in Diário",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = "Como você está se sentindo?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666)
                    )
                }
            }
            
            // Seleção de emoji moderna com animações
            Column {
                Text(
                    text = "Escolha seu emoji do dia",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color(0xFF2D3748)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(listOf(
                        Triple("😢", "Triste", Color(0xFF4A90E2)),
                        Triple("😊", "Alegre", Color(0xFF7ED321)), 
                        Triple("😴", "Cansado", Color(0xFF9013FE)),
                        Triple("😰", "Ansioso", Color(0xFFFF9500)),
                        Triple("😨", "Medo", Color(0xFFBD10E0)),
                        Triple("😡", "Raiva", Color(0xFFD0021B))
                    )) { (emoji, label, color) ->
                        ModernEmojiOption(
                            emoji = emoji,
                            label = label,
                            color = color,
                            isSelected = selectedEmoji == emoji,
                            onClick = { onEmojiSelected(emoji) }
                        )
                    }
                }
            }
            
            // Dropdown moderno
            Column {
                Text(
                    text = "Descreva seu estado emocional",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color(0xFF2D3748)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                ExposedDropdownMenuBox(
                    expanded = showFeelingDropdown,
                    onExpandedChange = onDropdownToggle
                ) {
                    OutlinedTextField(
                        value = selectedFeeling.ifEmpty { "Toque para selecionar" },
                        onValueChange = { },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        label = { 
                            Text(
                                "Como você se sente?", 
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF666666)
                            ) 
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showFeelingDropdown)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6366F1),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedLabelColor = Color(0xFF6366F1)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    
                    ExposedDropdownMenu(
                        expanded = showFeelingDropdown,
                        onDismissRequest = { onDropdownToggle(false) }
                    ) {
                        feelings.forEach { feeling ->
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        feeling, 
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF2D3748)
                                    ) 
                                },
                                onClick = {
                                    onFeelingSelected(feeling)
                                    onDropdownToggle(false)
                                }
                            )
                        }
                    }
                }
            }
            
            // Botão de envio com gradiente moderno
            AnimatedVisibility(
                visible = selectedEmoji.isNotEmpty() && selectedFeeling.isNotEmpty(),
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Button(
                    onClick = onSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = if (showSubmitSuccess) {
                                        listOf(Color(0xFF10B981), Color(0xFF34D399))
                                    } else {
                                        listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                                    }
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (showSubmitSuccess) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Sucesso",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Check-in enviado! 🎉",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Send,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Enviar Check-in",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernEmojiOption(
    emoji: String,
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "EmojiScale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Card(
            modifier = Modifier
                .size(56.dp)
                .scale(scale)
                .clickable(onClick = onClick),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) {
                    color.copy(alpha = 0.15f)
                } else {
                    Color(0xFFF8FAFF)
                }
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected) 6.dp else 2.dp
            ),
            border = if (isSelected) BorderStroke(2.dp, color) else null
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 24.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) color else Color(0xFF666666),
            textAlign = TextAlign.Center
        )
    }
} 