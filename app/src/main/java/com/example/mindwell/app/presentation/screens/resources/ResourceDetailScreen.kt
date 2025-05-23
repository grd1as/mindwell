package com.example.mindwell.app.presentation.screens.resources

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.resource?.title ?: "Carregando...") },
                navigationIcon = {
                    IconButton(onClick = { nav.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            LoadingState(modifier = Modifier.padding(padding))
        } else if (state.error != null) {
            ErrorState(
                error = state.error,
                onRetry = { vm.loadResource(resourceId) },
                modifier = Modifier.padding(padding)
            )
        } else if (state.resource != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Banner colorido no topo
                ResourceBanner(
                    resource = state.resource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                
                // Conteúdo específico baseado no tipo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    when (state.resource.categoryId) {
                        "breathing" -> BreathingExerciseContent(resource = state.resource)
                        "meditation" -> MeditationContent(resource = state.resource)
                        "journaling" -> JournalingContent(resource = state.resource)
                        "exercise" -> ExerciseContent(resource = state.resource)
                        "sleep" -> SleepContent(resource = state.resource)
                        else -> GenericResourceContent(resource = state.resource)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Botão de conclusão
                Button(
                    onClick = { /* Implementar ação para marcar como concluído */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Marcar como concluído")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
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
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
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
fun ResourceBanner(
    resource: ResourceDetailModel,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, icon) = when (resource.categoryId) {
        "breathing" -> Pair(Color(0xFF90CAF9), Icons.Default.Favorite)
        "meditation" -> Pair(Color(0xFFBBDEFB), Icons.Default.Favorite)
        "journaling" -> Pair(Color(0xFFB39DDB), Icons.Default.Create)
        "exercise" -> Pair(Color(0xFFA5D6A7), Icons.Default.Favorite)
        "sleep" -> Pair(Color(0xFF80DEEA), Icons.Default.Star)
        else -> Pair(Color(0xFFE0E0E0), Icons.Default.Info)
    }
    
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = backgroundColor
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = resource.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = "${resource.durationMinutes} minutos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = resource.description,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun BreathingExerciseContent(resource: ResourceDetailModel) {
    var isAnimating by remember { mutableStateOf(false) }
    
    Column {
        SectionTitle(text = "Como praticar:")
        
        resource.steps.forEachIndexed { index, step ->
            StepItem(index = index + 1, text = step)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        SectionTitle(text = "Benefícios:")
        
        BulletPoint(text = "Redução da ansiedade e estresse")
        BulletPoint(text = "Melhora da concentração")
        BulletPoint(text = "Promoção do relaxamento")
        BulletPoint(text = "Auxílio para adormecer")
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedButton(
            onClick = { isAnimating = !isAnimating },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = if (isAnimating) Icons.Default.Close else Icons.Default.PlayArrow,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isAnimating) "Pausar prática guiada" else "Iniciar prática guiada")
        }
        
        if (isAnimating) {
            Spacer(modifier = Modifier.height(16.dp))
            BreathingAnimation()
        }
    }
}

@Composable
fun BreathingAnimation() {
    // Implementação simplificada da animação
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Respire fundo...",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun MeditationContent(resource: ResourceDetailModel) {
    Column {
        SectionTitle(text = "Como meditar:")
        
        resource.steps.forEachIndexed { index, step ->
            StepItem(index = index + 1, text = step)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedButton(
            onClick = { /* Implementar para iniciar áudio guiado */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Iniciar meditação guiada")
        }
    }
}

@Composable
fun JournalingContent(resource: ResourceDetailModel) {
    Column {
        SectionTitle(text = "Perguntas para reflexão:")
        
        resource.steps.forEachIndexed { index, step ->
            StepItem(index = index + 1, text = step)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedButton(
            onClick = { /* Implementar para abrir bloco de anotações */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Abrir meu diário")
        }
    }
}

@Composable
fun ExerciseContent(resource: ResourceDetailModel) {
    Column {
        SectionTitle(text = "Exercícios recomendados:")
        
        BulletPoint(text = "Caminhada de 10 minutos ao ar livre")
        BulletPoint(text = "Alongamentos suaves para relaxamento")
        BulletPoint(text = "Yoga para iniciantes - posturas básicas")
        BulletPoint(text = "Exercícios de respiração com movimento")
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedButton(
            onClick = { /* Implementar para abrir vídeo de demonstração */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver demonstração")
        }
    }
}

@Composable
fun SleepContent(resource: ResourceDetailModel) {
    Column {
        SectionTitle(text = "Dicas para melhorar o sono:")
        
        BulletPoint(text = "Mantenha um horário regular para dormir e acordar")
        BulletPoint(text = "Evite telas pelo menos 1 hora antes de dormir")
        BulletPoint(text = "Crie um ambiente escuro, silencioso e fresco")
        BulletPoint(text = "Pratique um ritual relaxante antes de dormir")
        BulletPoint(text = "Evite cafeína e álcool próximo à hora de dormir")
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedButton(
            onClick = { /* Implementar para abrir sons relaxantes */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sons relaxantes para dormir")
        }
    }
}

@Composable
fun GenericResourceContent(resource: ResourceDetailModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Conteúdo detalhado estará disponível em breve.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
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