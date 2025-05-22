package com.example.mindwell.app.data.mappers

import com.example.mindwell.app.data.model.SummaryDTO
import com.example.mindwell.app.data.model.SummaryItemDTO
import com.example.mindwell.app.domain.entities.Summary
import com.example.mindwell.app.domain.entities.SummaryItem

/**
 * Mapper para conversão entre DTOs e entidades de resumo.
 */
object SummaryMapper {
    /**
     * Converte DTO de resumo para entidade de domínio Summary.
     * @param dto DTO de resumo
     * @return Entidade de domínio Summary
     */
    fun mapToDomain(dto: SummaryDTO): Summary {
        return Summary(
            total = dto.total,
            breakdown = dto.breakdown.map { mapToDomain(it) },
            overallLevel = dto.overallLevel
        )
    }
    
    /**
     * Converte DTO de item de resumo para entidade de domínio SummaryItem.
     * @param dto DTO de item de resumo
     * @return Entidade de domínio SummaryItem
     */
    fun mapToDomain(dto: SummaryItemDTO): SummaryItem {
        return SummaryItem(
            value = dto.value,
            count = dto.count,
            percent = dto.percent,
            level = dto.level
        )
    }
} 