package com.example.mindwell.app.presentation.screens.evolution

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mindwell.app.data.model.*
import com.example.mindwell.app.presentation.screens.evolution.components.MonthlySummaryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvolutionScreen(
    nav: NavController,
    vm: EvolutionViewModel = hiltViewModel()
) {
    val state = vm.state
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        if (state.is_loading) {
            ModernLoadingState()
        } else if (state.error != null) {
            ModernErrorState(state.error, onRetry = { vm.refresh() })
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header moderno com gradiente
                ModernEvolutionHeader(onBackClick = { nav.navigateUp() })
                
                // Conteúdo em cards modernos
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Seletor de mês
                    ModernMonthSelector(
                        monthLabel = vm.format_current_month(),
                        onPreviousMonth = { vm.previous_month() },
                        onNextMonth = { vm.next_month() }
                    )
                    
                    // Resumo mensal
                    state.monthly_summary?.let { summary ->
                        MonthlySummaryCard(
                            summary = summary,
                            get_emoji_from_option_id = vm::get_emoji_from_option_id,
                            get_trend_icon = vm::get_trend_icon,
                            format_workload_change = vm::format_workload_change,
                            get_workload_change_color = vm::get_workload_change_color
                        )
                    }
                    
                    // Gráfico de distribuição de humor (NOVO)
                    state.mood_distribution?.let { moodData ->
                        ModernMoodDistributionCard(moodData = moodData)
                    }
                    
                    // Timeline semanal
                    state.monthly_trend?.let { trend ->
                        ModernWeeklyMoodChart(
                            weeklyMood = trend.weeklyMood,
                            overallTrend = trend.overallTrend,
                            viewModel = vm
                        )
                    }
                    
                    // Alertas de carga de trabalho (NOVO)
                    state.workload_alerts?.let { workloadData ->
                        ModernWorkloadAlertsCard(workloadData = workloadData)
                    }
                    
                    // Diagnóstico de clima organizacional (NOVO)
                    state.climate_diagnosis?.let { climateData ->
                        ModernClimateDiagnosisCard(climateData = climateData)
                    }
                    
                    // Atividade combinada
                    state.monthly_trend?.let { trend ->
                        CombinedActivityCard(
                            dailySummary = trend.dailySummary,
                            viewModel = vm
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(72.dp)
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Text(
            text = "Oops! Algo deu errado",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        FilledTonalButton(
            onClick = onRetry,
            modifier = Modifier.height(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tentar novamente")
        }
    }
}

@Composable
private fun EvolutionContent(
    modifier: Modifier = Modifier,
    monthLabel: String,
    monthlyTrend: com.example.mindwell.app.data.model.MonthlyTrendDTO?,
    monthlySummary: com.example.mindwell.app.domain.entities.MonthlySummary?,
    viewModel: EvolutionViewModel,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    if (monthlyTrend == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Nenhum dado disponível",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Faça alguns check-ins para ver sua evolução",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Month selector
        ModernMonthSelector(
            monthLabel = monthLabel,
            onPreviousMonth = onPreviousMonth,
            onNextMonth = onNextMonth
        )
        
        // Monthly summary card - First chart as requested
        monthlySummary?.let { summary ->
            MonthlySummaryCard(
                summary = summary,
                get_emoji_from_option_id = viewModel::get_emoji_from_option_id,
                get_trend_icon = viewModel::get_trend_icon,
                format_workload_change = viewModel::format_workload_change,
                get_workload_change_color = viewModel::get_workload_change_color
            )
        }
        
        // Weekly mood chart with overall trend included
        ModernWeeklyMoodChart(
            weeklyMood = monthlyTrend.weeklyMood,
            overallTrend = monthlyTrend.overallTrend,
            viewModel = viewModel
        )
        
        // Daily summary chart with peak/low days combined
        CombinedActivityCard(
            dailySummary = monthlyTrend.dailySummary,
            viewModel = viewModel
        )
        
        // Espaço final
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun ModernMonthSelector(
    monthLabel: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF8FAFF),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onPreviousMonth() },
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF6366F1),
                                    Color(0xFF8B5CF6)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Mês anterior",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📅",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = monthLabel,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            }
            
            Card(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onNextMonth() },
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF6366F1),
                                    Color(0xFF8B5CF6)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Próximo mês",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernOverallTrendCard(
    trend: String,
    tip: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1F2937),
                        Color(0xFF374151)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = trend,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.95f),
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = tip,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun ModernWeeklyMoodChart(
    weeklyMood: List<WeeklyMoodDTO>,
    overallTrend: String,
    viewModel: EvolutionViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF10B981),
                                    Color(0xFF059669)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "Timeline Semanal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Suas emoções ao longo das semanas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (weeklyMood.isEmpty()) {
                EmptyStateMessage("Nenhum dado semanal disponível ainda")
            } else {
                ModernTimelineView(weeklyMood, viewModel)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Overall trend card (modern)
            ModernOverallTrendCard(
                trend = overallTrend,
                tip = viewModel.get_trend_tip()
            )
        }
    }
}

