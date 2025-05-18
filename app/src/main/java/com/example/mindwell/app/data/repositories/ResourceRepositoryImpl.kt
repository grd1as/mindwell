package com.example.mindwell.app.data.repositories

import com.example.mindwell.app.data.datasources.local.dao.ResourceDao
import com.example.mindwell.app.data.mappers.ResourceMapper
import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.entities.ResourceType
import com.example.mindwell.app.domain.repositories.ResourceRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório para operações de recursos educativos.
 */
@Singleton
class ResourceRepositoryImpl @Inject constructor(
    private val resourceDao: ResourceDao
) : ResourceRepository {

    private val gson = Gson()

    override suspend fun saveResource(resource: Resource): Long {
        val entity = ResourceMapper.toEntity(resource)
        return resourceDao.insert(entity)
    }

    override suspend fun getResourceById(id: Long): Resource? {
        val entity = resourceDao.getById(id) ?: return null
        return ResourceMapper.toDomainModel(entity)
    }

    override fun getAllResources(): Flow<List<Resource>> {
        return resourceDao.getAll()
            .map { entities ->
                ResourceMapper.toDomainModelList(entities)
            }
    }

    override fun getResourcesByType(type: ResourceType): Flow<List<Resource>> {
        return resourceDao.getByType(type)
            .map { entities ->
                ResourceMapper.toDomainModelList(entities)
            }
    }

    override fun getRecommendedResources(): Flow<List<Resource>> {
        return resourceDao.getRecommended()
            .map { entities ->
                ResourceMapper.toDomainModelList(entities)
            }
    }

    override fun searchResources(query: String): Flow<List<Resource>> {
        return resourceDao.search(query)
            .map { entities ->
                ResourceMapper.toDomainModelList(entities)
            }
    }

    override fun getResourcesByTags(tags: List<String>): Flow<List<Resource>> {
        // Para busca por tags, precisamos buscar todos os recursos e filtrar com base nas tags,
        // já que o Room não suporta facilmente busca em coleções serializadas
        return resourceDao.getAll()
            .map { entities ->
                val resources = ResourceMapper.toDomainModelList(entities)
                
                // Filtrar por tags
                val lowercaseTags = tags.map { it.lowercase() }
                resources.filter { resource ->
                    resource.tags.any { tag -> 
                        lowercaseTags.any { searchTag -> tag.lowercase().contains(searchTag) }
                    }
                }
            }
    }

    override suspend fun deleteResource(id: Long): Boolean {
        return resourceDao.delete(id) > 0
    }
} 