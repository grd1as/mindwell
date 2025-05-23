package com.example.mindwell.app.domain.usecases.resource

import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.repositories.ResourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para obter lista de recursos disponíveis.
 */
interface GetResourcesUseCase {
    /**
     * Obtém lista de recursos disponíveis.
     * @param category Categoria opcional para filtrar recursos
     * @return Flow com o resultado contendo a lista de recursos
     */
    operator fun invoke(category: String? = null): Flow<Result<List<Resource>>>
}

/**
 * Implementação do caso de uso para obter lista de recursos.
 */
class GetResourcesUseCaseImpl @Inject constructor(
    private val resource_repository: ResourceRepository
) : GetResourcesUseCase {
    override operator fun invoke(category: String?): Flow<Result<List<Resource>>> = flow {
        try {
            val resources = resource_repository.get_resources(category)
            emit(Result.success(resources))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 