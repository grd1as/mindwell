package com.example.mindwell.app.domain.usecases.resource

import com.example.mindwell.app.domain.entities.ResourceCategory
import com.example.mindwell.app.domain.repositories.ResourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para obter categorias de recursos disponíveis.
 */
interface GetResourceCategoriesUseCase {
    /**
     * Obtém categorias de recursos disponíveis.
     * @return Flow com o resultado contendo a lista de categorias
     */
    operator fun invoke(): Flow<Result<List<ResourceCategory>>>
}

/**
 * Implementação do caso de uso para obter categorias de recursos.
 */
class GetResourceCategoriesUseCaseImpl @Inject constructor(
    private val resource_repository: ResourceRepository
) : GetResourceCategoriesUseCase {
    override operator fun invoke(): Flow<Result<List<ResourceCategory>>> = flow {
        try {
            val categories = resource_repository.get_resource_categories()
            emit(Result.success(categories))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 