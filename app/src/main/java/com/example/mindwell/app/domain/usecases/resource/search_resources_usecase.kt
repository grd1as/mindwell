package com.example.mindwell.app.domain.usecases.resource

import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.repositories.ResourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Caso de uso para buscar recursos por texto.
 */
interface SearchResourcesUseCase {
    /**
     * Busca recursos que contenham o texto fornecido.
     * @param query Texto a ser buscado nos recursos
     * @return Flow com os recursos encontrados
     */
    operator fun invoke(query: String): Flow<Result<List<Resource>>>
}

/**
 * Implementação do caso de uso para buscar recursos por texto.
 */
class SearchResourcesUseCaseImpl(
    private val resourceRepository: ResourceRepository
) : SearchResourcesUseCase {
    /**
     * Busca recursos que contenham o texto fornecido.
     * @param query Texto a ser buscado nos recursos
     * @return Flow com os recursos encontrados
     */
    override operator fun invoke(query: String): Flow<Result<List<Resource>>> {
        if (query.isBlank()) {
            return resourceRepository.getAllResources()
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
        }
        
        return resourceRepository.searchResources(query.trim())
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }
} 