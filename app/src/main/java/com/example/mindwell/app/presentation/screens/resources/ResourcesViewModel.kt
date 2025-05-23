package com.example.mindwell.app.presentation.screens.resources

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mindwell.app.common.navigation.AppDestinations
import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.entities.ResourceCategory
import com.example.mindwell.app.domain.usecases.resource.GetResourcesUseCase
import com.example.mindwell.app.domain.usecases.resource.GetResourceCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResourcesViewModel @Inject constructor(
    private val getResourcesUseCase: GetResourcesUseCase,
    private val getResourceCategoriesUseCase: GetResourceCategoriesUseCase
) : ViewModel() {
    private val TAG = "ResourcesViewModel"
    
    var state by mutableStateOf(ResourcesState())
        private set
        
    private var navController: NavController? = null
    
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
}

data class ResourcesState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val categories: List<ResourceCategory> = emptyList(),
    val featuredResources: List<Resource> = emptyList(),
    val selectedCategoryId: String? = null,
    val selectedResourceId: String? = null
) 