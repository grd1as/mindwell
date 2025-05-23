package com.example.mindwell.app.presentation.screens.resources

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
                        "Recursos Inteligentes",
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
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Carregando recursos personalizados...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        } else if (state.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Erro ao carregar recursos",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { vm.retry() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tentar novamente")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cabeçalho motivacional modernizado
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
                            title = "💡 Dicas do Momento",
                            subtitle = "Baseadas no seu estado atual"
                        )
                    }
                    
                    items(state.personalizedTips) { tip ->
                        ModernPersonalizedTipCard(tip = tip)
                    }
                }
                
                // Seção de categorias em linha horizontal
                if (state.categories.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "🎯 Explorar Categorias",
                            subtitle = "Descubra recursos por tema"
                        )
                    }
                    
                    item {
                        ModernCategoriesSection(
                            categories = state.categories,
                            selectedCategoryId = state.selectedCategoryId,
                            onCategorySelected = { vm.selectCategory(it) }
                        )
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
                }
                
                // Card informativo sobre IA modernizado
                item {
                    ModernGeminiInfoCard()
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
} 