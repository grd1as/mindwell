package com.example.mindwell.app.data.mappers

import com.example.mindwell.app.data.model.CheckinAnswerDTO
import com.example.mindwell.app.data.model.CheckinDTO
import com.example.mindwell.app.data.model.CheckinPageDTO
import com.example.mindwell.app.domain.entities.Checkin
import com.example.mindwell.app.domain.entities.CheckinAnswer
import com.example.mindwell.app.domain.entities.CheckinPage
import com.example.mindwell.app.domain.entities.Emotion
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Mapper para conversão entre DTOs e entidades de check-in.
 */
object CheckinMapper {
    private val date_display_formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("pt", "BR"))
    
    /**
     * Converte DTO de página de check-ins para entidade de domínio CheckinPage.
     * @param dto DTO de página de check-ins
     * @return Entidade de domínio CheckinPage
     */
    fun mapToDomain(dto: CheckinPageDTO): CheckinPage {
        return CheckinPage(
            page = dto.page,
            size = dto.size,
            total_pages = dto.total_pages,
            total_items = dto.total_items,
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
        
        // Criar uma emoção mock apenas para satisfazer o construtor
        // Idealmente, esta informação deveria vir das respostas ou de outro mapeamento
        val mock_emotion = Emotion(
            id = 1,
            name = "Normal",
            emoji = "😐",
            value = 2
        )
        
        return Checkin(
            id = dto.checkin_id.toLong(),
            date = timestamp.format(date_display_formatter),
            emotion = mock_emotion,
            streak = dto.streak,
            note = null // Não temos esta informação no DTO
        )
    }
    
    /**
     * Converte DTO de resposta de check-in para entidade de domínio CheckinAnswer.
     * @param dto DTO de resposta de check-in
     * @return Entidade de domínio CheckinAnswer
     */
    fun mapToDomain(dto: CheckinAnswerDTO): CheckinAnswer {
        return CheckinAnswer(
            question_id = dto.question_id,
            option_id = dto.option_id,
            value = dto.value
        )
    }
} 