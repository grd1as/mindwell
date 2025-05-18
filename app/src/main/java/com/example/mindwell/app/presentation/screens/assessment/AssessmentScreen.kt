package com.example.mindwell.app.presentation.screens.assessment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mindwell.app.common.navigation.AppDestinations
import com.example.mindwell.app.common.navigation.BottomNavigationBar
import com.example.mindwell.app.domain.entities.AssessmentQuestion
import com.example.mindwell.app.domain.entities.AssessmentType
import com.example.mindwell.app.domain.entities.QuestionCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentScreen(
    navController: NavController,
    viewModel: AssessmentViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    
    // Mostrar mensagens de erro
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(message = error)
        }
    }
    
    // Navegar de volta quando a avaliação for concluída
    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            navController.navigate(AppDestinations.HOME) {
                popUpTo(AppDestinations.ASSESSMENT) { inclusive = true }
            }
        }
    }
    
    // Safely extract assessment and questions
    val assessment = uiState.assessment
    val questions = assessment?.questions ?: emptyList()
    val currentIndex = uiState.currentQuestionIndex
    val currentQuestion = questions.getOrNull(currentIndex)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Avaliação Psicossocial") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Progresso
            if (questions.isNotEmpty()) {
                LinearProgressIndicator(
                    progress = uiState.progressPercent / 100f,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "$currentIndex/${questions.size} questões respondidas",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Seleção de tipo de avaliação
            Text(
                text = "Categoria de avaliação:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuestionCategoryChip(
                    category = QuestionCategory.WORKLOAD,
                    label = "Carga",
                    selected = true,
                    onClick = { }
                )
                
                QuestionCategoryChip(
                    category = QuestionCategory.SUPPORT,
                    label = "Suporte",
                    selected = false,
                    onClick = { }
                )
                
                QuestionCategoryChip(
                    category = QuestionCategory.WORK_LIFE_BALANCE,
                    label = "Equilíbrio",
                    selected = false,
                    onClick = { }
                )
            }
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Questionário atual            
            if (currentQuestion == null) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                QuestionItem(
                    question = currentQuestion,
                    selectedResponse = currentQuestion.answer as? Int,
                    onResponseSelected = { response ->
                        viewModel.answerCurrentQuestion(response)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Opções de navegação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentQuestion != null && !currentQuestion.required) {
                    Button(
                        onClick = { viewModel.skipCurrentQuestion() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pular")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                Button(
                    onClick = { viewModel.restartAssessment() },
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isSubmitting
                ) {
                    Text("Reiniciar")
                }
            }
            
            // Mostrar recomendações após completar
            if (uiState.isComplete && uiState.recommendationAfterCompletion.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Recomendações",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        uiState.recommendationAfterCompletion.forEach { recommendation ->
                            Text(
                                text = "• $recommendation",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionCategoryChip(
    category: QuestionCategory,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else null
    )
}

@Composable
fun QuestionItem(
    question: AssessmentQuestion,
    selectedResponse: Int?,
    onResponseSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = question.text,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (response in 1..5) {
                    ResponseOption(
                        value = response,
                        isSelected = selectedResponse == response,
                        onClick = { onResponseSelected(response) }
                    )
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Discordo\nTotalmente",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.weight(3f))
                
                Text(
                    text = "Concordo\nTotalmente",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ResponseOption(
    value: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = when (value) {
        1 -> Color(0xFFE53935)
        2 -> Color(0xFFEF6C00)
        3 -> Color(0xFFFDD835)
        4 -> Color(0xFF43A047)
        5 -> Color(0xFF1E88E5)
        else -> Color.Gray
    }
    
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (isSelected) color else Color.Transparent)
            .border(
                width = 2.dp,
                color = color,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else color
        )
    }
} 