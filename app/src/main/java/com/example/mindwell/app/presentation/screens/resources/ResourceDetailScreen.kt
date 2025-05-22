package com.example.mindwell.app.presentation.screens.resources

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Erro ao carregar o recurso",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = state.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { vm.loadResource(resourceId) }
                    ) {
                        Text("Tentar novamente")
                    }
                }
            }
        } else if (state.resource != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Conteúdo do recurso
                ResourceHeader(resource = state.resource)
                
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                
                // Conteúdo específico baseado no tipo
                when (state.resource.categoryId) {
                    "breathing" -> BreathingExerciseContent(resource = state.resource)
                    "meditation" -> MeditationContent(resource = state.resource)
                    "journaling" -> JournalingContent(resource = state.resource)
                    "exercise" -> ExerciseContent(resource = state.resource)
                    "sleep" -> SleepContent(resource = state.resource)
                    else -> GenericResourceContent(resource = state.resource)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { /* Implementar ação para marcar como concluído */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Marcar como concluído")
                }
            }
        }
    }
}

@Composable
fun ResourceHeader(resource: ResourceDetailModel) {
    Column {
        Text(
            text = resource.title,
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (resource.categoryId) {
                    "breathing" -> Icons.Default.Favorite
                    "meditation" -> Icons.Default.Favorite
                    "journaling" -> Icons.Default.Create
                    "exercise" -> Icons.Default.Star
                    "sleep" -> Icons.Default.Star
                    else -> Icons.Default.Info
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "${resource.durationMinutes} minutos",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = resource.description,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun BreathingExerciseContent(resource: ResourceDetailModel) {
    Column {
        Text(
            text = "Como praticar:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        BulletPoint(text = "Encontre um local tranquilo e sente-se confortavelmente")
        BulletPoint(text = "Inspire lentamente pelo nariz contando até 4")
        BulletPoint(text = "Segure a respiração contando até 7")
        BulletPoint(text = "Expire lentamente pela boca contando até 8")
        BulletPoint(text = "Repita o ciclo 4 vezes")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Benefícios:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        BulletPoint(text = "Redução da ansiedade e estresse")
        BulletPoint(text = "Melhora da concentração")
        BulletPoint(text = "Promoção do relaxamento")
        BulletPoint(text = "Auxílio para adormecer")
    }
}

@Composable
fun MeditationContent(resource: ResourceDetailModel) {
    Column {
        Text(
            text = "Como meditar:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        BulletPoint(text = "Sente-se em posição confortável, mantenha a coluna ereta")
        BulletPoint(text = "Feche os olhos e respire naturalmente")
        BulletPoint(text = "Focalize sua atenção na respiração")
        BulletPoint(text = "Quando a mente divagar, gentilmente traga-a de volta à respiração")
        BulletPoint(text = "Comece com sessões curtas e aumente gradualmente")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
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
        Text(
            text = "Perguntas para reflexão:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        BulletPoint(text = "O que me trouxe alegria hoje?")
        BulletPoint(text = "Por quais três coisas sou grato neste momento?")
        BulletPoint(text = "Qual foi um pequeno momento significativo hoje?")
        BulletPoint(text = "O que aprendi hoje sobre mim mesmo?")
        BulletPoint(text = "Como posso tornar amanhã um dia melhor?")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
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
        Text(
            text = "Exercícios recomendados:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        BulletPoint(text = "Caminhada de 10 minutos ao ar livre")
        BulletPoint(text = "Alongamentos suaves para relaxamento")
        BulletPoint(text = "Yoga para iniciantes - posturas básicas")
        BulletPoint(text = "Exercícios de respiração com movimento")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
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
        Text(
            text = "Dicas para melhorar o sono:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        BulletPoint(text = "Mantenha um horário regular para dormir e acordar")
        BulletPoint(text = "Evite telas pelo menos 1 hora antes de dormir")
        BulletPoint(text = "Crie um ambiente escuro, silencioso e fresco")
        BulletPoint(text = "Pratique um ritual relaxante antes de dormir")
        BulletPoint(text = "Evite cafeína e álcool próximo à hora de dormir")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { /* Implementar para abrir sons relaxantes */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Star, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sons relaxantes para dormir")
        }
    }
}

@Composable
fun GenericResourceContent(resource: ResourceDetailModel) {
    Text(
        text = "Conteúdo detalhado estará disponível em breve.",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(end = 8.dp, top = 2.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
} 