package com.example.mindwell.app.presentation.screens.evolution.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindwell.app.domain.entities.MonthlySummary

/**
 * Card que exibe o resumo mensal dos check-ins
 */
@Composable
fun MonthlySummaryCard(
    summary: MonthlySummary,
    get_emoji_from_option_id: (Int) -> String,
    get_trend_icon: () -> String,
    format_workload_change: () -> String,
    get_workload_change_color: () -> Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabeçalho
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Resumo do Mês",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = get_trend_icon(),
                    fontSize = 24.sp
                )
            }
            
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            
            // Estatísticas principais
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Total de check-ins
                summary_stat_item(
                    label = "Check-ins",
                    value = summary.total_checkins.toString(),
                    modifier = Modifier.weight(1f)
                )
                
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(60.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )
                
                // Emoji predominante
                summary_stat_item(
                    label = "Humor Principal",
                    value = if (summary.predominant_emoji.isNotEmpty()) {
                        get_emoji_from_option_id(summary.predominant_emoji.first().option_id)
                    } else "😐",
                    subtitle = summary.predominant_emoji.firstOrNull()?.label ?: "N/A",
                    modifier = Modifier.weight(1f)
                )
                
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(60.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )
                
                // Sentimento predominante
                summary_stat_item(
                    label = "Sentimento",
                    value = summary.predominant_sentiment.firstOrNull()?.label ?: "N/A",
                    subtitle = "${summary.predominant_sentiment.firstOrNull()?.count ?: 0}x",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            
            // Carga de trabalho
            workload_section(
                summary = summary,
                format_workload_change = format_workload_change,
                get_workload_change_color = get_workload_change_color
            )
        }
    }
}

/**
 * Item de estatística do resumo
 */
@Composable
private fun summary_stat_item(
    label: String,
    value: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        
        subtitle?.let {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Seção de carga de trabalho
 */
@Composable
private fun workload_section(
    summary: MonthlySummary,
    format_workload_change: () -> String,
    get_workload_change_color: () -> Color
) {
    val workload = summary.workload
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Carga de Trabalho",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Média atual
            Column {
                Text(
                    text = "Média Atual",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = String.format("%.1f", workload.current_avg),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Variação
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = get_workload_change_color().copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = format_workload_change(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = get_workload_change_color(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "vs. mês anterior: ${String.format("%.1f", workload.previous_avg)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
} 