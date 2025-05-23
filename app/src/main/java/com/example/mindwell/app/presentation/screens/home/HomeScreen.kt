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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.SheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    // Configurar o NavController no ViewModel
    LaunchedEffect(nav) {
        vm.setNavController(nav)
    }
    
    // Atualizar a saudação sempre que a tela for mostrada
    LaunchedEffect(Unit) {
        // Este bloco executa na abertura da tela
        println("HomeScreen ativada - atualizando saudação")
        vm.refreshGreeting() // Novo método a ser adicionado
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
    
    // Obter lista de sentimentos do ViewModel
    val feelings = vm.getFeelingsList().map { it.label }
    
    // Mostrar o bottom sheet de feedback se necessário
    if (state.showFeedbackDialog) {
        FeedbackBottomSheet(
            categories = vm.feedbackCategories,
            selectedCategory = state.feedbackCategory,
            description = state.feedbackDescription,
            isSubmitting = state.isSubmittingFeedback,
            success = state.feedbackSuccess,
            errorMessage = state.feedbackError,
            onCategorySelected = { vm.updateFeedbackCategory(it) },
            onDescriptionChanged = { vm.updateFeedbackDescription(it) },
            onSubmit = { vm.submitFeedback() },
            onDismiss = { vm.hideFeedbackDialog() },
            bottomSheetState = bottomSheetState
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header com saudação baseada no horário
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.greeting,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = state.greetingEmoji,
                            style = MaterialTheme.typography.headlineMedium,
                            fontSize = 28.sp
                        )
                    }
                    
                    // Ícone de configurações
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
                        modifier = Modifier.padding(16.dp, 12.dp)
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
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Lista de dicas personalizadas em uma única linha
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.customTips.forEach { tip ->
                                CustomTipButton(
                                    tip = tip,
                                    onClick = { vm.navigateToResource(tip.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
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
 * Bottom sheet para envio de feedback/report (Canal de Escuta)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackBottomSheet(
    categories: List<String>,
    selectedCategory: String,
    description: String,
    isSubmitting: Boolean,
    success: Boolean,
    errorMessage: String?,
    onCategorySelected: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
    bottomSheetState: SheetState
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
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        modifier = Modifier
            .fillMaxHeight(0.9f),
        dragHandle = { 
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Divider(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabeçalho com ícone
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Canal de Escuta",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Descrição do que é o Canal de Escuta
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "O que é o Canal de Escuta?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Este é um espaço seguro para você relatar situações desconfortáveis, denunciar comportamentos inadequados ou sugerir melhorias no ambiente de trabalho.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Todas as informações são tratadas com confidencialidade e anonimato. Seu bem-estar é nossa prioridade.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
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
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Agradecemos sua contribuição para um ambiente melhor.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Seleção de categoria
                Text(
                    text = "Selecione uma categoria:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                
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
                    label = { Text("Descreva a situação") },
                    placeholder = { Text("Descreva a situação de forma clara e detalhada...") }
                )
                
                // Texto sobre anonimato
                Text(
                    text = "Sua identidade será protegida. Somente o departamento de RH terá acesso a esta informação.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
            
            // Aumentar o espaçamento no final para garantir que todos elementos sejam visíveis
            Spacer(modifier = Modifier.height(60.dp))
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
fun CustomTipButton(
    tip: HomeViewModel.CustomTip,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Título da dica
            Text(
                text = tip.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícone da dica
                Icon(
                    imageVector = when {
                        tip.id.contains("breathing") -> Icons.Default.Favorite
                        tip.id.contains("meditation") -> Icons.Default.Star
                        else -> Icons.Default.Favorite
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                // Ícone de seta
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Ver mais",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
} 