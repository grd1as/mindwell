package com.example.mindwell.app.presentation.screens.resources

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mindwell.app.domain.entities.ResourceDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceDetailScreen(
    nav: NavController,
    resourceId: String,
    vm: ResourceDetailViewModel = hiltViewModel()
) {
    val state = vm.state
    
    LaunchedEffect(resourceId) {
        vm.loadResource(resourceId)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        if (state.isLoading) {
            LoadingState()
        } else if (state.error != null) {
            ErrorState(
                error = state.error,
                onRetry = { vm.loadResource(resourceId) }
            )
        } else if (state.resource != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header moderno com gradiente
                ModernHeader(
                    resource = state.resource,
                    onBackClick = { nav.navigateUp() }
                )
                
                // Conteúdo em cards modernos
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Conteúdo específico baseado no tipo
                    when (state.resource.categoryId) {
                        "breathing" -> ModernBreathingContent(resource = state.resource)
                        "meditation" -> ModernMeditationContent(resource = state.resource)
                        "journaling" -> ModernJournalingContent(resource = state.resource)
                        "exercise" -> ModernExerciseContent(resource = state.resource)
                        "sleep" -> ModernSleepContent(resource = state.resource)
                        else -> ModernGenericContent(resource = state.resource)
                    }
                    
                    // Botão de conclusão moderno
                    ModernCompletionButton()
                    
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Carregando recurso...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ErrorState(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Não foi possível carregar o recurso",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tentar novamente")
            }
        }
    }
}

@Composable
fun ModernHeader(
    resource: ResourceDetail,
    onBackClick: () -> Unit
) {
    val (gradientColors, emoji) = when (resource.categoryId) {
        "breathing" -> Pair(listOf(Color(0xFF10B981), Color(0xFF34D399)), "🫁")
        "meditation" -> Pair(listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA)), "🧘")
        "journaling" -> Pair(listOf(Color(0xFFEAB308), Color(0xFFFBBF24)), "📝")
        "exercise" -> Pair(listOf(Color(0xFFEF4444), Color(0xFFF87171)), "💪")
        "sleep" -> Pair(listOf(Color(0xFF6366F1), Color(0xFF818CF8)), "😴")
        else -> Pair(listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)), "✨")
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
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
                        .clickable { onBackClick() },
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
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
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
                    Text(
                        text = resource.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = "${resource.durationMinutes} minutos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Descrição
            Text(
                text = resource.description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ModernBreathingContent(resource: ResourceDetail) {
    var isAnimating by remember { mutableStateOf(false) }
    
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
                            Color(0xFFF0FDF4),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            ModernSectionTitle(text = "Como praticar:", emoji = "🌬️")
            
            resource.steps.forEachIndexed { index, step ->
                ModernStepItem(index = index + 1, text = step)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ModernSectionTitle(text = "Benefícios:", emoji = "✨")
            
            ModernBulletPoint(text = "Redução da ansiedade e estresse")
            ModernBulletPoint(text = "Melhora da concentração")
            ModernBulletPoint(text = "Promoção do relaxamento")
            ModernBulletPoint(text = "Auxílio para adormecer")
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ModernActionButton(
                text = if (isAnimating) "Pausar prática guiada" else "Iniciar prática guiada",
                icon = if (isAnimating) Icons.Default.Close else Icons.Default.PlayArrow,
                colors = listOf(Color(0xFF10B981), Color(0xFF34D399)),
                onClick = { isAnimating = !isAnimating }
            )
            
            if (isAnimating) {
                Spacer(modifier = Modifier.height(16.dp))
                ModernBreathingAnimation()
            }
        }
    }
}

@Composable
fun ModernBreathingAnimation() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF10B981).copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🫁",
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Respire fundo...",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color(0xFF10B981)
                )
                Text(
                    text = "Inspire por 4 segundos, segure por 7, expire por 8",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ModernMeditationContent(resource: ResourceDetail) {
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
                            Color(0xFFF3F4F6),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            ModernSectionTitle(text = "Como meditar:", emoji = "🧘")
            
            resource.steps.forEachIndexed { index, step ->
                ModernStepItem(index = index + 1, text = step)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ModernActionButton(
                text = "Iniciar meditação guiada",
                icon = Icons.Default.PlayArrow,
                colors = listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA)),
                onClick = { /* Implementar para iniciar áudio guiado */ }
            )
        }
    }
}

