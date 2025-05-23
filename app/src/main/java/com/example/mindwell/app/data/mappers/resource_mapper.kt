package com.example.mindwell.app.data.mappers

import com.example.mindwell.app.data.model.ResourceDTO
import com.example.mindwell.app.data.model.ResourceDetailDTO
import com.example.mindwell.app.data.model.ResourceCategoryDTO
import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.entities.ResourceDetail
import com.example.mindwell.app.domain.entities.ResourceCategory

/**
 * Mapper para conversão entre DTOs e entidades de recurso.
 */
object ResourceMapper {
    /**
     * Converte DTO de recurso para entidade de domínio Resource.
     * @param dto DTO de recurso
     * @return Entidade de domínio Resource
     */
    fun mapToDomain(dto: ResourceDTO): Resource {
        return Resource(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            categoryId = dto.categoryId,
            durationMinutes = dto.durationMinutes
        )
    }
    
    /**
     * Converte lista de DTOs de recurso para lista de entidades de domínio Resource.
     * @param dtos Lista de DTOs de recurso
     * @return Lista de entidades de domínio Resource
     */
    fun mapToDomain(dtos: List<ResourceDTO>): List<Resource> {
        return dtos.map { mapToDomain(it) }
    }
    
    /**
     * Converte DTO de detalhe de recurso para entidade de domínio ResourceDetail.
     * @param dto DTO de detalhe de recurso
     * @return Entidade de domínio ResourceDetail
     */
    fun mapDetailToDomain(dto: ResourceDetailDTO): ResourceDetail {
        return ResourceDetail(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            categoryId = dto.categoryId,
            durationMinutes = dto.durationMinutes,
            steps = dto.steps,
            completed = dto.completed
        )
    }
    
    /**
     * Converte DTO de categoria de recurso para entidade de domínio ResourceCategory.
     * @param dto DTO de categoria de recurso
     * @return Entidade de domínio ResourceCategory
     */
    fun mapCategoryToDomain(dto: ResourceCategoryDTO): ResourceCategory {
        return ResourceCategory(
            id = dto.id,
            title = dto.title,
            description = dto.description
        )
    }
    
    /**
     * Converte lista de DTOs de categoria para lista de entidades de domínio ResourceCategory.
     * @param dtos Lista de DTOs de categoria
     * @return Lista de entidades de domínio ResourceCategory
     */
    fun mapCategoriesToDomain(dtos: List<ResourceCategoryDTO>): List<ResourceCategory> {
        return dtos.map { mapCategoryToDomain(it) }
    }
} 