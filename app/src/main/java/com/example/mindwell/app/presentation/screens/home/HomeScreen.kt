package com.example.mindwell.app.presentation.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mindwell.app.common.navigation.AppDestinations
import com.example.mindwell.app.presentation.screens.home.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    
    // Animações
    val fabScale by animateFloatAsState(
        targetValue = if (showSubmitSuccess) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "FabScale"
    )
    
    // Configurar o NavController no ViewModel
    LaunchedEffect(nav) {
        vm.setNavController(nav)
    }
    
    // Atualizar a saudação sempre que a tela for mostrada
    LaunchedEffect(Unit) {
        vm.refreshGreeting()
        vm.checkReminder() // Verificar se deve mostrar lembrete
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
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp,
                    color = Color(0xFF6366F1)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header moderno até o topo (safe area)
                ModernHeader(
                    greeting = state.greeting,
                    greetingEmoji = state.greetingEmoji,
                    onSettingsClick = { nav.navigate(AppDestinations.SETTINGS) },
                    onTooltipRequest = { vm.showTooltip("settings") },
                            showTooltip = state.activeTooltip == "settings",
                    onDismissTooltip = { vm.hideTooltip() }
                )
                
                // Conteúdo com padding lateral
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Check-in rápido ou Card de Cooldown
                    if (state.hasCheckedInToday || !state.canCheckinNow) {
                        // Mostrar card de cooldown se já fez check-in ou está em cooldown
                        CheckinCooldownCard(
                            timeRemaining = state.timeUntilNextCheckin,
                            hasCheckedInToday = state.hasCheckedInToday,
                            onRefresh = { vm.refreshCheckinStatus() }
                        )
                    } else {
                        // Mostrar check-in normal se pode fazer check-in
                        ModernQuickCheckin(
                            selectedEmoji = selectedEmoji,
                            selectedFeeling = selectedFeeling,
                            feelings = feelings,
                            showFeelingDropdown = showFeelingDropdown,
                            showSubmitSuccess = showSubmitSuccess,
                            onEmojiSelected = { selectedEmoji = it },
                            onFeelingSelected = { selectedFeeling = it },
                            onDropdownToggle = { showFeelingDropdown = it },
                            onSubmit = { 
                                vm.submitCheckin(selectedEmoji, selectedFeeling)
                                showSubmitSuccess = true
                                coroutineScope.launch {
                                    delay(2000)
                                    showSubmitSuccess = false
                                    // Atualizar status após check-in
                                    vm.refreshCheckinStatus()
                                }
                            },
                            onTooltipRequest = { tooltipId -> vm.showTooltip(tooltipId) },
                            activeTooltip = state.activeTooltip,
                            onDismissTooltip = { vm.hideTooltip() }
                        )
                    }
                    
                    // Progress semanal moderno
                    ModernWeeklyProgress(
                        weeklyData = state.weeklyCheckins,
                        onTooltipRequest = { tooltipId -> vm.showTooltip(tooltipId) },
                        activeTooltip = state.activeTooltip,
                        onDismissTooltip = { vm.hideTooltip() }
                    )
                    
                    // Dicas personalizadas modernas
                    ModernPersonalizedTips(
                        tips = state.customTips,
                        isLoading = state.isLoadingTips,
                        onTipClick = { nav.navigate(AppDestinations.RESOURCES) },
                        onRefresh = { vm.refreshPersonalizedTips() },
                        onTooltipRequest = { tooltipId -> vm.showTooltip(tooltipId) },
                        activeTooltip = state.activeTooltip,
                        onDismissTooltip = { vm.hideTooltip() }
                    )
                    
                    // Questionários modernos
                    ModernQuestionnaires(
                        forms = state.availableForms,
                        onQuestionnaireClick = { form -> vm.startQuestionnaire(form.code, form.id) },
                        onTooltipRequest = { vm.showTooltip(it) },
                        activeTooltip = state.activeTooltip,
                        onDismissTooltip = { vm.hideTooltip() }
                    )
                    
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
        
        // FloatingActionButton para relatório no canto
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { vm.selectReportForm("REPORT") },
                modifier = Modifier
                    .scale(fabScale)
                    .combinedClickable(
                        onClick = { vm.selectReportForm("REPORT") },
                        onLongClick = { vm.showTooltip("report_fab") }
                    ),
                containerColor = Color(0xFFDC2626),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 12.dp,
                    pressedElevation = 16.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning, 
                    contentDescription = "Enviar relatório",
                    modifier = Modifier.size(28.dp)
                )
            }
            
            if (state.activeTooltip == "report_fab") {
                Tooltip(
                    tooltipText = "Envie um relatório ou denúncia anônima",
                    showTooltip = true,
                    onDismiss = { vm.hideTooltip() }
                )
            }
        }
    }
} 