package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.entities.ResourceDetail
import com.example.mindwell.app.domain.entities.ResourceCategory

/**
 * Interface para o repositório de recursos.
 */
interface ResourceRepository {
    /**
     * Obtém a lista de recursos disponíveis.
     * @param category Categoria opcional para filtrar recursos
     * @return Lista de recursos
     */
    suspend fun get_resources(category: String? = null): List<Resource>
    
    /**
     * Obtém os detalhes de um recurso específico.
     * @param resource_id ID do recurso
     * @return Detalhes do recurso
     */
    suspend fun get_resource_detail(resource_id: String): ResourceDetail
    
    /**
     * Obtém as categorias de recursos disponíveis.
     * @return Lista de categorias de recursos
     */
    suspend fun get_resource_categories(): List<ResourceCategory>
} 