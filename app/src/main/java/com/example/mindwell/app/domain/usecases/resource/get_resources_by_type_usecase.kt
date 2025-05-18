package com.example.mindwell.app.domain.usecases.resource

import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.entities.ResourceType
import com.example.mindwell.app.domain.repositories.ResourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Caso de uso para obter recursos por tipo.
 */
interface GetResourcesByTypeUseCase {
    /**
     * Obtém recursos de um tipo específico.
     * @param type Tipo de recurso a ser filtrado
     * @return Flow com os recursos do tipo especificado
     */
    operator fun invoke(type: ResourceType): Flow<Result<List<Resource>>>
}

/**
 * Implementação do caso de uso para obter recursos por tipo.
 */
class GetResourcesByTypeUseCaseImpl(
    private val resourceRepository: ResourceRepository
) : GetResourcesByTypeUseCase {
    /**
     * Obtém recursos de um tipo específico.
     * @param type Tipo de recurso a ser filtrado
     * @return Flow com os recursos do tipo especificado
     */
    override operator fun invoke(type: ResourceType): Flow<Result<List<Resource>>> {
        return resourceRepository.getResourcesByType(type)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }
} 