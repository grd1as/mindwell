package com.example.mindwell.app.presentation.screens.resources

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mindwell.app.data.services.PersonalizedResource
import com.example.mindwell.app.presentation.screens.resources.components.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen(
    nav: NavController,
    vm: ResourcesViewModel = hiltViewModel()
) {
    val state = vm.state
    var selectedResource by remember { mutableStateOf<PersonalizedResource?>(null) }
    
    LaunchedEffect(nav) {
        vm.setNavController(nav)
    }
    
    // Dialog para mostrar exercício interativo
    selectedResource?.let { resource ->
        ExerciseDialog(
            resource = resource,
            onDismiss = { selectedResource = null }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Recursos",
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color(0xFF6366F1),
                                strokeWidth = 4.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Carregando recursos...",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }
            } else if (state.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "⚠️",
                                fontSize = 64.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Erro ao carregar recursos",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center,
                                color = Color(0xFF1A1A1A)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = state.error,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF666666),
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { vm.retry() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF6366F1)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Tentar novamente",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    // Cabeçalho moderno
                    item {
                        ModernWelcomeHeader()
                    }
                    
                    // Seção personalizada do Gemini
                    state.personalizedContent?.let { content ->
                        item {
                            ModernPersonalizedMessageCard(
                                message = content.personalized_message,
                                onRefresh = { vm.refreshPersonalizedContent() },
                                isLoading = state.isPersonalizedLoading
                            )
                        }
                        
                        item {
                            SectionHeader(
                                title = "✨ Recursos Personalizados",
                                subtitle = "Criados especialmente para você"
                            )
                        }
                        
                        items(content.resources) { resource ->
                            ModernPersonalizedResourceCard(
                                resource = resource,
                                onClick = { selectedResource = resource }
                            )
                        }
                    }
                    
                    // Seção de dicas personalizadas
                    if (state.personalizedTips.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "💡 Dicas Inteligentes",
                                subtitle = "Baseadas em IA"
                            )
                        }
                        
                        items(state.personalizedTips) { tip ->
                            ModernPersonalizedTipCard(tip = tip)
                        }
                    }
                    
                    // Mostrar recursos filtrados por categoria
                    state.personalizedContent?.let { content ->
                        val filteredResources = if (state.selectedCategoryId == "all" || state.selectedCategoryId == null) {
                            emptyList()
                        } else {
                            content.resources.filter { it.category == state.selectedCategoryId }
                        }
                        
                        if (filteredResources.isNotEmpty()) {
                            item {
                                val categoryName = state.categories.find { it.id == state.selectedCategoryId }?.title ?: "Categoria"
                                SectionHeader(
                                    title = "📚 $categoryName",
                                    subtitle = "Recursos especializados"
                                )
                            }
                
                            items(filteredResources) { resource ->
                                ModernPersonalizedResourceCard(
                                    resource = resource,
                                    onClick = { selectedResource = resource }
                                )
                            }
                        }
                    }
                    
                    // Card informativo sobre IA
                    item {
                        ModernGeminiInfoCard()
                    }
                }
            }
        }
    }
} 