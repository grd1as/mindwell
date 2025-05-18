package com.example.mindwell.app.domain.usecases.resource

import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.repositories.ResourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Caso de uso para obter recursos por tags.
 */
class GetResourcesByTagsUseCase @Inject constructor(
    private val resourceRepository: ResourceRepository
) {
    /**
     * Obtém recursos que contenham as tags especificadas.
     * @param tags Lista de tags a serem filtradas
     * @return Flow com os recursos encontrados
     */
    operator fun invoke(tags: List<String>): Flow<Result<List<Resource>>> {
        require(tags.isNotEmpty()) { "A lista de tags não pode estar vazia" }
        
        return resourceRepository.getResourcesByTags(tags)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }
} 