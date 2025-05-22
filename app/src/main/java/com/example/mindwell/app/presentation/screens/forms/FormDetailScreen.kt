package com.example.mindwell.app.presentation.screens.forms

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
                    Text(state.formDetail?.name ?: "Carregando...") 
                },
                navigationIcon = {
                    IconButton(onClick = { nav.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { 
                        vm.reloadFormDetail()
                    }) {
                        Text("Tentar novamente")
                    }
                }
            }
        } else if (currentQuestion != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Indicador de progresso
                LinearProgressIndicator(
                    progress = (state.currentQuestionIndex + 1).toFloat() / 
                             (state.formDetail?.questions?.size ?: 1).toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                
                Text(
                    text = "Pergunta ${state.currentQuestionIndex + 1} de ${state.formDetail?.questions?.size}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Pergunta atual
                QuestionContent(
                    question = currentQuestion,
                    selectedOptionId = state.answers[currentQuestion.id]?.optionId,
                    onOptionSelected = { optionId ->
                        vm.answerQuestion(currentQuestion, optionId)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                )
                
                // Botões de navegação
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { vm.previousQuestion() },
                        enabled = state.currentQuestionIndex > 0
                    ) {
                        Text("Anterior")
                    }
                    
                    if (vm.canSubmit()) {
                        Button(
                            onClick = { vm.submitForm() },
                            enabled = !state.isSubmitting
                        ) {
                            if (state.isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Enviar")
                            }
                        }
                    } else {
                        Button(
                            onClick = { vm.nextQuestion() },
                            enabled = vm.canAdvance()
                        ) {
                            Text("Próxima")
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionContent(
    question: Question,
    selectedOptionId: Int?,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = question.text,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Opções de resposta
        question.options.forEach { option ->
            OptionItem(
                option = option,
                isSelected = selectedOptionId == option.id,
                onClick = { onOptionSelected(option.id) }
            )
        }
    }
}

@Composable
fun OptionItem(
    option: Option,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) 
        MaterialTheme.colorScheme.primary 
    else 
        MaterialTheme.colorScheme.outline
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = option.label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
} 