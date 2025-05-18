package com.example.mindwell.app.domain.usecases.resource

import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.repositories.ResourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Caso de uso para obter recursos recomendados.
 */
class GetRecommendedResourcesUseCase @Inject constructor(
    private val resourceRepository: ResourceRepository
) {
    /**
     * Obtém recursos recomendados para o usuário.
     * @return Flow com os recursos recomendados
     */
    operator fun invoke(): Flow<Result<List<Resource>>> {
        return resourceRepository.getRecommendedResources()
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }
} 