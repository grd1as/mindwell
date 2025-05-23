package com.example.mindwell.app.presentation.screens.resources.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mindwell.app.data.services.PersonalizedResource
import androidx.compose.foundation.lazy.LazyColumn
import android.util.Log

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
    
    Log.d("ExerciseDialog", "🔄 Recompondo - isExerciseActive: $isExerciseActive")
    
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
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFF)),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            if (isExerciseActive) {
                // Active exercise view - Full screen modern
                ModernActiveExerciseView(
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
                // Exercise preparation view - Modern card
                ModernExercisePreparationView(
                    resource = resource,
                    onStart = { steps ->
                        Log.d("ExerciseDialog", "🚀 onStart recebido com ${steps.size} passos")
                        exerciseSteps = steps
                        currentStep = 0
                        secondsRemaining = if (steps.isNotEmpty()) steps[0].durationSeconds else 0
                        Log.d("ExerciseDialog", "📊 Estado antes: isExerciseActive=$isExerciseActive")
                        isExerciseActive = true
                        Log.d("ExerciseDialog", "📊 Estado depois: isExerciseActive=$isExerciseActive")
                    },
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
fun ModernExercisePreparationView(
    resource: PersonalizedResource,
    onStart: (List<ExerciseStep>) -> Unit,
    onDismiss: () -> Unit
) {
    val (gradientColors, emoji) = when (resource.category) {
        "breathing" -> Pair(listOf(Color(0xFF10B981), Color(0xFF34D399)), "🫁")
        "meditation" -> Pair(listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA)), "🧘")
        "exercise" -> Pair(listOf(Color(0xFFEF4444), Color(0xFFF87171)), "💪")
        "journaling" -> Pair(listOf(Color(0xFFEAB308), Color(0xFFFBBF24)), "📝")
        "sleep" -> Pair(listOf(Color(0xFF6366F1), Color(0xFF818CF8)), "😴")
        else -> Pair(listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)), "✨")
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header moderno com gradiente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = gradientColors + Color(0xFFF8FAFF)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Barra de navegação
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { onDismiss() },
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Conteúdo do header
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ícone emoji
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.3f),
                                        Color.White.copy(alpha = 0.1f)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 20.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = resource.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "${resource.duration_minutes} min • ${resource.difficulty}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
        
        // Conteúdo scrollável
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Descrição
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = resource.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF2D3748),
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // Instruções específicas por categoria
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    when (resource.category) {
                        "breathing" -> ModernBreathingExerciseContent(gradientColors)
                        "meditation" -> ModernMeditationExerciseContent(gradientColors)
                        "exercise" -> ModernPhysicalExerciseContent(gradientColors)
                        "journaling" -> ModernJournalingExerciseContent(gradientColors)
                        "sleep" -> ModernSleepExerciseContent(gradientColors)
                        else -> ModernGeneralExerciseContent(gradientColors)
                    }
                }
            }
        }
        
        // Botão de iniciar sempre visível
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Button(
                onClick = { 
                    Log.d("ExerciseDialog", "🎯 Botão 'Começar agora' clicado!")
                    val steps = generateExerciseSteps(resource)
                    Log.d("ExerciseDialog", "📋 Steps gerados: ${steps.size} passos")
                    onStart(steps)
                    Log.d("ExerciseDialog", "✅ onStart chamado!")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(gradientColors),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Começar agora",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernActiveExerciseView(
    resource: PersonalizedResource,
    currentStep: Int,
    exerciseSteps: List<ExerciseStep>,
    secondsRemaining: Int,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onDismiss: () -> Unit
) {
    val (gradientColors, emoji) = when (resource.category) {
        "breathing" -> Pair(listOf(Color(0xFF10B981), Color(0xFF34D399)), "🫁")
        "meditation" -> Pair(listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA)), "🧘")
        "exercise" -> Pair(listOf(Color(0xFFEF4444), Color(0xFFF87171)), "💪")
        "journaling" -> Pair(listOf(Color(0xFFEAB308), Color(0xFFFBBF24)), "📝")
        "sleep" -> Pair(listOf(Color(0xFF6366F1), Color(0xFF818CF8)), "😴")
        else -> Pair(listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)), "✨")
    }

    val isCompleted = currentStep == exerciseSteps.size - 1 && secondsRemaining == 0

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header moderno com gradiente e progresso
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = gradientColors + Color(0xFFF8FAFF)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Barra de navegação
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { onStop() },
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Parar",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Progress indicator moderno
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Etapa ${currentStep + 1} de ${exerciseSteps.size}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            
                            Text(
                                text = "${((currentStep + 1).toFloat() / exerciseSteps.size * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LinearProgressIndicator(
                            progress = if (exerciseSteps.isNotEmpty()) (currentStep + 1).toFloat() / exerciseSteps.size else 0f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Conteúdo do header
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ícone emoji
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.3f),
                                        Color.White.copy(alpha = 0.1f)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 28.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        if (exerciseSteps.isNotEmpty() && currentStep < exerciseSteps.size) {
                            val step = exerciseSteps[currentStep]
                            
                            Text(
                                text = step.title,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
        
        // Conteúdo principal em cards modernos
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isCompleted) {
                // Tela de conclusão
                ModernCompletionCard(onDismiss = onDismiss, gradientColors = gradientColors)
            } else if (exerciseSteps.isNotEmpty() && currentStep < exerciseSteps.size) {
                val step = exerciseSteps[currentStep]
                
                // Card de instruções
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFF8FAFF),
                                        Color(0xFFFFFFFF)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Text(
                            text = step.instruction,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF2D3748),
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                // Card do timer - Principal destaque
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = gradientColors.map { it.copy(alpha = 0.1f) }
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = formatTime(secondsRemaining),
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = gradientColors.first()
                            )
                            
                            if (step.breathingPattern != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = step.breathingPattern,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF666666),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Botões de controle modernos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botão parar
                    OutlinedButton(
                        onClick = onStop,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, gradientColors.first()),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = gradientColors.first()
                        )
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Parar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    // Botão pausar
                    Button(
                        onClick = onPause,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(gradientColors),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Pausar",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernCompletionCard(
    onDismiss: () -> Unit,
    gradientColors: List<Color>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = gradientColors.map { it.copy(alpha = 0.1f) } + Color.White
                    )
                )
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎉",
                fontSize = 64.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Exercício concluído!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Parabéns por dedicar tempo ao seu bem-estar!",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(gradientColors),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Finalizar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ModernBreathingExerciseContent(gradientColors: List<Color>) {
    Column(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        gradientColors.first().copy(alpha = 0.05f),
                        Color.White
                    )
                )
            )
            .padding(20.dp)
    ) {
        ModernSectionTitle(text = "Como praticar:", emoji = "🌬️", color = gradientColors.first())
        
        val steps = listOf(
            "Sente-se confortavelmente ou deite-se",
            "Inspire profundamente pelo nariz por 4 segundos",
            "Segure a respiração por 7 segundos",
            "Expire lentamente pela boca por 8 segundos",
            "Repita o ciclo 4-7-8 por alguns minutos"
        )
        
        steps.forEachIndexed { index, step ->
            ModernStepItem(index = index + 1, text = step, color = gradientColors.first())
        }
    }
}

