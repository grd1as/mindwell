package com.example.mindwell.app.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.platform.LocalDensity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mindwell.app.common.navigation.AppDestinations
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Tooltip(
    modifier: Modifier = Modifier,
    tooltipText: String,
    showTooltip: Boolean,
    onDismiss: () -> Unit
) {
    if (!showTooltip) return
    
    Popup(
        alignment = Alignment.Center,
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = modifier
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
            shadowElevation = 4.dp
        ) {
            Text(
                text = tooltipText,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

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
    var showSubmitSuccess by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    // Configurar o NavController no ViewModel
    LaunchedEffect(nav) {
        vm.setNavController(nav)
    }
    
    // Observar eventos de navegação
    state.navigationEvent?.let { event ->
        when (event) {
            is HomeViewModel.NavigationEvent.ToForm -> {
                LaunchedEffect(event) {
                    nav.navigate(AppDestinations.formDetail(event.formId))
                    vm.handleNavigationEvent()
                }
            }
            is HomeViewModel.NavigationEvent.ToForms -> {
                LaunchedEffect(event) {
                    nav.navigate(AppDestinations.FORMS)
                    vm.handleNavigationEvent()
                }
            }
            is HomeViewModel.NavigationEvent.ToResource -> {
                LaunchedEffect(event) {
                    nav.navigate(AppDestinations.resourceDetail(event.resourceId))
                    vm.handleNavigationEvent()
                }
            }
            is HomeViewModel.NavigationEvent.Handled -> {
                // Já tratado
            }
        }
    }
    
    // Lista de sentimentos para o dropdown atualizada
    val feelings = listOf("Motivado", "Cansado", "Preocupado", "Estressado", "Animado", "Satisfeito")
    
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
            Box {
                FloatingActionButton(
                    onClick = { vm.selectReportForm("REPORT") },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.combinedClickable(
                        onClick = { vm.selectReportForm("REPORT") },
                        onLongClick = { vm.showTooltip("report_fab") }
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning, 
                        contentDescription = "Enviar relatório"
                    )
                }
                
                Tooltip(
                    tooltipText = "Envie um relatório ou denúncia anônima",
                    showTooltip = state.activeTooltip == "report_fab",
                    onDismiss = { vm.hideTooltip() }
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
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header simplificado - sem nome do usuário
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MindWell",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Ícone de configurações (sem dados do perfil)
                    Box {
                        IconButton(
                            onClick = { nav.navigate(AppDestinations.SETTINGS) },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .combinedClickable(
                                    onClick = { nav.navigate(AppDestinations.SETTINGS) },
                                    onLongClick = { vm.showTooltip("settings") }
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Configurações",
                                tint = Color.White
                            )
                        }
                        
                        Tooltip(
                            tooltipText = "Acesse as configurações do aplicativo",
                            showTooltip = state.activeTooltip == "settings",
                            onDismiss = { vm.hideTooltip() }
                        )
                    }
                }
                
                // Texto introdutório
                Text(
                    text = "Vamos ajudar a cuidar da sua saúde mental hoje.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // First question - Emoji selection
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Escolha o seu emoji de hoje!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(
                        onClick = { vm.showTooltip("emoji_help") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Ajuda",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    if (state.activeTooltip == "emoji_help") {
                        Tooltip(
                            tooltipText = "Escolha o emoji que melhor representa como você está se sentindo agora",
                            showTooltip = true,
                            onDismiss = { vm.hideTooltip() }
                        )
                    }
                }
                
                // Emojis em uma única linha
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
                        onClick = { selectedEmoji = "😢" },
                        modifier = Modifier.size(48.dp)
                    )
                    EmojiOption(
                        emoji = "😊",
                        label = "Alegre",
                        isSelected = selectedEmoji == "😊",
                        onClick = { selectedEmoji = "😊" },
                        modifier = Modifier.size(48.dp)
                    )
                    EmojiOption(
                        emoji = "😴",
                        label = "Cansado",
                        isSelected = selectedEmoji == "😴",
                        onClick = { selectedEmoji = "😴" },
                        modifier = Modifier.size(48.dp)
                    )
                    EmojiOption(
                        emoji = "😰",
                        label = "Ansioso",
                        isSelected = selectedEmoji == "😰",
                        onClick = { selectedEmoji = "😰" },
                        modifier = Modifier.size(48.dp)
                    )
                    EmojiOption(
                        emoji = "😨",
                        label = "Medo",
                        isSelected = selectedEmoji == "😨",
                        onClick = { selectedEmoji = "😨" },
                        modifier = Modifier.size(48.dp)
                    )
                    EmojiOption(
                        emoji = "😡",
                        label = "Raiva",
                        isSelected = selectedEmoji == "😡",
                        onClick = { selectedEmoji = "😡" },
                        modifier = Modifier.size(48.dp)
                    )
                }
                
                // Second question - Feeling dropdown
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Como você se sente hoje?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(
                        onClick = { vm.showTooltip("feeling_help") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Ajuda",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    if (state.activeTooltip == "feeling_help") {
                        Tooltip(
                            tooltipText = "Selecione a opção que melhor descreve seu estado emocional atual",
                            showTooltip = true,
                            onDismiss = { vm.hideTooltip() }
                        )
                    }
                }
                
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
                
                // Submit button com feedback visual
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Button(
                        onClick = { 
                            vm.submitCheckin(selectedEmoji, selectedFeeling)
                            showSubmitSuccess = true
                            coroutineScope.launch {
                                delay(2000)
                                showSubmitSuccess = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedEmoji.isNotEmpty() && selectedFeeling.isNotEmpty()
                    ) {
                        if (showSubmitSuccess) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Sucesso",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Enviado com sucesso!")
                            }
                        } else {
                            Text("Enviar Check-in")
                        }
                    }
                }
                
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
                
                // Dicas personalizadas
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE3F2FD)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Dicas personalizadas para você",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF1976D2)
                            )
                            
                            IconButton(
                                onClick = { vm.showTooltip("custom_tips_help") }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Ajuda",
                                    tint = Color(0xFF1976D2),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        
                        if (state.activeTooltip == "custom_tips_help") {
                            Tooltip(
                                tooltipText = "Dicas selecionadas especialmente para o seu perfil. Toque para ver mais detalhes.",
                                showTooltip = true,
                                onDismiss = { vm.hideTooltip() }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Lista de dicas personalizadas
                        state.customTips.forEach { tip ->
                            CustomTipItem(
                                tip = tip,
                                onClick = { vm.navigateToResource(tip.id) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Questionários disponíveis",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFFFF9800)
                            )
                            
                            IconButton(
                                onClick = { vm.showTooltip("questionnaires_help") }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Ajuda",
                                    tint = Color(0xFFFF9800),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        
                        if (state.activeTooltip == "questionnaires_help") {
                            Tooltip(
                                tooltipText = "Responda estes questionários para nos ajudar a avaliar seu bem-estar",
                                showTooltip = true,
                                onDismiss = { vm.hideTooltip() }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Self Assessment questionnaire
                        QuestionnaireItem(
                            title = "Auto-avaliação",
                            description = "Avalie como você está se sentindo hoje",
                            code = "SELF_ASSESS",
                            onClick = { vm.startQuestionnaire("SELF_ASSESS") },
                            viewModel = vm
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Climate questionnaire
                        QuestionnaireItem(
                            title = "Clima organizacional",
                            description = "Avalie o ambiente de trabalho",
                            code = "CLIMATE",
                            onClick = { vm.startQuestionnaire("CLIMATE") },
                            viewModel = vm
                        )
                    }
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Box(
            modifier = modifier
                .clip(CircleShape)
                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                .border(
                    width = 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                    shape = CircleShape
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 18.sp
            )
        }
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            fontSize = 10.sp
        )
    }
}

@Composable
fun QuestionnaireItem(
    title: String,
    description: String,
    code: String,
    onClick: () -> Unit,
    viewModel: HomeViewModel
) {
    val tooltipId = "questionnaire_${code.lowercase()}"
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { viewModel.showTooltip(tooltipId) }
            ),
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
        
        if (viewModel.state.activeTooltip == tooltipId) {
            Tooltip(
                tooltipText = "Toque para responder o questionário '$title'",
                showTooltip = true,
                onDismiss = { viewModel.hideTooltip() }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Modifier.combinedClickable(
    onClick: () -> Unit,
    onLongClick: () -> Unit
) = this.clickable(onClick = onClick)
    .pointerInput(Unit) {
        detectTapGestures(
            onLongPress = { onLongClick() },
            onTap = { onClick() }
        )
    }

@Composable
fun CustomTipItem(
    tip: HomeViewModel.CustomTip,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone da dica
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        tip.id.contains("breathing") -> Icons.Default.Favorite
                        tip.id.contains("meditation") -> Icons.Default.Star
                        else -> Icons.Default.Favorite
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // Conteúdo da dica
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Text(
                    text = tip.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Ícone de seta
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ver mais",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
} 