@Composable
private fun ModernTimelineView(
    weeklyMood: List<WeeklyMoodDTO>,
    viewModel: EvolutionViewModel
) {
    Column {
        // Timeline connector line
        if (weeklyMood.size > 1) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF10B981).copy(alpha = 0.3f),
                                Color(0xFF10B981),
                                Color(0xFF10B981),
                                Color(0xFF10B981).copy(alpha = 0.3f)
                            )
                        )
                    )
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Horizontal timeline items
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weeklyMood.forEachIndexed { index, week ->
                ModernTimelineItem(
                    weekNumber = index + 1,
                    weekData = week,
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ModernTimelineItem(
    weekNumber: Int,
    weekData: WeeklyMoodDTO,
    viewModel: EvolutionViewModel,
    modifier: Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Week indicator circle
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF10B981),
                            Color(0xFF059669)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "S$weekNumber",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Content card - Only emoji now
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Single main emoji - centered
                Text(
                    text = viewModel.get_emoji_from_option_id(weekData.predominantEmoji.optionId),
                    fontSize = 36.sp
                )
            }
        }
    }
}

@Composable
private fun CombinedActivityCard(
    dailySummary: com.example.mindwell.app.data.model.DailySummaryDTO,
    viewModel: EvolutionViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF3B82F6),
                                    Color(0xFF1D4ED8)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "Atividade Semanal",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Check-ins por dia da semana",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Compact horizontal mini chart
            val maxTotal = dailySummary.weekdayTotals.maxOfOrNull { it.total } ?: 1
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                dailySummary.weekdayTotals.forEach { weekdayTotal ->
                    ModernDayColumn(
                        weekdayTotal = weekdayTotal,
                        maxTotal = maxTotal,
                        viewModel = viewModel,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Peak and Low Days Info
            Column {
                // Peak days
                if (dailySummary.peakWeekdays.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Mais ativos: ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = dailySummary.peakWeekdays.joinToString(", ") { 
                                viewModel.get_weekday_name(it) 
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    if (dailySummary.lowWeekdays.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                // Low days
                if (dailySummary.lowWeekdays.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Menos ativos: ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = dailySummary.lowWeekdays.joinToString(", ") { 
                                viewModel.get_weekday_name(it) 
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernDayColumn(
    weekdayTotal: WeekdayTotalDTO,
    maxTotal: Int,
    viewModel: EvolutionViewModel,
    modifier: Modifier
) {
    val heightPercentage = if (maxTotal > 0) weekdayTotal.total.toFloat() / maxTotal else 0f
    val barHeight = (heightPercentage * 40).dp.coerceAtLeast(4.dp)
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Value label
        Text(
            text = if (weekdayTotal.total > 0) weekdayTotal.total.toString() else "",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (weekdayTotal.total > 0) MaterialTheme.colorScheme.primary else Color.Transparent,
            modifier = Modifier.height(16.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Bar
        Box(
            modifier = Modifier
                .width(20.dp)
                .height(barHeight)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(
                    if (weekdayTotal.total > 0) {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF60A5FA),
                                Color(0xFF3B82F6)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        )
                    }
                )
        )
        
        Spacer(modifier = Modifier.height(6.dp))
        
        // Day label
        Text(
            text = viewModel.get_weekday_name(weekdayTotal.weekday).take(3),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EmptyStateMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ModernLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF6366F1),
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Analisando seus dados...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "Carregando insights personalizados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

@Composable
private fun ModernErrorState(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📊",
                    fontSize = 48.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Ops! Algo deu errado",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
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
                                    colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tentar novamente",
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

@Composable
private fun ModernEvolutionHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6366F1),
                        Color(0xFF8B5CF6),
                        Color(0xFFF8FAFF)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Barra de navegação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onBackClick() },
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
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Conteúdo do header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícone emoji
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📈",
                        fontSize = 28.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "Evolução Pessoal",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Sua jornada de bem-estar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ModernMoodDistributionCard(moodData: MoodDistributionDTO) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
            // Header
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
                                    Color(0xFF1D4ED8)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "😊",
                        fontSize = 20.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "Distribuição de Humor",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = "Como você se sentiu em ${moodData.period}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Gráficos das questões
            moodData.questions.forEach { question ->
                MoodQuestionChart(question = question)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun MoodQuestionChart(question: MoodQuestionDTO) {
    Column {
        Text(
            text = question.text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "${question.totalResponses} respostas",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF666666)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Barra de percentual para cada opção
        question.options.forEach { option ->
            MoodOptionBar(option = option)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun MoodOptionBar(option: MoodOptionDTO) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Emoji da opção
        Text(
            text = getEmojiFromOptionId(option.optionId),
            fontSize = 16.sp,
            modifier = Modifier.width(24.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Nome da opção
        Text(
            text = option.label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.width(80.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Barra de progresso
        Box(
            modifier = Modifier
                .weight(1f)
                .height(12.dp)
                .background(
                    color = Color(0xFFE5E7EB),
                    shape = RoundedCornerShape(6.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = (option.percent / 100).toFloat())
                    .background(
                        color = getColorFromLevel(option.level),
                        shape = RoundedCornerShape(6.dp)
                    )
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Percentual
        Text(
            text = "${option.percent.toInt()}%",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.width(32.dp)
        )
    }
}

@Composable
private fun ModernWorkloadAlertsCard(workloadData: WorkloadAlertsDTO) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFEF3F2),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFEF4444),
                                    Color(0xFFDC2626)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚠️",
                        fontSize = 18.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "Alertas de Carga de Trabalho",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = "Últimos ${workloadData.months.size} meses",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Timeline dos meses
            workloadData.months.forEach { month ->
                WorkloadMonthItem(month = month)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun WorkloadMonthItem(month: WorkloadMonthDTO) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Período
        Text(
            text = month.period,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.width(80.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Carga média
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Carga: ${String.format("%.1f", month.workloadAvg)}/5",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        color = Color(0xFFE5E7EB),
                        shape = RoundedCornerShape(3.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = (month.workloadAvg / 5).toFloat())
                        .background(
                            color = getWorkloadColor(month.workloadAvg),
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Alertas
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (month.alertCount > 0) Color(0xFFEF4444) else Color(0xFF10B981)
            )
        ) {
            Text(
                text = "${month.alertCount}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun ModernClimateDiagnosisCard(climateData: ClimateDiagnosisDTO) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF0FDF4),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF10B981),
                                    Color(0xFF059669)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏢",
                        fontSize = 18.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "Clima Organizacional",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = "Diagnóstico de ${climateData.period}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Dimensões
            climateData.dimensions.forEach { dimension ->
                ClimateDimensionItem(dimension = dimension)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ClimateDimensionItem(dimension: ClimateDimensionDTO) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nome da dimensão
        Text(
            text = dimension.name.capitalize(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.width(100.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Score visual
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${String.format("%.1f", dimension.score)}/5.0",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        color = Color(0xFFE5E7EB),
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = (dimension.score / 5).toFloat())
                        .background(
                            color = getClimateStatusColor(dimension.status),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Status
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = getClimateStatusColor(dimension.status).copy(alpha = 0.1f)
            )
        ) {
            Text(
                text = dimension.status,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = getClimateStatusColor(dimension.status),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

// Funções auxiliares para cores e emojis
private fun getEmojiFromOptionId(optionId: Int): String {
    return when (optionId) {
        1 -> "😢" // TRISTE
        2 -> "😊" // ALEGRE
        3 -> "😴" // CANSADO
        4 -> "😰" // ANSIOSO
        5 -> "😨" // MEDO
        6 -> "😡" // RAIVA
        else -> "😐" // NEUTRO
    }
}

private fun getColorFromLevel(level: String): Color {
    return when (level.lowercase()) {
        "baixo" -> Color(0xFF10B981)
        "moderado" -> Color(0xFFEAB308)
        "alto" -> Color(0xFFEF4444)
        else -> Color(0xFF6B7280)
    }
}

private fun getWorkloadColor(workload: Double): Color {
    return when {
        workload <= 2.0 -> Color(0xFF10B981)
        workload <= 3.5 -> Color(0xFFEAB308)
        else -> Color(0xFFEF4444)
    }
}

private fun getClimateStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "saudável" -> Color(0xFF10B981)
        "atenção" -> Color(0xFFEAB308)
        "alerta" -> Color(0xFFEF4444)
        else -> Color(0xFF6B7280)
    }
} 