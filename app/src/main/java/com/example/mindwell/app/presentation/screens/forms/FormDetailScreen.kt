package com.example.mindwell.app.presentation.screens.forms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mindwell.app.common.navigation.AppDestinations
import com.example.mindwell.app.domain.entities.Option
import com.example.mindwell.app.domain.entities.Question

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDetailScreen(
    nav: NavController,
    vm: FormDetailViewModel = hiltViewModel()
) {
    val state = vm.state
    val currentQuestion = vm.getCurrentQuestion()
    
    // Redirecionar para a tela Home após sucesso
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            nav.navigate(AppDestinations.HOME) {
                popUpTo(AppDestinations.FORMS)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        state.formDetail?.name ?: "Carregando...",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { nav.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1A1A1A)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFF))
        ) {
            if (state.isLoading) {
                FormDetailLoadingState()
            } else if (state.error != null) {
                FormDetailErrorState(
                    error = state.error,
                    onRetry = { vm.reloadFormDetail() }
                )
            } else if (currentQuestion != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Header moderno com progresso
                    ModernProgressHeader(
                        currentQuestion = state.currentQuestionIndex + 1,
                        totalQuestions = state.formDetail?.questions?.size ?: 1,
                        formName = state.formDetail?.name ?: ""
                    )
                    
                    // Conteúdo da pergunta
                    ModernQuestionContent(
                        question = currentQuestion,
                        selectedOptionId = state.answers[currentQuestion.id]?.option_id,
                        onOptionSelected = { optionId ->
                            vm.answerQuestion(currentQuestion, optionId)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Botões de navegação modernos
                    ModernNavigationButtons(
                        canGoBack = state.currentQuestionIndex > 0,
                        canAdvance = vm.canAdvance(),
                        canSubmit = vm.canSubmit(),
                        isSubmitting = state.isSubmitting,
                        onPrevious = { vm.previousQuestion() },
                        onNext = { vm.nextQuestion() },
                        onSubmit = { vm.submitForm() }
                    )
                }
            }
        }
    }
}

@Composable
fun ModernProgressHeader(
    currentQuestion: Int,
    totalQuestions: Int,
    formName: String
) {
    val progress by animateFloatAsState(
        targetValue = currentQuestion.toFloat() / totalQuestions.toFloat(),
        animationSpec = tween(durationMillis = 300)
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF6366F1),
                            Color(0xFF8B5CF6)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📝 $formName",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Pergunta $currentQuestion de $totalQuestions",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    
                    // Círculo de progresso
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Barra de progresso moderna
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(
                            Color.White.copy(alpha = 0.3f),
                            RoundedCornerShape(3.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(
                                Color.White,
                                RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun ModernQuestionContent(
    question: Question,
    selectedOptionId: Int?,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Pergunta com emoji
            Text(
                text = "❓ ${question.text}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Center,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Opções de resposta
            question.options.forEachIndexed { index, option ->
                ModernOptionItem(
                    option = option,
                    isSelected = selectedOptionId == option.id,
                    onClick = { onOptionSelected(option.id) },
                    index = index
                )
            }
        }
    }
}

@Composable
fun ModernOptionItem(
    option: Option,
    isSelected: Boolean,
    onClick: () -> Unit,
    index: Int
) {
    val borderColor = if (isSelected) Color(0xFF6366F1) else Color(0xFFE0E0E0)
    val backgroundColor = if (isSelected) Color(0xFF6366F1).copy(alpha = 0.1f) else Color.White
    
    // Descrições para cada nível
    val descriptions = mapOf(
        "1" to "Muito ruim - Precisa de atenção urgente",
        "2" to "Ruim - Existem problemas significativos",
        "3" to "Regular - Tem espaço para melhorias",
        "4" to "Bom - Geralmente positivo",
        "5" to "Excelente - Muito satisfeito"
    )
    
    // Emojis para cada nível
    val levelEmojis = mapOf(
        "1" to "😢",
        "2" to "😕",
        "3" to "😐",
        "4" to "🙂",
        "5" to "😊"
    )
    
    // Pegar o emoji e descrição baseado no valor da opção
    val optionEmoji = levelEmojis[option.value] ?: "❓"
    val description = descriptions[option.value]
    
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            initialOffsetY = { 30 },
            animationSpec = tween(200, delayMillis = index * 30)
        ) + fadeIn(animationSpec = tween(200, delayMillis = index * 30))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .selectable(
                    selected = isSelected,
                    onClick = onClick,
                    role = Role.RadioButton
                ),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            border = BorderStroke(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected) 4.dp else 1.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Emoji do nível
                    Text(
                        text = optionEmoji,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // Número/Nível
                        Text(
                            text = "Nível ${option.value}",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = if (isSelected) Color(0xFF6366F1) else Color(0xFF1A1A1A)
                        )
                        
                        // Descrição do nível
                        description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp
                                ),
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                    
                    // Indicador de seleção
                    if (isSelected) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Selecionado",
                            tint = Color(0xFF6366F1),
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .border(
                                    1.dp,
                                    Color(0xFFE0E0E0),
                                    CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernNavigationButtons(
    canGoBack: Boolean,
    canAdvance: Boolean,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Botão Anterior
            OutlinedButton(
                onClick = onPrevious,
                enabled = canGoBack,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF6366F1)
                ),
                border = BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = if (canGoBack) 1f else 0.3f))
            ) {
                Text(
                    "Anterior",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Botão Próxima/Enviar
            Button(
                onClick = if (canSubmit) onSubmit else onNext,
                enabled = if (canSubmit) !isSubmitting else canAdvance,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canSubmit) Color(0xFF10B981) else Color(0xFF6366F1)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else if (canSubmit) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Enviar",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                } else {
                    Text(
                        "Próxima",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// Estados específicos para FormDetail (com nomes únicos)
@Composable
fun FormDetailLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = Color(0xFF6366F1),
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Carregando questionário...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

@Composable
fun FormDetailErrorState(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "⚠️",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Oops! Algo deu errado",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6366F1)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Tentar Novamente")
                }
            }
        }
    }
} 