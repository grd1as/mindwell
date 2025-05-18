package com.example.mindwell.app.presentation.screens.resources

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mindwell.app.R
import com.example.mindwell.app.common.navigation.BottomNavigationBar
import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.entities.ResourceType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen(
    navController: NavController,
    viewModel: ResourcesViewModel = viewModel()
) {
    // Coletar estado da UI do ViewModel
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Título
            Text(
                text = stringResource(id = R.string.available_resources),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo de busca
            TextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(id = R.string.search_resources)) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Filtros por tipo
            Text(
                text = "Filtrar por tipo:",
                style = MaterialTheme.typography.titleSmall
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedType == null,
                    onClick = { viewModel.setSelectedType(null) },
                    label = { Text("Todos") }
                )
                
                FilterChip(
                    selected = uiState.selectedType == ResourceType.MINDFULNESS,
                    onClick = { 
                        viewModel.setSelectedType(
                            if (uiState.selectedType == ResourceType.MINDFULNESS) null 
                            else ResourceType.MINDFULNESS
                        )
                    },
                    label = { Text("Mindfulness") }
                )
                
                FilterChip(
                    selected = uiState.selectedType == ResourceType.EDUCATION,
                    onClick = { 
                        viewModel.setSelectedType(
                            if (uiState.selectedType == ResourceType.EDUCATION) null 
                            else ResourceType.EDUCATION
                        )
                    },
                    label = { Text("Educação") }
                )
                
                FilterChip(
                    selected = uiState.selectedType == ResourceType.EXERCISE,
                    onClick = { 
                        viewModel.setSelectedType(
                            if (uiState.selectedType == ResourceType.EXERCISE) null 
                            else ResourceType.EXERCISE
                        )
                    },
                    label = { Text("Exercícios") }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Estado de carregamento
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            
            // Erro
            uiState.error?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            
            // Lista de recursos
            if (!uiState.isLoading && uiState.error == null) {
                if (uiState.filteredResources.isEmpty()) {
                    Text(
                        text = "Nenhum recurso encontrado para os filtros selecionados.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.filteredResources) { resource ->
                            ResourceItem(
                                resource = resource,
                                onClick = {
                                    // Navegar para detalhes do recurso
                                    navController.navigate("resource/${resource.id}")
                                }
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
fun ResourceItem(
    resource: Resource,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Título com indicador de recomendado
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = resource.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                if (resource.isRecommended) {
                    Text(
                        text = "Recomendado",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Tipo de recurso
            Text(
                text = when(resource.type) {
                    ResourceType.MINDFULNESS -> "Mindfulness"
                    ResourceType.EDUCATION -> "Educação"
                    ResourceType.EXERCISE -> "Exercício"
                    ResourceType.ARTICLE -> "Artigo"
                    ResourceType.VIDEO -> "Vídeo"
                    ResourceType.AUDIO -> "Áudio" 
                    ResourceType.MEDITATION -> "Meditação"
                    ResourceType.BREATHING -> "Exercício de Respiração"
                    ResourceType.COMMUNITY -> "Comunidade"
                    ResourceType.PROFESSIONAL -> "Profissional"
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Descrição
            Text(
                text = resource.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                resource.tags.forEach { tag ->
                    Text(
                        text = "#$tag",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
} 