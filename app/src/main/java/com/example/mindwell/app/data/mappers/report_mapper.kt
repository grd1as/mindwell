package com.example.mindwell.app.data.mappers

import com.example.mindwell.app.data.model.ReportDTO
import com.example.mindwell.app.domain.entities.Report

/**
 * Mapper para conversão entre DTOs e entidades de denúncia/report.
 */
object ReportMapper {
    /**
     * Converte entidade de domínio Report para DTO de denúncia.
     * @param domain Entidade de domínio Report
     * @return DTO de denúncia
     */
    fun mapToDto(domain: Report): ReportDTO {
        return ReportDTO(
            category = domain.category,
            description = domain.description,
            tags = domain.tags
        )
    }
} 