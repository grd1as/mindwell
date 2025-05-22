package com.example.mindwell.app.data.mappers

import com.example.mindwell.app.data.model.PreferenceDTO
import com.example.mindwell.app.domain.entities.Preference

/**
 * Mapper para conversão entre DTOs e entidades de preferência.
 */
object PreferenceMapper {
    /**
     * Converte DTO de preferência para entidade de domínio Preference.
     * @param dto DTO de preferência
     * @return Entidade de domínio Preference
     */
    fun mapToDomain(dto: PreferenceDTO): Preference {
        return Preference(
            name = dto.name,
            notificationsEnabled = dto.notificationsEnabled
        )
    }
    
    /**
     * Converte entidade de domínio Preference para DTO de preferência.
     * @param domain Entidade de domínio Preference
     * @return DTO de preferência
     */
    fun mapToDto(domain: Preference): PreferenceDTO {
        return PreferenceDTO(
            name = domain.name,
            notificationsEnabled = domain.notificationsEnabled
        )
    }
} 