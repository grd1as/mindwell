package com.example.mindwell.app.domain.usecases.resource

import com.example.mindwell.app.domain.repositories.ResourceRepository
import javax.inject.Inject

/**
 * Caso de uso para excluir um recurso.
 */
class DeleteResourceUseCase @Inject constructor(
    private val resourceRepository: ResourceRepository
) {
    /**
     * Exclui um recurso pelo ID.
     * @param id ID do recurso a ser excluído
     * @return true se o recurso foi excluído com sucesso
     */
    suspend operator fun invoke(id: Long): Result<Boolean> = runCatching {
        resourceRepository.deleteResource(id)
    }
} 