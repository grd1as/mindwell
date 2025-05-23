package com.example.mindwell.app.presentation.screens.evolution

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mindwell.app.common.navigation.AppDestinations
import com.example.mindwell.app.domain.entities.SummaryItem
import com.example.mindwell.app.presentation.screens.evolution.TrendData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvolutionScreen(
    nav: NavController,
    vm: EvolutionViewModel = hiltViewModel()
) {
    val state = vm.state
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultados") },
                navigationIcon = {
                    IconButton(onClick = { nav.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            ErrorContent(state.error, onRetry = { vm.loadMockedData(state.currentMonth) })
        } else {
            EvolutionContent(
                modifier = Modifier.padding(padding),
                monthLabel = vm.formatCurrentMonth(),
                summary = state.summary,
                trendData = state.trendData,
                trendDirection = state.trendDirection,
                trendTip = vm.getTrendTip(),
                onPreviousMonth = { vm.previousMonth() },
                onNextMonth = { vm.nextMonth() }
            )
        }
    }
}

@Composable
private fun ErrorContent(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ocorreu um erro:",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Tentar novamente")
        }
    }
}

@Composable
private fun EvolutionContent(
    modifier: Modifier = Modifier,
    monthLabel: String,
    summary: com.example.mindwell.app.domain.entities.Summary?,
    trendData: List<TrendData>,
    trendDirection: String,
    trendTip: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    if (summary == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nenhum dado disponível para o período selecionado")
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Month selector
        MonthSelector(
            monthLabel = monthLabel,
            onPreviousMonth = onPreviousMonth,
            onNextMonth = onNextMonth
        )
        
        // Status card
        StatusCard(summary)
        
        // Summary card
        SummaryCard(summary = summary)
        
        // Breakdown chart
        BreakdownChart(summary.breakdown)
        
        // Trend analysis card
        TrendAnalysisCard(
            trendData = trendData,
            trendDirection = trendDirection,
            trendTip = trendTip
        )
        
        // Espaço maior na parte inferior para não sobrepor com a barra de navegação
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun MonthSelector(
    monthLabel: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Mês anterior")
        }
        
        Text(
            text = monthLabel,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Próximo mês")
        }
    }
}

@Composable
private fun StatusCard(summary: com.example.mindwell.app.domain.entities.Summary) {
    val status = when (summary.overallLevel) {
        "VERY_GOOD" -> "Excelente"
        "GOOD" -> "Bom"
        "NEUTRAL" -> "Neutro"
        "BAD" -> "Difícil"
        "VERY_BAD" -> "Desafiador"
        else -> "Indefinido"
    }
    
    val statusColor = when (summary.overallLevel) {
        "VERY_GOOD" -> Color(0xFF4CAF50)
        "GOOD" -> Color(0xFF8BC34A)
        "NEUTRAL" -> Color(0xFFFFEB3B)
        "BAD" -> Color(0xFFFFAB40)
        "VERY_BAD" -> Color(0xFFFF5252)
        else -> Color.Gray
    }
    
    val message = when (summary.overallLevel) {
        "VERY_GOOD" -> "Seu bem-estar está em alta! Continue com as práticas positivas."
        "GOOD" -> "Você está indo bem! Mantenha os bons hábitos."
        "NEUTRAL" -> "Seu estado emocional está equilibrado. Considere atividades que aumentem seu bem-estar."
        "BAD" -> "Tem sido um período difícil. Pratique autocuidado e busque apoio se necessário."
        "VERY_BAD" -> "Momento desafiador. Considere falar com um profissional de saúde mental."
        else -> "Sem dados suficientes para análise."
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = statusColor.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = when (summary.overallLevel) {
                        "VERY_GOOD" -> Icons.Default.Star
                        "GOOD" -> Icons.Default.Favorite
                        "NEUTRAL" -> Icons.Default.Info
                        "BAD" -> Icons.Default.Warning
                        "VERY_BAD" -> Icons.Default.Close
                        else -> Icons.Default.MoreVert
                    },
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(28.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Status: $status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SummaryCard(summary: com.example.mindwell.app.domain.entities.Summary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Resumo do Mês",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Nível Geral: ${summary.overallLevel}",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Total de Check-ins: ${summary.total}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun BreakdownChart(
    breakdown: List<SummaryItem>
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Distribuição de Humor",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Chart visualization
            breakdown.forEach { item ->
                val barColor = when (item.level) {
                    "VERY_BAD" -> Color(0xFFFF5252)  // Red
                    "BAD" -> Color(0xFFFFAB40)       // Orange
                    "NEUTRAL" -> Color(0xFFFFEB3B)   // Yellow
                    "GOOD" -> Color(0xFF8BC34A)      // Light green
                    "VERY_GOOD" -> Color(0xFF4CAF50) // Green
                    else -> Color.Gray
                }
                
                val barPercent = (item.percent / 100f).coerceIn(0f, 1f)
                
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.value,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Text(
                            text = "${item.count} (${item.percent}%)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .background(Color.LightGray, RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(barPercent)
                                .fillMaxHeight()
                                .background(barColor, RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrendAnalysisCard(
    trendData: List<TrendData>,
    trendDirection: String,
    trendTip: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Análise de Tendência",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Visualização simples de tendência com círculos conectados
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                trendData.forEachIndexed { index, trendItem ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Círculo colorido representando o nível
                        val dotColor = when (trendItem.moodLevel) {
                            "VERY_BAD" -> Color(0xFFFF5252)
                            "BAD" -> Color(0xFFFFAB40)
                            "NEUTRAL" -> Color(0xFFFFEB3B)
                            "GOOD" -> Color(0xFF8BC34A)
                            "VERY_GOOD" -> Color(0xFF4CAF50)
                            else -> Color.Gray
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(dotColor, CircleShape)
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Label da semana
                        Text(
                            text = trendItem.weekLabel,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    // Linha conectando os pontos, exceto após o último
                    if (index < trendData.size - 1) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(Color.LightGray)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Descrição da tendência
            Text(
                text = "Sua tendência geral é $trendDirection neste mês.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Dica baseada na tendência
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Dica: $trendTip",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
} 