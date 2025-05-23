package com.example.mindwell.app.presentation.screens.resources

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mindwell.app.common.navigation.AppDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResourcesViewModel @Inject constructor() : ViewModel() {
    
    var state by mutableStateOf(ResourcesState())
        private set
        
    private var navController: NavController? = null
    private var allResources: List<Resource> = emptyList()
    
    init {
        loadResources()
    }
    
    fun setNavController(navController: NavController) {
        this.navController = navController
    }
    
    fun retry() {
        loadResources()
    }
    
    private fun loadResources() {
        viewModelScope.launch {
            // Indica carregamento
            state = state.copy(isLoading = true, error = null)
            
            try {
                // Lista de recursos
                allResources = listOf(
                    Resource(
                        id = "breathing_478",
                        title = "Técnica de Respiração 4-7-8",
                        description = "Uma técnica simples e eficaz para reduzir ansiedade e promover relaxamento em momentos de estresse.",
                        categoryId = "breathing",
                        durationMinutes = 5
                    ),
                    Resource(
                        id = "meditation_body_scan",
                        title = "Meditação Body Scan",
                        description = "Uma meditação guiada que ajuda a reconectar com o corpo e liberar tensões acumuladas.",
                        categoryId = "meditation",
                        durationMinutes = 10
                    ),
                    Resource(
                        id = "journaling_gratitude",
                        title = "Diário de Gratidão",
                        description = "Prática diária para focar nos aspectos positivos da vida e cultivar uma perspectiva otimista.",
                        categoryId = "journaling",
                        durationMinutes = 5
                    ),
                    Resource(
                        id = "exercise_simple",
                        title = "Exercícios Simples para Bem-Estar",
                        description = "Pequenas atividades físicas que podem ser realizadas em qualquer lugar para melhorar o humor e a disposição.",
                        categoryId = "exercise",
                        durationMinutes = 15
                    ),
                    Resource(
                        id = "sleep_routine",
                        title = "Rotina para Qualidade do Sono",
                        description = "Passos práticos para estabelecer uma rotina que promove melhor qualidade de sono e descanso.",
                        categoryId = "sleep",
                        durationMinutes = 7
                    )
                )
                
                // Simula o carregamento de recursos
                // Em um cenário real, isso viria de um repositório
                state = ResourcesState(
                    categories = listOf(
                        ResourceCategory(
                            id = "all",
                            title = "Todos",
                            description = "Todos os recursos disponíveis"
                        ),
                        ResourceCategory(
                            id = "breathing",
                            title = "Exercícios de Respiração",
                            description = "Técnicas para acalmar a mente e reduzir a ansiedade"
                        ),
                        ResourceCategory(
                            id = "meditation",
                            title = "Meditação",
                            description = "Guias para meditação mindfulness e relaxamento"
                        ),
                        ResourceCategory(
                            id = "journaling",
                            title = "Diário Reflexivo",
                            description = "Prompts e técnicas para escrita terapêutica"
                        ),
                        ResourceCategory(
                            id = "exercise",
                            title = "Atividade Física",
                            description = "Exercícios simples para melhorar o humor"
                        ),
                        ResourceCategory(
                            id = "sleep",
                            title = "Sono Saudável",
                            description = "Dicas para melhorar a qualidade do sono"
                        )
                    ),
                    featuredResources = allResources,  // Inicialmente mostra todos os recursos
                    selectedCategoryId = "all"  // Inicia com "Todos" selecionado
                )
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = "Não foi possível carregar os recursos. Verifique sua conexão e tente novamente."
                )
            }
        }
    }
    
    fun selectCategory(categoryId: String) {
        if (categoryId == "all") {
            // Se a categoria "Todos" for selecionada, mostrar todos os recursos
            state = state.copy(
                selectedCategoryId = categoryId,
                featuredResources = allResources
            )
        } else {
            // Filtra os recursos pela categoria selecionada
            val filteredResources = allResources.filter { it.categoryId == categoryId }
            state = state.copy(
                selectedCategoryId = categoryId,
                featuredResources = filteredResources
            )
        }
    }
    
    fun selectResource(resourceId: String) {
        // Navegar para a tela de detalhes do recurso
        navController?.navigate(AppDestinations.resourceDetail(resourceId))
    }
}

data class ResourcesState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val categories: List<ResourceCategory> = emptyList(),
    val featuredResources: List<Resource> = emptyList(),
    val selectedCategoryId: String? = null,
    val selectedResourceId: String? = null
)

data class ResourceCategory(
    val id: String,
    val title: String,
    val description: String
)

data class Resource(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val durationMinutes: Int
) 