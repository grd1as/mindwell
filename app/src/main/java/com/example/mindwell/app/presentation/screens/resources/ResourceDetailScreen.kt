package com.example.mindwell.app.presentation.screens.resources

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.entities.ResourceType
import com.example.mindwell.app.domain.usecases.resource.GetResourceByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado da UI para a tela de detalhes do recurso
 */
data class ResourceDetailUiState(
    val resource: Resource? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Implementação mock do caso de uso para obter um recurso pelo ID
 */
class MockGetResourceByIdUseCase : GetResourceByIdUseCase {
    override suspend operator fun invoke(id: Long): Result<Resource?> {
        // Simulate some delay
        kotlinx.coroutines.delay(500)
        
        // Create a mock resource based on ID
        val mockResource = Resource(
            id = id,
            title = "Recurso de exemplo #$id",
            description = "Esta é uma descrição para o recurso de exemplo #$id",
            type = when (id % 10) {
                0L -> ResourceType.ARTICLE
                1L -> ResourceType.VIDEO
                2L -> ResourceType.AUDIO
                3L -> ResourceType.MEDITATION
                4L -> ResourceType.MINDFULNESS
                5L -> ResourceType.BREATHING
                6L -> ResourceType.EDUCATION
                7L -> ResourceType.COMMUNITY
                8L -> ResourceType.PROFESSIONAL
                else -> ResourceType.EXERCISE
            },
            tags = listOf("exemplo", "mock", "teste"),
            content = "Conteúdo detalhado para o recurso de exemplo #$id que seria mostrado na tela de detalhes.",
            isRecommended = id % 2 == 0L
        )
        
        return Result.success(mockResource)
    }
}

/**
 * ViewModel para a tela de detalhes do recurso
 */
class ResourceDetailViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ResourceDetailUiState(isLoading = true))
    val uiState: StateFlow<ResourceDetailUiState> = _uiState.asStateFlow()
    
    // Mock implementation for GetResourceByIdUseCase
    private val getResourceByIdUseCase: GetResourceByIdUseCase = MockGetResourceByIdUseCase()
    
    fun loadResource(resourceId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val result = getResourceByIdUseCase(resourceId)
                
                result.fold(
                    onSuccess = { resource ->
                        if (resource != null) {
                            _uiState.update { it.copy(resource = resource, isLoading = false) }
                        } else {
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    error = "Recurso não encontrado"
                                )
                            }
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Erro ao carregar o recurso"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Erro ao carregar o recurso"
                    )
                }
            }
        }
    }
}

/**
 * Tela de detalhes do recurso
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceDetailScreen(
    navController: NavController,
    resourceId: Long,
    viewModel: ResourceDetailViewModel = viewModel()
) {
    // Carregar o recurso pelo ID
    LaunchedEffect(resourceId) {
        viewModel.loadResource(resourceId)
    }
    
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Recurso") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp)
                        )
                    }
                    
                    uiState.error != null -> {
                        Text(
                            text = uiState.error ?: "Erro desconhecido",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    
                    uiState.resource != null -> {
                        ResourceDetailContent(resource = uiState.resource!!)
                    }
                    
                    else -> {
                        Text(
                            text = "Recurso não encontrado",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResourceDetailContent(resource: Resource) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Título
        Text(
            text = resource.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Tipo
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
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Descrição
        Text(
            text = resource.description,
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Conteúdo principal
        Text(
            text = resource.content,
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tags
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Tags: ",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = resource.tags.joinToString(", "),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
} 