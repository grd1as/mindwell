package com.example.mindwell.app.data.mappers

import com.example.mindwell.app.data.model.*
import com.example.mindwell.app.domain.entities.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

/**
 * Mapper para conversão entre DTOs e entidades de formulário.
 */
object FormMapper {
    // Formatter personalizado para lidar com diferentes formatos de data
    private val customDateTimeFormatter = DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
        .optionalStart()
        .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
        .optionalEnd()
        .toFormatter()
    
    /**
     * Converte DTO de formulário para entidade de domínio Form.
     * @param dto DTO de formulário
     * @return Entidade de domínio Form
     */
    fun mapToDomain(dto: FormDTO): Form {
        return Form(
            id = dto.id,
            code = dto.code,
            name = dto.name,
            type = dto.type,
            description = dto.description,
            nextAllowed = dto.nextAllowed?.let { parseDateTime(it) },
            lastAnsweredAt = dto.lastAnsweredAt?.let { parseDateTime(it) }
        )
    }
    
    /**
     * Faz o parsing de data com suporte a diferentes formatos.
     * @param dateTimeString String da data/hora
     * @return ZonedDateTime parseado
     */
    private fun parseDateTime(dateTimeString: String): ZonedDateTime {
        return try {
            // Tenta parsing padrão primeiro
            ZonedDateTime.parse(dateTimeString)
        } catch (e: Exception) {
            try {
                // Tenta parsing com formatter customizado
                ZonedDateTime.parse(dateTimeString, customDateTimeFormatter.withZone(java.time.ZoneId.systemDefault()))
            } catch (e2: Exception) {
                // Se falhar, adiciona o timezone UTC
                ZonedDateTime.parse("${dateTimeString}Z")
            }
        }
    }
    
    /**
     * Converte lista de DTOs de formulário para lista de entidades de domínio Form.
     * @param dtos Lista de DTOs de formulário
     * @return Lista de entidades de domínio Form
     */
    fun mapToDomain(dtos: List<FormDTO>): List<Form> {
        return dtos.map { mapToDomain(it) }
    }
    
    /**
     * Converte DTO de detalhe de formulário para entidade de domínio FormDetail.
     * @param dto DTO de detalhe de formulário
     * @return Entidade de domínio FormDetail
     */
    fun mapToDomain(dto: FormDetailDTO): FormDetail {
        return FormDetail(
            id = dto.id,
            code = dto.code,
            name = dto.name,
            questions = dto.questions.map { mapToDomain(it) }
        )
    }
    
    /**
     * Converte DTO de pergunta para entidade de domínio Question.
     * @param dto DTO de pergunta
     * @return Entidade de domínio Question
     */
    fun mapToDomain(dto: QuestionDTO): Question {
        return Question(
            id = dto.id,
            ordinal = dto.ordinal,
            type = dto.type,
            text = dto.text,
            options = dto.options.map { mapToDomain(it) }
        )
    }
    
    /**
     * Converte DTO de opção para entidade de domínio Option.
     * @param dto DTO de opção
     * @return Entidade de domínio Option
     */
    fun mapToDomain(dto: OptionDTO): Option {
        return Option(
            id = dto.id,
            value = dto.value,
            label = dto.label
        )
    }
    
    /**
     * Converte entidade de domínio Answer para DTO de resposta.
     * @param domain Entidade de domínio Answer
     * @return DTO de resposta
     */
    fun mapToDto(domain: Answer): AnswerDTO {
        return AnswerDTO(
            question_id = domain.question_id,
            option_id = domain.option_id
        )
    }
    
    /**
     * Converte lista de entidades de domínio Answer para lista de DTOs de resposta.
     * @param domains Lista de entidades de domínio Answer
     * @return Lista de DTOs de resposta
     */
    fun mapToDto(domains: List<Answer>): List<AnswerDTO> {
        return domains.map { mapToDto(it) }
    }
} 