@Composable
fun ModernJournalingContent(resource: ResourceDetail) {
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
                            Color(0xFFFEF3C7),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            ModernSectionTitle(text = "Perguntas para reflexão:", emoji = "📝")
            
            resource.steps.forEachIndexed { index, step ->
                ModernStepItem(index = index + 1, text = step)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ModernActionButton(
                text = "Abrir meu diário",
                icon = Icons.Default.Edit,
                colors = listOf(Color(0xFFEAB308), Color(0xFFFBBF24)),
                onClick = { /* Implementar para abrir bloco de anotações */ }
            )
        }
    }
}

@Composable
fun ModernExerciseContent(resource: ResourceDetail) {
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
                            Color(0xFFFEE2E2),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            ModernSectionTitle(text = "Exercícios recomendados:", emoji = "💪")
            
            ModernBulletPoint(text = "Caminhada de 10 minutos ao ar livre")
            ModernBulletPoint(text = "Alongamentos suaves para relaxamento")
            ModernBulletPoint(text = "Yoga para iniciantes - posturas básicas")
            ModernBulletPoint(text = "Exercícios de respiração com movimento")
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ModernActionButton(
                text = "Ver demonstração",
                icon = Icons.Default.PlayArrow,
                colors = listOf(Color(0xFFEF4444), Color(0xFFF87171)),
                onClick = { /* Implementar para abrir vídeo de demonstração */ }
            )
        }
    }
}

@Composable
fun ModernSleepContent(resource: ResourceDetail) {
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
                            Color(0xFFEEF2FF),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            ModernSectionTitle(text = "Dicas para melhorar o sono:", emoji = "😴")
            
            ModernBulletPoint(text = "Mantenha um horário regular para dormir e acordar")
            ModernBulletPoint(text = "Evite telas pelo menos 1 hora antes de dormir")
            ModernBulletPoint(text = "Crie um ambiente escuro, silencioso e fresco")
            ModernBulletPoint(text = "Pratique um ritual relaxante antes de dormir")
            ModernBulletPoint(text = "Evite cafeína e álcool próximo à hora de dormir")
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ModernActionButton(
                text = "Sons relaxantes para dormir",
                icon = Icons.Default.PlayArrow,
                colors = listOf(Color(0xFF6366F1), Color(0xFF818CF8)),
                onClick = { /* Implementar para abrir sons relaxantes */ }
            )
        }
    }
}

@Composable
fun ModernGenericContent(resource: ResourceDetail) {
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
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🚀",
                fontSize = 48.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Conteúdo em desenvolvimento",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Conteúdo detalhado estará disponível em breve.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ModernCompletionButton() {
    Button(
        onClick = { /* Implementar ação para marcar como concluído */ },
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
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF10B981), Color(0xFF34D399))
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Marcar como concluído",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ModernSectionTitle(text: String, emoji: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF6366F1),
                            Color(0xFF8B5CF6)
                        )
                    ),
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
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color(0xFF1A1A1A)
        )
    }
}

@Composable
fun ModernStepItem(index: Int, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF6366F1),
                            Color(0xFF8B5CF6)
                        )
                    ),
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
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun ModernBulletPoint(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = Color(0xFF6366F1),
                    shape = CircleShape
                )
                .padding(top = 6.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF2D3748)
        )
    }
}

@Composable
fun ModernActionButton(
    text: String,
    icon: ImageVector,
    colors: List<Color>,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
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
                    brush = Brush.horizontalGradient(colors),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun StepItem(index: Int, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Text(
                text = index.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
} 