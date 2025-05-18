package com.example.mindwell.app.presentation.screens.metrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mindwell.app.common.navigation.BottomNavigationBar
import com.example.mindwell.app.domain.entities.WellbeingMetrics
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricsScreen(
    navController: NavController,
    viewModel: MetricsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Título
            Text(
                text = "Métricas de Bem-estar",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Filtro de período
            Text(
                text = "Selecione o período:",
                style = MaterialTheme.typography.titleSmall
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedPeriod == MetricPeriod.WEEK,
                    onClick = { viewModel.setSelectedPeriod(MetricPeriod.WEEK) },
                    label = { Text("Semana") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                
                FilterChip(
                    selected = uiState.selectedPeriod == MetricPeriod.MONTH,
                    onClick = { viewModel.setSelectedPeriod(MetricPeriod.MONTH) },
                    label = { Text("Mês") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                
                FilterChip(
                    selected = uiState.selectedPeriod == MetricPeriod.QUARTER,
                    onClick = { viewModel.setSelectedPeriod(MetricPeriod.QUARTER) },
                    label = { Text("Trimestre") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                
                FilterChip(
                    selected = uiState.selectedPeriod == MetricPeriod.YEAR,
                    onClick = { viewModel.setSelectedPeriod(MetricPeriod.YEAR) },
                    label = { Text("Ano") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Estado de carregamento
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            
            // Mensagem de erro
            uiState.error?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            
            // Conteúdo principal
            if (!uiState.isLoading && uiState.error == null) {
                if (uiState.wellbeingMetrics.isEmpty()) {
                    NoMetricsAvailable()
                } else {
                    // Resumo dos dados
                    MetricsSummary(
                        wellbeingScore = viewModel.calculateAverageWellbeing(),
                        moodScore = viewModel.calculateAverageMood(),
                        stressScore = viewModel.calculateAverageStress()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Lista detalhada de métricas
                    Text(
                        text = "Histórico de Métricas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    uiState.wellbeingMetrics.sortedByDescending { it.date }.forEach { metric ->
                        MetricItem(metric = metric)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MetricsSummary(
    wellbeingScore: Float,
    moodScore: Float,
    stressScore: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Resumo do Período",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Indicador circular de bem-estar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WellbeingIndicator(
                    score = wellbeingScore,
                    label = "Bem-estar Geral",
                    maxValue = 100f
                )
                
                MoodIndicator(
                    score = moodScore,
                    label = "Humor Médio",
                    maxValue = 5f
                )
                
                StressIndicator(
                    score = stressScore,
                    label = "Estresse Médio",
                    maxValue = 5f
                )
            }
        }
    }
}

@Composable
fun WellbeingIndicator(
    score: Float,
    label: String,
    maxValue: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val normalizedScore = (score / maxValue) * 100
        
        // Determine color based on score
        val color = when {
            normalizedScore < 30 -> MaterialTheme.colorScheme.error
            normalizedScore < 70 -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
        }
        
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)
        ) {
            CircularProgressIndicator(
                progress = normalizedScore / 100,
                modifier = Modifier.size(80.dp),
                strokeWidth = 8.dp,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                color = color,
                strokeCap = StrokeCap.Round
            )
            
            Text(
                text = "${normalizedScore.roundToInt()}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MoodIndicator(
    score: Float,
    label: String,
    maxValue: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val normalizedScore = (score / maxValue) * 100
        
        // Determine color based on score
        val color = when {
            normalizedScore < 40 -> MaterialTheme.colorScheme.error
            normalizedScore < 70 -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
        }
        
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)
        ) {
            CircularProgressIndicator(
                progress = score / maxValue,
                modifier = Modifier.size(80.dp),
                strokeWidth = 8.dp,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                color = color,
                strokeCap = StrokeCap.Round
            )
            
            Text(
                text = String.format("%.1f", score),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StressIndicator(
    score: Float,
    label: String,
    maxValue: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val normalizedScore = (score / maxValue) * 100
        
        // For stress, lower is better (inverse color scale)
        val color = when {
            normalizedScore > 80 -> MaterialTheme.colorScheme.error
            normalizedScore > 60 -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
        }
        
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)
        ) {
            CircularProgressIndicator(
                progress = score / maxValue,
                modifier = Modifier.size(80.dp),
                strokeWidth = 8.dp,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                color = color,
                strokeCap = StrokeCap.Round
            )
            
            Text(
                text = String.format("%.1f", score),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MetricItem(metric: WellbeingMetrics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (metric.isCritical()) 
                MaterialTheme.colorScheme.errorContainer 
            else if (metric.needsAttention())
                MaterialTheme.colorScheme.tertiaryContainer
            else 
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Data
                Text(
                    text = metric.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                // Indicador de crítico
                if (metric.isCritical()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Atenção necessária",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Métricas
            MetricProgressBar(
                label = "Bem-estar",
                value = metric.wellbeingScore,
                maxValue = 100f,
                isInverted = false
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            MetricProgressBar(
                label = "Humor",
                value = metric.averageMood,
                maxValue = 5f,
                isInverted = false
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            MetricProgressBar(
                label = "Estresse",
                value = metric.averageStress,
                maxValue = 5f,
                isInverted = true
            )
            
            // Exibir carga de trabalho e ambiente se disponíveis
            metric.workloadScore?.let {
                Spacer(modifier = Modifier.height(8.dp))
                MetricProgressBar(
                    label = "Carga de Trabalho",
                    value = it.toFloat(),
                    maxValue = 100f,
                    isInverted = true
                )
            }
            
            metric.environmentScore?.let {
                Spacer(modifier = Modifier.height(8.dp))
                MetricProgressBar(
                    label = "Ambiente",
                    value = it.toFloat(),
                    maxValue = 100f,
                    isInverted = false
                )
            }
        }
    }
}

@Composable
fun MetricProgressBar(
    label: String,
    value: Float,
    maxValue: Float,
    isInverted: Boolean
) {
    val progress = value / maxValue
    
    // Para métricas invertidas (como estresse), valores altos são ruins
    val color = if (isInverted) {
        when {
            progress > 0.8f -> MaterialTheme.colorScheme.error
            progress > 0.6f -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
        }
    } else {
        when {
            progress < 0.3f -> MaterialTheme.colorScheme.error
            progress < 0.7f -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
        }
    }
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = String.format("%.1f / %.0f", value, maxValue),
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun NoMetricsAvailable() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Nenhuma métrica disponível para o período selecionado.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
} 