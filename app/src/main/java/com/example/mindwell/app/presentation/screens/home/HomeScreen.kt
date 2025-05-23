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
    var selectedEmoji by remember { mutableStateOf("") }
    var selectedFeeling by remember { mutableStateOf("") }
    var showFeelingDropdown by remember { mutableStateOf(false) }
    
    // Lista de sentimentos para o dropdown
    val feelings = listOf("Muito bem", "Bem", "Normal", "Mal", "Muito mal", "Não sei dizer")
    
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
        // FloatingActionButton para relatório
        floatingActionButton = {
            FloatingActionButton(
                onClick = { vm.selectReportForm("REPORT") },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Warning, 
                    contentDescription = "Enviar relatório"
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
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // First question - Emoji selection
                Text(
                    text = "Escolha o seu emoji de hoje!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // First row of emojis
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    EmojiOption(
                        emoji = "😢",
                        label = "Triste",
                        isSelected = selectedEmoji == "😢",
                        onClick = { selectedEmoji = "😢" }
                    )
                    EmojiOption(
                        emoji = "😊",
                        label = "Alegre",
                        isSelected = selectedEmoji == "😊",
                        onClick = { selectedEmoji = "😊" }
                    )
                    EmojiOption(
                        emoji = "😴",
                        label = "Cansado",
                        isSelected = selectedEmoji == "😴",
                        onClick = { selectedEmoji = "😴" }
                    )
                }
                
                // Second row of emojis
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    EmojiOption(
                        emoji = "😰",
                        label = "Ansioso",
                        isSelected = selectedEmoji == "😰",
                        onClick = { selectedEmoji = "😰" }
                    )
                    EmojiOption(
                        emoji = "😨",
                        label = "Medo",
                        isSelected = selectedEmoji == "😨",
                        onClick = { selectedEmoji = "😨" }
                    )
                    EmojiOption(
                        emoji = "😡",
                        label = "Raiva",
                        isSelected = selectedEmoji == "😡",
                        onClick = { selectedEmoji = "😡" }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Second question - Feeling dropdown
                Text(
                    text = "Como você se sente hoje?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Dropdown para selecionar o sentimento
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showFeelingDropdown = true },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedFeeling.ifEmpty { "Selecione como você se sente" },
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Expandir"
                            )
                        }
                    }
                    
                    DropdownMenu(
                        expanded = showFeelingDropdown,
                        onDismissRequest = { showFeelingDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        feelings.forEach { feeling ->
                            DropdownMenuItem(
                                text = { Text(feeling) },
                                onClick = {
                                    selectedFeeling = feeling
                                    showFeelingDropdown = false
                                }
                            )
                        }
                    }
                }
                
                // Submit button
                Button(
                    onClick = { vm.submitCheckin(selectedEmoji, selectedFeeling) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    enabled = selectedEmoji.isNotEmpty() && selectedFeeling.isNotEmpty()
                ) {
                    Text("Enviar Check-in")
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
                        
                        // Self Assessment questionnaire
                        QuestionnaireItem(
                            title = "Auto-avaliação",
                            description = "Avalie como você está se sentindo hoje",
                            code = "SELF_ASSESS",
                            onClick = { vm.startQuestionnaire("SELF_ASSESS") }
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Climate questionnaire
                        QuestionnaireItem(
                            title = "Clima organizacional",
                            description = "Avalie o ambiente de trabalho",
                            code = "CLIMATE",
                            onClick = { vm.startQuestionnaire("CLIMATE") }
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
                
                // Feedback channel
                Button(
                    onClick = { vm.showFeedbackDialog() },
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
                
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun EmojiOption(
    emoji: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                .border(
                    width = 2.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                    shape = CircleShape
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 24.sp
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
fun QuestionnaireItem(
    title: String,
    description: String,
    code: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall
                )
                
                Text(
                    text = "Código: $code",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
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