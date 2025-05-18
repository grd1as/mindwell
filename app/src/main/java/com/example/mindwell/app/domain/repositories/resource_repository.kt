package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.entities.ResourceType
import kotlinx.coroutines.flow.Flow

/**
 * Interface para acesso a operações de recursos educativos.
 */
interface ResourceRepository {
    /**
     * Salva um novo recurso.
     * @return ID do recurso salvo
     */
    suspend fun saveResource(resource: Resource): Long
    
    /**
     * Obtém um recurso específico pelo ID.
     */
    suspend fun getResourceById(id: Long): Resource?
    
    /**
     * Obtém todos os recursos.
     */
    fun getAllResources(): Flow<List<Resource>>
    
    /**
     * Obtém recursos de um tipo específico.
     */
    fun getResourcesByType(type: ResourceType): Flow<List<Resource>>
    
    /**
     * Obtém recursos recomendados.
     */
    fun getRecommendedResources(): Flow<List<Resource>>
    
    /**
     * Busca recursos por texto.
     */
    fun searchResources(query: String): Flow<List<Resource>>
    
    /**
     * Busca recursos por tags.
     */
    fun getResourcesByTags(tags: List<String>): Flow<List<Resource>>
    
    /**
     * Exclui um recurso pelo ID.
     * @return true se o recurso foi excluído com sucesso
     */
    suspend fun deleteResource(id: Long): Boolean
}