@Composable
fun ModernMeditationExerciseContent(gradientColors: List<Color>) {
    Column(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        gradientColors.first().copy(alpha = 0.05f),
                        Color.White
                    )
                )
            )
            .padding(20.dp)
    ) {
        ModernSectionTitle(text = "Instruções:", emoji = "🧘", color = gradientColors.first())
        
        val instructions = listOf(
            "Encontre um local calmo e confortável",
            "Feche os olhos suavemente",
            "Concentre-se na sua respiração natural",
            "Quando a mente divagar, volte gentilmente ao foco",
            "Termine com gratidão pelo momento"
        )
        
        instructions.forEachIndexed { index, instruction ->
            ModernStepItem(index = index + 1, text = instruction, color = gradientColors.first())
        }
    }
}

@Composable
fun ModernPhysicalExerciseContent(gradientColors: List<Color>) {
    Column(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        gradientColors.first().copy(alpha = 0.05f),
                        Color.White
                    )
                )
            )
            .padding(20.dp)
    ) {
        ModernSectionTitle(text = "Preparação:", emoji = "💪", color = gradientColors.first())
        
        val preparations = listOf(
            "Vista roupas confortáveis para exercício",
            "Certifique-se de ter espaço suficiente",
            "Mantenha uma garrafa de água por perto",
            "Ouça seu corpo e respeite seus limites"
        )
        
        preparations.forEachIndexed { index, prep ->
            ModernStepItem(index = index + 1, text = prep, color = gradientColors.first())
        }
    }
}

@Composable
fun ModernJournalingExerciseContent(gradientColors: List<Color>) {
    Column(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        gradientColors.first().copy(alpha = 0.05f),
                        Color.White
                    )
                )
            )
            .padding(20.dp)
    ) {
        ModernSectionTitle(text = "Dicas para escrever:", emoji = "📝", color = gradientColors.first())
        
        val tips = listOf(
            "Encontre um local tranquilo",
            "Escreva sem se preocupar com gramática",
            "Seja honesto com seus sentimentos",
            "Não julgue o que está escrevendo"
        )
        
        tips.forEachIndexed { index, tip ->
            ModernStepItem(index = index + 1, text = tip, color = gradientColors.first())
        }
    }
}

@Composable
fun ModernSleepExerciseContent(gradientColors: List<Color>) {
    Column(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        gradientColors.first().copy(alpha = 0.05f),
                        Color.White
                    )
                )
            )
            .padding(20.dp)
    ) {
        ModernSectionTitle(text = "Para um sono melhor:", emoji = "😴", color = gradientColors.first())
        
        val tips = listOf(
            "Mantenha um horário regular de sono",
            "Evite telas 1 hora antes de dormir",
            "Pratique exercícios de relaxamento",
            "Mantenha o quarto fresco e escuro"
        )
        
        tips.forEachIndexed { index, tip ->
            ModernStepItem(index = index + 1, text = tip, color = gradientColors.first())
        }
    }
}

