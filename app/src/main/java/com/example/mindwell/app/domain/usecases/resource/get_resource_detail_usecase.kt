package com.example.mindwell.app.domain.usecases.resource

import com.example.mindwell.app.domain.entities.ResourceDetail
import com.example.mindwell.app.domain.repositories.ResourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para obter detalhes de um recurso específico.
 */
interface GetResourceDetailUseCase {
    /**
     * Obtém detalhes de um recurso específico.
     * @param resource_id ID do recurso
     * @return Flow com o resultado contendo os detalhes do recurso
     */
    operator fun invoke(resource_id: String): Flow<Result<ResourceDetail>>
}

/**
 * Implementação do caso de uso para obter detalhes de um recurso.
 */
class GetResourceDetailUseCaseImpl @Inject constructor(
    private val resource_repository: ResourceRepository
) : GetResourceDetailUseCase {
    override operator fun invoke(resource_id: String): Flow<Result<ResourceDetail>> = flow {
        try {
            val resourceDetail = resource_repository.get_resource_detail(resource_id)
            emit(Result.success(resourceDetail))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 