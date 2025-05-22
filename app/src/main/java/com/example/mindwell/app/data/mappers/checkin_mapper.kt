package com.example.mindwell.app.data.mappers

import com.example.mindwell.app.data.model.CheckinAnswerDTO
import com.example.mindwell.app.data.model.CheckinDTO
import com.example.mindwell.app.data.model.CheckinPageDTO
import com.example.mindwell.app.domain.entities.Checkin
import com.example.mindwell.app.domain.entities.CheckinAnswer
import com.example.mindwell.app.domain.entities.CheckinPage
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Mapper para conversão entre DTOs e entidades de check-in.
 */
object CheckinMapper {
    private val dateDisplayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("pt", "BR"))
    
    /**
     * Converte DTO de página de check-ins para entidade de domínio CheckinPage.
     * @param dto DTO de página de check-ins
     * @return Entidade de domínio CheckinPage
     */
    fun mapToDomain(dto: CheckinPageDTO): CheckinPage {
        return CheckinPage(
            page = dto.page,
            size = dto.size,
            totalPages = dto.totalPages,
            totalItems = dto.totalItems,
            items = dto.items.map { mapToDomain(it) }
        )
    }
    
    /**
     * Converte DTO de check-in para entidade de domínio Checkin.
     * @param dto DTO de check-in
     * @return Entidade de domínio Checkin
     */
    fun mapToDomain(dto: CheckinDTO): Checkin {
        val timestamp = ZonedDateTime.parse(dto.timestamp)
        return Checkin(
            checkinId = dto.checkinId,
            timestamp = timestamp,
            answers = dto.answers.map { mapToDomain(it) },
            streak = dto.streak,
            date = timestamp.format(dateDisplayFormatter)
        )
    }
    
    /**
     * Converte DTO de resposta de check-in para entidade de domínio CheckinAnswer.
     * @param dto DTO de resposta de check-in
     * @return Entidade de domínio CheckinAnswer
     */
    fun mapToDomain(dto: CheckinAnswerDTO): CheckinAnswer {
        return CheckinAnswer(
            questionId = dto.questionId,
            optionId = dto.optionId,
            value = dto.value
        )
    }
} 