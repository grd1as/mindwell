package com.example.mindwell.app.data.mappers

import com.example.mindwell.app.data.model.FeelingDTO
import com.example.mindwell.app.domain.entities.Feeling

/**
 * Mapper para conversão entre DTOs e entidades de sentimento.
 */
object FeelingMapper {
    /**
     * Converte DTO de sentimento para entidade de domínio Feeling.
     * @param dto DTO de sentimento
     * @return Entidade de domínio Feeling
     */
    fun mapToDomain(dto: FeelingDTO): Feeling {
        return Feeling(
            id = dto.id,
            label = dto.label,
            emoji = dto.emoji,
            value = dto.value
        )
    }
    
    /**
     * Converte lista de DTOs de sentimento para lista de entidades de domínio Feeling.
     * @param dtos Lista de DTOs de sentimento
     * @return Lista de entidades de domínio Feeling
     */
    fun mapToDomain(dtos: List<FeelingDTO>): List<Feeling> {
        return dtos.map { mapToDomain(it) }
    }
} 