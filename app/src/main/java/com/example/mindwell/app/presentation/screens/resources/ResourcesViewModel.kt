package com.example.mindwell.app.presentation.screens.resources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.entities.ResourceType
import com.example.mindwell.app.domain.usecases.resource.GetAllResourcesUseCase
import com.example.mindwell.app.domain.usecases.resource.GetResourcesByTypeUseCase
import com.example.mindwell.app.domain.usecases.resource.SearchResourcesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.random.Random

/**
 * Estado da UI para a tela de recursos
 */
data class ResourcesUiState(
    val resources: List<Resource> = emptyList(),
    val filteredResources: List<Resource> = emptyList(),
    val selectedType: ResourceType? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Implementação mock do caso de uso para obter todos os recursos
 */
class MockGetAllResourcesUseCase : GetAllResourcesUseCase {
    override fun invoke(): Flow<Result<List<Resource>>> = flow {
        // Simulate delay
        kotlinx.coroutines.delay(500)
        
        // Generate mock resources covering all resource types
        val mockResources = generateMockResources(20)
        emit(Result.success(mockResources))
    }
    
    private fun generateMockResources(count: Int): List<Resource> {
        return List(count) { index ->
            val id = index.toLong() + 1
            Resource(
                id = id,
                title = "Recurso #$id",
                description = "Descrição do recurso #$id com conteúdo informativo sobre bem-estar mental",
                type = ResourceType.values()[index % ResourceType.values().size],
                tags = generateRandomTags(),
                content = "Conteúdo detalhado do recurso #$id. Este texto seria mais extenso em um ambiente real.",
                isRecommended = index % 3 == 0
            )
        }
    }
    
    private fun generateRandomTags(): List<String> {
        val allTags = listOf("ansiedade", "estresse", "meditação", "relaxamento", 
                        "foco", "sono", "produtividade", "trabalho", "alimentação", 
                        "exercício", "mindfulness", "respiração")
        val tagsCount = Random.nextInt(1, 4)
        return allTags.shuffled().take(tagsCount)
    }
}

/**
 * Implementação mock do caso de uso para obter recursos por tipo
 */
class MockGetResourcesByTypeUseCase : GetResourcesByTypeUseCase {
    override fun invoke(type: ResourceType): Flow<Result<List<Resource>>> = flow {
        // Simulate delay
        kotlinx.coroutines.delay(300)
        
        // Generate mock resources of the specified type
        val mockResources = generateMockResourcesByType(type, 5)
        emit(Result.success(mockResources))
    }
    
    private fun generateMockResourcesByType(type: ResourceType, count: Int): List<Resource> {
        return List(count) { index ->
            val id = (index + 100).toLong()
            Resource(
                id = id,
                title = "Recurso de ${type.name.lowercase()} #$id",
                description = "Recurso específico do tipo ${type.name}",
                type = type,
                tags = listOf(type.name.lowercase(), "exemplo", "mock"),
                content = "Conteúdo específico para recurso do tipo ${type.name}",
                isRecommended = index % 2 == 0
            )
        }
    }
}

/**
 * Implementação mock do caso de uso para buscar recursos
 */
class MockSearchResourcesUseCase : SearchResourcesUseCase {
    override fun invoke(query: String): Flow<Result<List<Resource>>> = flow {
        // Simulate delay
        kotlinx.coroutines.delay(200)
        
        // Se a consulta estiver vazia, retornar todos os recursos
        if (query.isBlank()) {
            emit(Result.success(generateMockResources(15)))
            return@flow
        }
        
        // Filtrar recursos que correspondem à consulta
        val filteredResources = generateMockResources(15).filter { resource ->
            resource.title.contains(query, ignoreCase = true) ||
            resource.description.contains(query, ignoreCase = true) ||
            resource.tags.any { it.contains(query, ignoreCase = true) }
        }
        
        emit(Result.success(filteredResources))
    }
    
    private fun generateMockResources(count: Int): List<Resource> {
        return List(count) { index ->
            val id = index.toLong() + 1
            Resource(
                id = id,
                title = "Recurso #$id",
                description = "Descrição do recurso #$id com conteúdo informativo sobre bem-estar mental",
                type = ResourceType.values()[index % ResourceType.values().size],
                tags = listOf("exemplo", "mock", "saúde"),
                content = "Conteúdo detalhado do recurso #$id. Este texto seria mais extenso em um ambiente real.",
                isRecommended = index % 3 == 0
            )
        }
    }
}

/**
 * ViewModel para a tela de recursos
 */
class ResourcesViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ResourcesUiState(isLoading = true))
    val uiState: StateFlow<ResourcesUiState> = _uiState.asStateFlow()
    
    // Mock implementations
    private val getAllResourcesUseCase: GetAllResourcesUseCase = MockGetAllResourcesUseCase()
    private val getResourcesByTypeUseCase: GetResourcesByTypeUseCase = MockGetResourcesByTypeUseCase()
    private val searchResourcesUseCase: SearchResourcesUseCase = MockSearchResourcesUseCase()
    
    init {
        loadResources()
    }
    
    /**
     * Carrega todos os recursos
     */
    private fun loadResources() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getAllResourcesUseCase().collect { result ->
                result.fold(
                    onSuccess = { resources ->
                        _uiState.update { 
                            it.copy(
                                resources = resources,
                                filteredResources = filterResources(
                                    resources, 
                                    it.selectedType, 
                                    it.searchQuery
                                ),
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Erro ao carregar recursos"
                            )
                        }
                    }
                )
            }
        }
    }
    
    /**
     * Filtra os recursos por tipo
     */
    fun setSelectedType(type: ResourceType?) {
        _uiState.update { currentState ->
            val newFilteredResources = filterResources(
                currentState.resources,
                type,
                currentState.searchQuery
            )
            
            currentState.copy(
                selectedType = type,
                filteredResources = newFilteredResources
            )
        }
    }
    
    /**
     * Atualiza a consulta de busca
     */
    fun updateSearchQuery(query: String) {
        _uiState.update { currentState ->
            val newFilteredResources = filterResources(
                currentState.resources,
                currentState.selectedType,
                query
            )
            
            currentState.copy(
                searchQuery = query,
                filteredResources = newFilteredResources
            )
        }
    }
    
    /**
     * Aplica os filtros aos recursos
     */
    private fun filterResources(
        resources: List<Resource>,
        type: ResourceType?,
        query: String
    ): List<Resource> {
        return resources.filter { resource ->
            val matchesType = type == null || resource.type == type
            val matchesQuery = query.isEmpty() || 
                resource.title.contains(query, ignoreCase = true) ||
                resource.description.contains(query, ignoreCase = true) ||
                resource.tags.any { it.contains(query, ignoreCase = true) }
            
            matchesType && matchesQuery
        }
    }
    
    /**
     * Recarrega os recursos
     */
    fun refreshResources() {
        loadResources()
    }
} 