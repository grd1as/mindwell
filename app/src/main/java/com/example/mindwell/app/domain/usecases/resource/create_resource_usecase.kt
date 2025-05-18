package com.example.mindwell.app.domain.usecases.resource

import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.repositories.ResourceRepository
import javax.inject.Inject

/**
 * Caso de uso para criar um novo recurso educativo.
 */
class CreateResourceUseCase @Inject constructor(
    private val resourceRepository: ResourceRepository
) {
    /**
     * Cria um novo recurso educativo.
     * @param resource O recurso a ser criado
     * @return ID do recurso criado
     */
    suspend operator fun invoke(resource: Resource): Result<Long> = runCatching {
        resourceRepository.saveResource(resource)
    }
} 