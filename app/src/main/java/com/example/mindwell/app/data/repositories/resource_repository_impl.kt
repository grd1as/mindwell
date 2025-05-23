package com.example.mindwell.app.data.repositories

import android.util.Log
import com.example.mindwell.app.data.datasources.remote.ResourceRemoteDataSource
import com.example.mindwell.app.data.mappers.ResourceMapper
import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.entities.ResourceDetail
import com.example.mindwell.app.domain.entities.ResourceCategory
import com.example.mindwell.app.domain.repositories.ResourceRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de recursos que usa a API.
 */
@Singleton
class ResourceRepositoryImpl @Inject constructor(
    private val remoteDataSource: ResourceRemoteDataSource
) : ResourceRepository {
    private val TAG = "ResourceRepository"
    
    override suspend fun get_resources(category: String?): List<Resource> {
        Log.d(TAG, "🌐 Tentando buscar recursos da API (categoria: $category)")
        try {
            val resourceDTOs = remoteDataSource.get_resources(category)
            Log.d(TAG, "✅ Sucesso na busca de ${resourceDTOs.size} recursos da API")
            return ResourceMapper.mapToDomain(resourceDTOs)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro na busca de recursos da API: ${e.message}")
            throw e
        }
    }
    
    override suspend fun get_resource_detail(resource_id: String): ResourceDetail {
        Log.d(TAG, "🌐 Tentando buscar detalhes do recurso $resource_id da API")
        try {
            val resourceDetailDTO = remoteDataSource.get_resource_detail(resource_id)
            Log.d(TAG, "✅ Sucesso na busca de detalhes do recurso da API")
            return ResourceMapper.mapDetailToDomain(resourceDetailDTO)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro na busca de detalhes do recurso da API: ${e.message}")
            throw e
        }
    }
    
    override suspend fun get_resource_categories(): List<ResourceCategory> {
        Log.d(TAG, "🌐 Tentando buscar categorias de recursos da API")
        try {
            val categoryDTOs = remoteDataSource.get_resource_categories()
            Log.d(TAG, "✅ Sucesso na busca de ${categoryDTOs.size} categorias da API")
            return ResourceMapper.mapCategoriesToDomain(categoryDTOs)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro na busca de categorias da API: ${e.message}")
            throw e
        }
    }
} 