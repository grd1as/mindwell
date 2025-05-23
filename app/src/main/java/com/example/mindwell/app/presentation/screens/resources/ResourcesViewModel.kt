package com.example.mindwell.app.presentation.screens.resources

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mindwell.app.common.navigation.AppDestinations
import com.example.mindwell.app.data.services.PersonalizedContentResponse
import com.example.mindwell.app.data.services.PersonalizedTip
import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.entities.ResourceCategory
import com.example.mindwell.app.domain.usecases.resource.GetPersonalizedResourcesUseCase
import com.example.mindwell.app.domain.usecases.resource.GetPersonalizedTipsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para a tela de recursos, agora funcionando apenas com Gemini AI
 */
@HiltViewModel
class ResourcesViewModel @Inject constructor(
    private val getPersonalizedResourcesUseCase: GetPersonalizedResourcesUseCase,
    private val getPersonalizedTipsUseCase: GetPersonalizedTipsUseCase
) : ViewModel() {
    private val TAG = "ResourcesViewModel"
    
    var state by mutableStateOf(ResourcesState())
        private set
        
    private var navController: NavController? = null
    
    init {
        loadPersonalizedContent()
        loadMockCategories()
    }
    
    fun setNavController(navController: NavController) {
        this.navController = navController
    }
    
    fun retry() {
        loadPersonalizedContent()
    }
    
    private fun loadPersonalizedContent() {
        viewModelScope.launch {
            Log.d(TAG, "🤖 Carregando conteúdo personalizado do Gemini")
            
            state = state.copy(isPersonalizedLoading = true)
            
            try {
                // Carregar recursos personalizados
                getPersonalizedResourcesUseCase().collect { result ->
                    result.onSuccess { personalizedContent ->
                        Log.d(TAG, "✅ Recursos personalizados carregados: ${personalizedContent.resources.size}")
                        
                        // Carregar dicas personalizadas
                        getPersonalizedTipsUseCase().collect { tipsResult ->
                            tipsResult.onSuccess { tips ->
                                Log.d(TAG, "✅ Dicas personalizadas carregadas: ${tips.size}")
                                
                                state = state.copy(
                                    personalizedContent = personalizedContent,
                                    personalizedTips = tips,
                                    isPersonalizedLoading = false,
                                    isLoading = false
                                )
                            }
                            tipsResult.onFailure { exception ->
                                Log.e(TAG, "❌ Erro ao carregar dicas: ${exception.message}")
                                state = state.copy(
                                    isPersonalizedLoading = false,
                                    isLoading = false,
                                    error = "Erro ao carregar dicas personalizadas"
                                )
                            }
                        }
                    }
                    result.onFailure { exception ->
                        Log.e(TAG, "❌ Erro ao carregar recursos personalizados: ${exception.message}")
                        state = state.copy(
                            isPersonalizedLoading = false,
                            isLoading = false,
                            error = "Erro ao carregar recursos personalizados"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ ERRO geral: ${e.message}", e)
                state = state.copy(
                    isPersonalizedLoading = false,
                    isLoading = false,
                    error = "Erro inesperado ao carregar conteúdo"
                )
            }
        }
    }
    
    private fun loadMockCategories() {
        // Carregar categorias mock que se alinham com as do Gemini
        val mockCategories = listOf(
            ResourceCategory(
                id = "all",
                title = "Todos",
                description = "Todos os tipos de recursos"
            ),
            ResourceCategory(
                id = "breathing",
                title = "Respiração",
                description = "Exercícios de respiração e relaxamento"
            ),
            ResourceCategory(
                id = "meditation",
                title = "Meditação",
                description = "Práticas de mindfulness e meditação"
            ),
            ResourceCategory(
                id = "exercise",
                title = "Exercício",
                description = "Atividades físicas para bem-estar"
            ),
            ResourceCategory(
                id = "sleep",
                title = "Sono",
                description = "Técnicas para melhorar a qualidade do sono"
            ),
            ResourceCategory(
                id = "journaling",
                title = "Diário",
                description = "Práticas de escrita reflexiva"
            ),
            ResourceCategory(
                id = "mindfulness",
                title = "Mindfulness",
                description = "Atenção plena e consciência do momento presente"
            )
        )
        
        state = state.copy(
            categories = mockCategories,
            selectedCategoryId = "all"
        )
    }
    
    fun selectCategory(categoryId: String) {
        Log.d(TAG, "Categoria selecionada: $categoryId")
        
        state = state.copy(
            selectedCategoryId = categoryId
        )
        
        // Filtrar recursos personalizados por categoria (se houver)
        state.personalizedContent?.let { content ->
            val filteredResources = if (categoryId == "all") {
                content.resources
            } else {
                content.resources.filter { it.category == categoryId }
            }
            
            Log.d(TAG, "✅ Recursos filtrados por categoria '$categoryId': ${filteredResources.size}")
        }
    }
    
    fun selectResource(resourceId: String) {
        Log.d(TAG, "Recurso selecionado: $resourceId")
        // Como não temos detalhes específicos, vamos manter a navegação simples
        // ou implementar uma tela de detalhes mock
    }
    
    fun refreshPersonalizedContent() {
        loadPersonalizedContent()
    }
}

/**
 * Estado da tela de recursos simplificado para usar apenas Gemini
 */
data class ResourcesState(
    val isLoading: Boolean = true,
    val isPersonalizedLoading: Boolean = false,
    val error: String? = null,
    val categories: List<ResourceCategory> = emptyList(),
    val selectedCategoryId: String? = null,
    val personalizedContent: PersonalizedContentResponse? = null,
    val personalizedTips: List<PersonalizedTip> = emptyList(),
    // Manter compatibilidade com a UI existente
    val featuredResources: List<Resource> = emptyList()
) 