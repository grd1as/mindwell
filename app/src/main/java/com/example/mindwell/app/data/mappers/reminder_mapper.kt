package com.example.mindwell.app.data.mappers

import com.example.mindwell.app.data.model.ReminderDTO
import com.example.mindwell.app.domain.entities.Reminder
import java.time.ZonedDateTime

/**
 * Mapper para conversão entre DTOs e entidades de lembrete.
 */
object ReminderMapper {
    /**
     * Converte DTO de lembrete para entidade de domínio Reminder.
     * @param dto DTO de lembrete
     * @return Entidade de domínio Reminder
     */
    fun mapToDomain(dto: ReminderDTO): Reminder {
        return Reminder(
            formId = dto.formId,
            title = dto.title,
            scheduled = ZonedDateTime.parse(dto.scheduled)
        )
    }
    
    /**
     * Converte lista de DTOs de lembrete para lista de entidades de domínio Reminder.
     * @param dtos Lista de DTOs de lembrete
     * @return Lista de entidades de domínio Reminder
     */
    fun mapToDomain(dtos: List<ReminderDTO>): List<Reminder> {
        return dtos.map { mapToDomain(it) }
    }
}