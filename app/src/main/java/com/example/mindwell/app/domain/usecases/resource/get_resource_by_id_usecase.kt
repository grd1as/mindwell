package com.example.mindwell.app.domain.usecases.resource

import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.repositories.ResourceRepository

/**
 * Caso de uso para obter um recurso pelo ID.
 */
interface GetResourceByIdUseCase {
    /**
     * Obtém um recurso pelo ID.
     * @param id ID do recurso a ser obtido
     * @return O recurso encontrado ou null se não existir
     */
    suspend operator fun invoke(id: Long): Result<Resource?>
}

/**
 * Implementação do caso de uso para obter um recurso pelo ID.
 */
class GetResourceByIdUseCaseImpl(
    private val resourceRepository: ResourceRepository
) : GetResourceByIdUseCase {
    /**
     * Obtém um recurso pelo ID.
     * @param id ID do recurso a ser obtido
     * @return O recurso encontrado ou null se não existir
     */
    override suspend operator fun invoke(id: Long): Result<Resource?> = runCatching {
        resourceRepository.getResourceById(id)
    }
} 