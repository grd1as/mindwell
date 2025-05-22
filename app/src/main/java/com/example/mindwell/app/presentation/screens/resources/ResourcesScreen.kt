package com.example.mindwell.app.presentation.screens.resources

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen(
    nav: NavController,
    vm: ResourcesViewModel = hiltViewModel()
) {
    val state = vm.state
    
    LaunchedEffect(nav) {
        vm.setNavController(nav)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guias e Recursos") },
                navigationIcon = {
                    IconButton(onClick = { nav.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Categorias",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            
            items(state.categories) { category ->
                CategoryCard(
                    category = category,
                    onClick = { vm.selectCategory(category.id) }
                )
            }
            
            item {
                Text(
                    text = if (state.selectedCategoryId != null) {
                        state.categories.find { it.id == state.selectedCategoryId }?.title ?: "Recursos Populares"
                    } else "Recursos Populares",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            
            items(state.featuredResources) { resource ->
                ResourceCard(
                    resource = resource,
                    onClick = { vm.selectResource(resource.id) }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCard(
    category: ResourceCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (category.id) {
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
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = category.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ver mais"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceCard(
    resource: Resource,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = resource.title,
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = resource.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${resource.durationMinutes} minutos",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Button(
                    onClick = onClick,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text("Acessar")
                }
            }
        }
    }
} 