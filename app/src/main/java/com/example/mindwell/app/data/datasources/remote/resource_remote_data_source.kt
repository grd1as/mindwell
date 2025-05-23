package com.example.mindwell.app.data.datasources.remote

import com.example.mindwell.app.data.model.ResourceDTO
import com.example.mindwell.app.data.model.ResourceDetailDTO
import com.example.mindwell.app.data.model.ResourceCategoryDTO
import com.example.mindwell.app.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fonte de dados remota para recursos.
 */
@Singleton
class ResourceRemoteDataSource @Inject constructor(
    private val api_service: ApiService
) {
    /**
     * Obtém a lista de recursos disponíveis.
     * @param category Categoria opcional para filtrar recursos
     * @return Lista de recursos
     */
    suspend fun get_resources(category: String? = null): List<ResourceDTO> {
        return api_service.get_resources(category)
    }
    
    /**
     * Obtém os detalhes de um recurso específico.
     * @param resource_id ID do recurso
     * @return Detalhes do recurso
     */
    suspend fun get_resource_detail(resource_id: String): ResourceDetailDTO {
        return api_service.get_resource_detail(resource_id)
    }
    
    /**
     * Obtém as categorias de recursos disponíveis.
     * @return Lista de categorias de recursos
     */
    suspend fun get_resource_categories(): List<ResourceCategoryDTO> {
        return api_service.get_resource_categories()
    }
} 