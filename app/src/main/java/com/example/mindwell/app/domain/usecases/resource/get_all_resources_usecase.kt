package com.example.mindwell.app.domain.usecases.resource

import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.repositories.ResourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Caso de uso para obter todos os recursos.
 */
interface GetAllResourcesUseCase {
    /**
     * Obtém todos os recursos disponíveis.
     * @return Flow com todos os recursos
     */
    operator fun invoke(): Flow<Result<List<Resource>>>
}

/**
 * Implementação do caso de uso para obter todos os recursos.
 */
class GetAllResourcesUseCaseImpl(
    private val resourceRepository: ResourceRepository
) : GetAllResourcesUseCase {
    /**
     * Obtém todos os recursos disponíveis.
     * @return Flow com todos os recursos
     */
    override operator fun invoke(): Flow<Result<List<Resource>>> {
        return resourceRepository.getAllResources()
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }
} 