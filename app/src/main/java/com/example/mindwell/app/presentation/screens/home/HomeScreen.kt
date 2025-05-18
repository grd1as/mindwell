package com.example.mindwell.app.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mindwell.app.R
import com.example.mindwell.app.common.navigation.AppDestinations
import com.example.mindwell.app.common.navigation.BottomNavigationBar
import com.example.mindwell.app.domain.entities.CheckIn
import com.example.mindwell.app.domain.entities.Reminder
import com.example.mindwell.app.domain.entities.WellbeingMetrics
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppDestinations.CHECK_IN) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.new_checkin)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Cabeçalho de boas-vindas
                    item {
                        Column {
                            Text(
                                text = stringResource(id = R.string.welcome_title),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Text(
                                text = "Seu bem-estar no trabalho é importante",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // Métricas de Bem-estar resumidas
                    item {
                        TodayWellbeingSummary(
                            wellbeingMetrics = uiState.todayWellbeing,
                            onCheckInClick = { navController.navigate(AppDestinations.CHECK_IN) }
                        )
                    }
                    
                    // Dicas personalizadas
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Dicas Personalizadas",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                IconButton(onClick = { viewModel.refreshHomeData() }) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Atualizar"
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (uiState.personalizedTips.isEmpty()) {
                                Text(
                                    text = "Sem dicas disponíveis no momento",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(vertical = 4.dp)
                                ) {
                                    items(uiState.personalizedTips) { tip ->
                                        TipCard(tip = tip)
                                    }
                                }
                            }
                        }
                    }
                    
                    // Check-ins recentes
                    item {
                        Column {
                            Text(
                                text = stringResource(id = R.string.recent_checkins),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (uiState.recentCheckIns.isEmpty()) {
                                Text(
                                    text = "Nenhum check-in recente. Faça seu primeiro check-in!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    uiState.recentCheckIns.forEach { checkIn ->
                                        CheckInItem(checkIn = checkIn)
                                    }
                                }
                            }
                        }
                    }
                    
                    // Avaliação pendente
                    item {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { navController.navigate(AppDestinations.ASSESSMENT) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.pending_assessments),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Avalie os riscos psicossociais do seu ambiente de trabalho",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
            
            // Mensagem de erro
            if (uiState.error != null) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = uiState.error ?: "Erro desconhecido",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TodayWellbeingSummary(
    wellbeingMetrics: WellbeingMetrics?,
    onCheckInClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Seu Bem-estar Hoje",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (wellbeingMetrics == null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Ainda não temos dados para hoje",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = onCheckInClick,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Fazer Check-in Agora")
                    }
                }
            } else {
                // Bem-estar geral
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bem-estar Geral",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Text(
                            text = "${wellbeingMetrics.wellbeingScore.toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    LinearProgressIndicator(
                        progress = wellbeingMetrics.wellbeingScore / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = when {
                            wellbeingMetrics.isCritical() -> MaterialTheme.colorScheme.error
                            wellbeingMetrics.needsAttention() -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Humor e estresse
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Humor
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Humor",
                            style = MaterialTheme.typography.bodySmall
                        )
                        
                        Text(
                            text = String.format("%.1f/5", wellbeingMetrics.averageMood),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // Estresse
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Estresse",
                            style = MaterialTheme.typography.bodySmall
                        )
                        
                        Text(
                            text = String.format("%.1f/5", wellbeingMetrics.averageStress),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (wellbeingMetrics.averageStress > 3.5f) 
                                MaterialTheme.colorScheme.error 
                            else 
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TipCard(tip: Reminder) {
    Card(
        modifier = Modifier.width(280.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = tip.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun CheckInItem(checkIn: CheckIn) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador visual do humor
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        when {
                            checkIn.moodLevel >= 4 -> MaterialTheme.colorScheme.primary
                            checkIn.moodLevel >= 3 -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Informações do check-in
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = checkIn.timestamp.toLocalDate().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = when (checkIn.moodLevel) {
                        5 -> "Muito Bem"
                        4 -> "Bem"
                        3 -> "Neutro"
                        2 -> "Mal"
                        1 -> "Muito Mal"
                        else -> "Desconhecido"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (checkIn.notes != null && checkIn.notes.isNotBlank()) {
                    Text(
                        text = checkIn.notes,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                    )
                }
            }
            
            // Nível de estresse
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Estresse",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "${checkIn.stressLevel}/5",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        checkIn.stressLevel > 3 -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}

@Composable
fun getMoodColor(moodLevel: Int): Color {
    return when (moodLevel) {
        1 -> Color(0xFFE53935) // Vermelho para muito ruim
        2 -> Color(0xFFEF6C00) // Laranja para ruim
        3 -> Color(0xFFFDD835) // Amarelo para neutro
        4 -> Color(0xFF43A047) // Verde para bom
        5 -> Color(0xFF1E88E5) // Azul para muito bom
        else -> Color.Gray
    }
} 