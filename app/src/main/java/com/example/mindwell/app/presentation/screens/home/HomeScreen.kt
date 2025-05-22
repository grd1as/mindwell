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
import androidx.compose.ui.window.Dialog
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
    
    // Mostrar o diálogo de feedback se necessário
    if (state.showFeedbackDialog) {
        FeedbackDialog(
            categories = vm.feedbackCategories,
            selectedCategory = state.feedbackCategory,
            description = state.feedbackDescription,
            isSubmitting = state.isSubmittingFeedback,
            success = state.feedbackSuccess,
            errorMessage = state.feedbackError,
            onCategorySelected = { vm.updateFeedbackCategory(it) },
            onDescriptionChanged = { vm.updateFeedbackDescription(it) },
            onSubmit = { vm.submitFeedback() },
            onDismiss = { vm.hideFeedbackDialog() }
        )
    }
    
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
                    onClick = { vm.showFeedbackDialog() },  // Atualizado para abrir o diálogo
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

/**
 * Diálogo para envio de feedback/report
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackDialog(
    categories: List<String>,
    selectedCategory: String,
    description: String,
    isSubmitting: Boolean,
    success: Boolean,
    errorMessage: String?,
    onCategorySelected: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val categoryLabels = mapOf(
        "ASSÉDIO_MORAL" to "Assédio Moral",
        "ASSÉDIO_SEXUAL" to "Assédio Sexual",
        "DISCRIMINAÇÃO_RACIAL" to "Discriminação Racial",
        "DISCRIMINAÇÃO_DE_GÊNERO" to "Discriminação de Gênero",
        "VIOLÊNCIA_FÍSICA" to "Violência Física",
        "VIOLÊNCIA_VERBAL" to "Violência Verbal",
        "CONFLITO_INTERPESSOAL" to "Conflito Interpessoal",
        "SAÚDE_E_SEGURANÇA" to "Assuntos de Saúde e Segurança",
        "INFRAESTRUTURA_INADEQUADA" to "Infraestrutura Inadequada",
        "EQUIPAMENTO_QUEBRADO" to "Equipamento Quebrado",
        "ERGONOMIA_INADEQUADA" to "Ergonomia Inadequada",
        "OUTRO" to "Outro"
    )
    
    var expanded by remember { mutableStateOf(false) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Canal de Escuta",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                if (success) {
                    // Mensagem de sucesso
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Sucesso",
                                tint = Color.Green,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Feedback enviado com sucesso!",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Seleção de categoria
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = categoryLabels[selectedCategory] ?: "Selecione uma categoria",
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            label = { Text("Categoria") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(categoryLabels[category] ?: category) },
                                    onClick = {
                                        onCategorySelected(category)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    // Campo de descrição
                    OutlinedTextField(
                        value = description,
                        onValueChange = onDescriptionChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        label = { Text("Descrição (opcional)") },
                        placeholder = { Text("Descreva a ocorrência...") }
                    )
                    
                    // Mensagem de erro, se houver
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    // Botões de ação
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismiss
                        ) {
                            Text("Cancelar")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = onSubmit,
                            enabled = !isSubmitting
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Enviar")
                            }
                        }
                    }
                }
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