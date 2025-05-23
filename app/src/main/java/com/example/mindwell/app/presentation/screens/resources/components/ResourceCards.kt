package com.example.mindwell.app.presentation.screens.resources.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindwell.app.data.services.PersonalizedResource
import com.example.mindwell.app.data.services.PersonalizedTip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernPersonalizedResourceCard(
    resource: PersonalizedResource,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = resource.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1A1A1A)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = resource.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Ícone com background colorido
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF6366F1),
                                    Color(0xFF8B5CF6)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getResourceEmoji(resource.icon),
                        fontSize = 24.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF3F4F6)
                        )
                    ) {
                        Text(
                            text = "${resource.duration_minutes} min",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFEEF2FF)
                        )
                    ) {
                        Text(
                            text = resource.difficulty,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color(0xFF6366F1),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6366F1)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = resource.action_text,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModernPersonalizedTipCard(tip: PersonalizedTip) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = when (tip.priority) {
                                        "high" -> listOf(Color(0xFFFF6B6B), Color(0xFFFF8E8E))
                                        "medium" -> listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                                        else -> listOf(Color(0xFF10B981), Color(0xFF34D399))
                                    }
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getTipEmoji(tip.icon),
                            fontSize = 18.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = tip.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1A1A1A)
                    )
                }
                
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF3F4F6)
                    )
                ) {
                    Text(
                        text = tip.estimated_time,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = tip.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun getResourceIcon(iconName: String): ImageVector {
    return when (iconName) {
        "breathing" -> Icons.Default.Favorite
        "meditation" -> Icons.Default.Star
        "exercise" -> Icons.Default.Create
        "sleep" -> Icons.Default.Info
        "writing" -> Icons.Default.Edit
        "heart" -> Icons.Default.Favorite
        else -> Icons.Default.Star
    }
}

@Composable
fun getResourceEmoji(iconName: String): String {
    return when (iconName) {
        "breathing" -> "🌬️"
        "meditation" -> "🧘"
        "exercise" -> "🏃"
        "sleep" -> "😴"
        "writing" -> "✍️"
        "heart" -> "❤️"
        else -> "✨"
    }
}

@Composable
fun getTipIcon(iconName: String): ImageVector {
    return when (iconName) {
        "lightbulb" -> Icons.Default.Info
        "heart" -> Icons.Default.Favorite
        "star" -> Icons.Default.Star
        "smile" -> Icons.Default.Favorite
        "target" -> Icons.Default.Info
        else -> Icons.Default.Info
    }
}

@Composable
fun getTipEmoji(iconName: String): String {
    return when (iconName) {
        "lightbulb" -> "💡"
        "heart" -> "❤️"
        "star" -> "⭐"
        "smile" -> "😊"
        "target" -> "🎯"
        else -> "💡"
    }
} 