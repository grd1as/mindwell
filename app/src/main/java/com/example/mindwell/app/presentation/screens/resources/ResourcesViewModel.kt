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
import com.example.mindwell.app.domain.usecases.resource.GetResourcesUseCase
import com.example.mindwell.app.domain.usecases.resource.GetResourceCategoriesUseCase
import com.example.mindwell.app.domain.usecases.resource.GetPersonalizedResourcesUseCase
import com.example.mindwell.app.domain.usecases.resource.GetPersonalizedTipsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResourcesViewModel @Inject constructor(
    private val getResourcesUseCase: GetResourcesUseCase,
    private val getResourceCategoriesUseCase: GetResourceCategoriesUseCase,
    private val getPersonalizedResourcesUseCase: GetPersonalizedResourcesUseCase,
    private val getPersonalizedTipsUseCase: GetPersonalizedTipsUseCase
) : ViewModel() {
    private val TAG = "ResourcesViewModel"
    
    var state by mutableStateOf(ResourcesState())
        private set
        
    private var navController: NavController? = null
    
    init {
        loadResources()
        loadPersonalizedContent()
    }
    
    fun setNavController(navController: NavController) {
        this.navController = navController
    }
    
    fun retry() {
        loadResources()
        loadPersonalizedContent()
    }
    
    private fun loadPersonalizedContent() {
        viewModelScope.launch {
            Log.d(TAG, "🤖 Carregando conteúdo personalizado do Gemini")
            
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
                                    isPersonalizedLoading = false
                                )
                            }
                            tipsResult.onFailure { exception ->
                                Log.e(TAG, "❌ Erro ao carregar dicas: ${exception.message}")
                                state = state.copy(isPersonalizedLoading = false)
                            }
                        }
                    }
                    result.onFailure { exception ->
                        Log.e(TAG, "❌ Erro ao carregar recursos personalizados: ${exception.message}")
                        state = state.copy(isPersonalizedLoading = false)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro geral ao carregar conteúdo personalizado: ${e.message}")
                state = state.copy(isPersonalizedLoading = false)
            }
        }
    }
    
    private fun loadResources() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            
            try {
                Log.d(TAG, "🌐 Tentando carregar recursos e categorias da API")
                
                // Carrega categorias primeiro
                getResourceCategoriesUseCase().collect { categoriesResult ->
                    categoriesResult.onSuccess { categories ->
                        Log.d(TAG, "✅ Sucesso ao carregar ${categories.size} categorias da API")
                        
                        // Carrega recursos
                        getResourcesUseCase().collect { resourcesResult ->
                            resourcesResult.onSuccess { resources ->
                                Log.d(TAG, "✅ Sucesso ao carregar ${resources.size} recursos da API")
                                state = state.copy(
                                    isLoading = false,
                                    categories = categories,
                                    featuredResources = resources,
                                    selectedCategoryId = "all"
                                )
                            }
                            resourcesResult.onFailure { exception ->
                                Log.e(TAG, "❌ ERRO ao carregar recursos: ${exception.message}", exception)
                                state = state.copy(
                                    isLoading = false,
                                    error = "Erro ao carregar recursos: ${exception.message}"
                                )
                            }
                        }
                    }
                    categoriesResult.onFailure { exception ->
                        Log.e(TAG, "❌ ERRO ao carregar categorias: ${exception.message}", exception)
                        state = state.copy(
                            isLoading = false,
                            error = "Erro ao carregar categorias: ${exception.message}"
                        )
                    }
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ ERRO ao carregar recursos: ${e.message}", e)
                state = state.copy(
                    isLoading = false,
                    error = "Não foi possível carregar os recursos. Verifique sua conexão e tente novamente."
                )
            }
        }
    }
    
    fun selectCategory(categoryId: String) {
        Log.d(TAG, "Categoria selecionada: $categoryId")
        
        viewModelScope.launch {
            try {
                val category = if (categoryId == "all") null else categoryId
                
                getResourcesUseCase(category).collect { result ->
                    result.onSuccess { resources ->
                        Log.d(TAG, "✅ Recursos filtrados por categoria: ${resources.size}")
                        state = state.copy(
                            featuredResources = resources,
                            selectedCategoryId = categoryId
                        )
                    }
                    result.onFailure { exception ->
                        Log.e(TAG, "❌ ERRO ao filtrar recursos: ${exception.message}", exception)
                        state = state.copy(
                            error = "Erro ao filtrar recursos: ${exception.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ ERRO ao filtrar recursos: ${e.message}", e)
            }
        }
    }
    
    fun selectResource(resourceId: String) {
        Log.d(TAG, "Recurso selecionado: $resourceId")
        navController?.navigate(AppDestinations.resourceDetail(resourceId))
    }
    
    fun refreshPersonalizedContent() {
        state = state.copy(isPersonalizedLoading = true)
        loadPersonalizedContent()
    }
}

data class ResourcesState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val categories: List<ResourceCategory> = emptyList(),
    val featuredResources: List<Resource> = emptyList(),
    val selectedCategoryId: String? = null,
    val selectedResourceId: String? = null,
    val personalizedContent: PersonalizedContentResponse? = null,
    val personalizedTips: List<PersonalizedTip> = emptyList(),
    val isPersonalizedLoading: Boolean = true
) 