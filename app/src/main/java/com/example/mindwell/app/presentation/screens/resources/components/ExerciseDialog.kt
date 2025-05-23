package com.example.mindwell.app.presentation.screens.resources.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mindwell.app.data.services.PersonalizedResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDialog(
    resource: PersonalizedResource,
    onDismiss: () -> Unit
) {
    var isExerciseActive by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(0) }
    var secondsRemaining by remember { mutableStateOf(0) }
    var exerciseSteps by remember { mutableStateOf<List<ExerciseStep>>(emptyList()) }
    
    // Timer effect
    LaunchedEffect(isExerciseActive, secondsRemaining) {
        if (isExerciseActive && secondsRemaining > 0) {
            kotlinx.coroutines.delay(1000)
            secondsRemaining--
        } else if (isExerciseActive && secondsRemaining == 0) {
            // Move to next step
            if (currentStep < exerciseSteps.size - 1) {
                currentStep++
                secondsRemaining = exerciseSteps[currentStep].durationSeconds
            } else {
                // Exercise completed
                isExerciseActive = false
            }
        }
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            if (isExerciseActive) {
                // Active exercise view
                ActiveExerciseView(
                    resource = resource,
                    currentStep = currentStep,
                    exerciseSteps = exerciseSteps,
                    secondsRemaining = secondsRemaining,
                    onPause = { isExerciseActive = false },
                    onStop = { 
                        isExerciseActive = false
                        currentStep = 0
                    },
                    onDismiss = onDismiss
                )
            } else {
                // Exercise preparation view
                ExercisePreparationView(
                    resource = resource,
                    onStart = { steps ->
                        exerciseSteps = steps
                        currentStep = 0
                        secondsRemaining = if (steps.isNotEmpty()) steps[0].durationSeconds else 0
                        isExerciseActive = true
                    },
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
fun ExercisePreparationView(
    resource: PersonalizedResource,
    onStart: (List<ExerciseStep>) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        // Cabeçalho do exercício
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        imageVector = getResourceIcon(resource.icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .size(36.dp)
                            .padding(8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = resource.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "${resource.duration_minutes} minutos • ${resource.difficulty}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Descrição do exercício
        Text(
            text = resource.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Instruções específicas baseadas na categoria
        when (resource.category) {
            "breathing" -> BreathingExerciseContent()
            "meditation" -> MeditationExerciseContent()
            "exercise" -> PhysicalExerciseContent()
            "journaling" -> JournalingExerciseContent()
            "sleep" -> SleepExerciseContent()
            else -> GeneralExerciseContent()
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Botões de ação
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Fechar")
            }
            
            Button(
                onClick = { 
                    val steps = generateExerciseSteps(resource)
                    onStart(steps)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Começar")
            }
        }
    }
}

@Composable
fun ActiveExerciseView(
    resource: PersonalizedResource,
    currentStep: Int,
    exerciseSteps: List<ExerciseStep>,
    secondsRemaining: Int,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        // Progress indicator
        LinearProgressIndicator(
            progress = if (exerciseSteps.isNotEmpty()) (currentStep + 1).toFloat() / exerciseSteps.size else 0f,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Current step info
        if (exerciseSteps.isNotEmpty() && currentStep < exerciseSteps.size) {
            val step = exerciseSteps[currentStep]
            
            Text(
                text = "Etapa ${currentStep + 1} de ${exerciseSteps.size}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = step.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = step.instruction,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Timer display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatTime(secondsRemaining),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    if (step.breathingPattern != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = step.breathingPattern,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Control buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onStop,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Parar")
            }
            
            Button(
                onClick = onPause,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Pausar")
            }
        }
        
        if (currentStep == exerciseSteps.size - 1 && secondsRemaining == 0) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Exercício concluído!",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Finalizar")
                    }
                }
            }
        }
    }
}

// Data class para os passos do exercício
data class ExerciseStep(
    val title: String,
    val instruction: String,
    val durationSeconds: Int,
    val breathingPattern: String? = null
)

// Função para gerar passos do exercício baseado no tipo
fun generateExerciseSteps(resource: PersonalizedResource): List<ExerciseStep> {
    return when (resource.category) {
        "breathing" -> listOf(
            ExerciseStep(
                title = "Preparação",
                instruction = "Sente-se confortavelmente com as costas retas. Relaxe os ombros e feche os olhos suavemente.",
                durationSeconds = 30
            ),
            ExerciseStep(
                title = "Respiração 4-7-8",
                instruction = "Inspire pelo nariz contando até 4, segure a respiração contando até 7, expire pela boca contando até 8.",
                durationSeconds = 60,
                breathingPattern = "Inspire 4 • Segure 7 • Expire 8"
            ),
            ExerciseStep(
                title = "Repetição",
                instruction = "Continue o mesmo padrão respiratório. Foque apenas na contagem e na sensação da respiração.",
                durationSeconds = 90,
                breathingPattern = "4-7-8 • Ritmo calmo"
            ),
            ExerciseStep(
                title = "Finalização",
                instruction = "Aos poucos, volte à respiração natural. Abra os olhos lentamente e observe como se sente.",
                durationSeconds = 30
            )
        )
        
        "meditation" -> listOf(
            ExerciseStep(
                title = "Posicionamento",
                instruction = "Encontre uma posição confortável. Pode ser sentado ou deitado. O importante é que você se sinta relaxado.",
                durationSeconds = 45
            ),
            ExerciseStep(
                title = "Consciência Corporal",
                instruction = "Faça uma varredura mental do seu corpo, desde a cabeça até os pés. Note onde há tensão e permita que ela se dissolva.",
                durationSeconds = 120
            ),
            ExerciseStep(
                title = "Foco na Respiração",
                instruction = "Agora concentre-se na sua respiração natural. Sinta o ar entrando e saindo. Quando a mente divagar, gentilmente volte à respiração.",
                durationSeconds = 180
            ),
            ExerciseStep(
                title = "Gratidão",
                instruction = "Traga à mente três coisas pelas quais você é grato hoje. Sinta essa gratidão preenchendo seu coração.",
                durationSeconds = 60
            ),
            ExerciseStep(
                title = "Retorno",
                instruction = "Lentamente, traga sua consciência de volta ao momento presente. Mova dedos e pés, abra os olhos quando estiver pronto.",
                durationSeconds = 45
            )
        )
        
        else -> listOf(
            ExerciseStep(
                title = "Preparação",
                instruction = "Prepare-se para o exercício. Encontre um local confortável e concentre-se no momento presente.",
                durationSeconds = 30
            ),
            ExerciseStep(
                title = "Prática",
                instruction = "Siga as instruções do exercício com atenção plena. Respire naturalmente e mantenha-se focado.",
                durationSeconds = 180
            ),
            ExerciseStep(
                title = "Finalização",
                instruction = "Finalize o exercício gradualmente. Observe as sensações e sentimentos que surgiram durante a prática.",
                durationSeconds = 30
            )
        )
    }
}

// Função para formatar tempo em MM:SS
fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
} 