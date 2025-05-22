package com.example.mindwell.app.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mindwell.app.common.navigation.AppDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    nav: NavController,
    vm: HomeViewModel = hiltViewModel()
) {
    val state = vm.state
    var selectedEmotion by remember { mutableStateOf(-1) }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
                    label = { Text("Início") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { nav.navigate(AppDestinations.CHECK_IN) },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Check-in") },
                    label = { Text("Check-in") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { nav.navigate(AppDestinations.FORMS) },
                    icon = { Icon(Icons.Default.List, contentDescription = "Quest") },
                    label = { Text("Quest") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { nav.navigate(AppDestinations.RESOURCES) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Guias") },
                    label = { Text("Guias") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { nav.navigate(AppDestinations.EVOLUTION) },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Resultados") },
                    label = { Text("Resultados") }
                )
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with welcome message
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Bem-vindo(a)",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = state.userName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                    
                    // Profile avatar or settings
                    IconButton(
                        onClick = { nav.navigate(AppDestinations.SETTINGS) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Mood selection
                Text(
                    text = "Como você está se sentindo hoje?",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    EmotionItem(
                        emoji = "❤️", 
                        label = "Muito Mal", 
                        isSelected = selectedEmotion == 0,
                        onClick = { selectedEmotion = 0 }
                    )
                    EmotionItem(
                        emoji = "🧡", 
                        label = "Mal", 
                        isSelected = selectedEmotion == 1,
                        onClick = { selectedEmotion = 1 }
                    )
                    EmotionItem(
                        emoji = "💛", 
                        label = "Normal", 
                        isSelected = selectedEmotion == 2,
                        onClick = { selectedEmotion = 2 }
                    )
                    EmotionItem(
                        emoji = "💚", 
                        label = "Bom", 
                        isSelected = selectedEmotion == 3,
                        onClick = { selectedEmotion = 3 }
                    )
                    EmotionItem(
                        emoji = "💙", 
                        label = "Muito Bom", 
                        isSelected = selectedEmotion == 4,
                        onClick = { selectedEmotion = 4 }
                    )
                }
                
                // How do you feel today button
                OutlinedButton(
                    onClick = { nav.navigate(AppDestinations.CHECK_IN) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Como você se sente hoje?",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Nível 3 de 5",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Streak section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "3 dias seguidos de check-in! 🔥",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFFF9800)
                        )
                    }
                }
                
                // Tips section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Dicas",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFFF9800)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Faça um intervalo de 5 minutos a cada hora para esticar o corpo e descansar a mente.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                // Resource boxes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .clickable { nav.navigate(AppDestinations.RESOURCES) },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Exercícios de respiração",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .clickable { nav.navigate(AppDestinations.RESOURCES) },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Meditação guiada",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                // Available questionnaires
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Questionários disponíveis",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFFF9800)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (state.pendingForms > 0) {
                            Button(
                                onClick = { nav.navigate(AppDestinations.FORMS) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF9800)
                                )
                            ) {
                                Text("Ver ${state.pendingForms} formulários pendentes")
                            }
                        } else {
                            Text(
                                text = "Não há questionários disponíveis no momento.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                // Feedback channel
                Button(
                    onClick = { nav.navigate(AppDestinations.REPORT) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFFFF9800)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Text(
                        text = "Canal de escuta/feedback",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun EmotionItem(
    emoji: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                    shape = CircleShape
                )
                .clickable(onClick = onClick)
                .padding(8.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 18.sp
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            fontSize = 10.sp
        )
    }
} 