@Composable
fun ModernGeneralExerciseContent(gradientColors: List<Color>) {
    Column(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        gradientColors.first().copy(alpha = 0.05f),
                        Color.White
                    )
                )
            )
            .padding(20.dp)
    ) {
        ModernSectionTitle(text = "Instruções gerais:", emoji = "✨", color = gradientColors.first())
        
        val instructions = listOf(
            "Prepare-se mentalmente para a atividade",
            "Concentre-se no momento presente",
            "Respire naturalmente durante a prática",
            "Observe suas sensações sem julgamento"
        )
        
        instructions.forEachIndexed { index, instruction ->
            ModernStepItem(index = index + 1, text = instruction, color = gradientColors.first())
        }
    }
}

@Composable
fun ModernSectionTitle(text: String, emoji: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = color.copy(alpha = 0.2f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 16.sp
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color(0xFF1A1A1A)
        )
    }
}

@Composable
fun ModernStepItem(index: Int, text: String, color: Color) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = color,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = index.toString(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF2D3748),
            modifier = Modifier.padding(top = 2.dp)
        )
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
        
        "exercise" -> listOf(
            ExerciseStep(
                title = "Aquecimento",
                instruction = "Faça movimentos suaves para aquecer o corpo: role os ombros, estique os braços e faça polichinelos leves.",
                durationSeconds = 60
            ),
            ExerciseStep(
                title = "Exercícios Principais",
                instruction = "Execute os movimentos do exercício mantendo boa postura. Respire naturalmente e ouça seu corpo.",
                durationSeconds = 300
            ),
            ExerciseStep(
                title = "Alongamento",
                instruction = "Termine com alongamentos suaves para relaxar os músculos trabalhados. Segure cada posição por 15-20 segundos.",
                durationSeconds = 120
            )
        )
        
        "journaling" -> listOf(
            ExerciseStep(
                title = "Preparação",
                instruction = "Encontre um local tranquilo e pegue papel e caneta (ou abra um aplicativo de notas). Respire fundo e se concentre.",
                durationSeconds = 30
            ),
            ExerciseStep(
                title = "Reflexão Inicial",
                instruction = "Comece escrevendo como você está se sentindo neste momento. Não se preocupe com gramática ou estrutura.",
                durationSeconds = 120
            ),
            ExerciseStep(
                title = "Exploração",
                instruction = "Explore seus pensamentos e sentimentos. Escreva sobre o que está em sua mente, suas preocupações ou alegrias.",
                durationSeconds = 240
            ),
            ExerciseStep(
                title = "Gratidão",
                instruction = "Termine escrevendo três coisas pelas quais você é grato hoje, por menores que sejam.",
                durationSeconds = 60
            )
        )
        
        "sleep" -> listOf(
            ExerciseStep(
                title = "Preparação do Ambiente",
                instruction = "Ajuste a iluminação, temperatura e elimine distrações. Deixe o ambiente propício para o relaxamento.",
                durationSeconds = 60
            ),
            ExerciseStep(
                title = "Relaxamento Muscular",
                instruction = "Deite-se confortavelmente. Tensione e relaxe cada grupo muscular, começando pelos pés e subindo até a cabeça.",
                durationSeconds = 180
            ),
            ExerciseStep(
                title = "Visualização",
                instruction = "Imagine um lugar calmo e sereno. Pode ser uma praia, floresta ou qualquer lugar que lhe traga paz.",
                durationSeconds = 240
            ),
            ExerciseStep(
                title = "Respiração para Dormir",
                instruction = "Respire lenta e profundamente. A cada expiração, permita que seu corpo afunde mais na cama.",
                durationSeconds = 180
            )
        )
        
        "mindfulness" -> listOf(
            ExerciseStep(
                title = "Ancoragem no Presente",
                instruction = "Sente-se confortavelmente e traga sua atenção para o momento presente. Note os sons, cheiros e sensações ao seu redor.",
                durationSeconds = 60
            ),
            ExerciseStep(
                title = "Observação dos Pensamentos",
                instruction = "Observe seus pensamentos como nuvens passando no céu. Não julgue nem se prenda a eles, apenas observe.",
                durationSeconds = 180
            ),
            ExerciseStep(
                title = "Consciência Corporal",
                instruction = "Traga atenção para seu corpo. Sinta os pontos de contato com a superfície onde está sentado ou deitado.",
                durationSeconds = 120
            ),
            ExerciseStep(
                title = "Integração",
                instruction = "Lentamente, prepare-se para retornar às suas atividades levando essa consciência presente com você.",
                durationSeconds = 60
            )
        )
        
        else -> listOf(
            ExerciseStep(
                title = "Preparação",
                instruction = "Prepare-se para o exercício. Encontre um local confortável e concentre-se no momento presente.",
                durationSeconds = 30
            ),
            ExerciseStep(
                title = "Prática Principal",
                instruction = "Siga as instruções do exercício com atenção plena. Respire naturalmente e mantenha-se focado no que está fazendo.",
                durationSeconds = 180
            ),
            ExerciseStep(
                title = "Conclusão",
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