package com.example.mindwell.app.data.mappers

import com.example.mindwell.app.data.datasources.local.entities.AssessmentEntity
import com.example.mindwell.app.domain.entities.Assessment
import com.example.mindwell.app.domain.entities.AssessmentQuestion
import com.example.mindwell.app.domain.entities.AssessmentResult
import com.example.mindwell.app.domain.entities.AssessmentType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Mapper para converter entre entidades de domínio e entidades de banco de dados para Assessment.
 */
object AssessmentMapper {
    private val gson = Gson()
    
    /**
     * Converte de entidade de domínio para entidade de banco de dados.
     */
    fun toEntity(domainModel: Assessment): AssessmentEntity {
        return AssessmentEntity(
            id = domainModel.id,
            type = AssessmentType.WELLBEING, // Valor padrão
            timestamp = LocalDateTime.of(domainModel.date, LocalTime.now()),
            responsesJson = gson.toJson(domainModel.questions),
            score = domainModel.result?.score ?: 0
        )
    }
    
    /**
     * Converte de entidade de banco de dados para entidade de domínio.
     */
    fun toDomainModel(entity: AssessmentEntity): Assessment {
        val listType = object : TypeToken<List<AssessmentQuestion>>() {}.type
        val questions: List<AssessmentQuestion> = gson.fromJson(entity.responsesJson, listType) ?: emptyList()
        
        return Assessment(
            id = entity.id,
            date = entity.timestamp.toLocalDate(),
            questions = questions,
            completed = true, // Assumimos que já está completo se estiver no banco
            result = AssessmentResult(
                score = entity.score,
                riskLevel = getRiskLevel(entity.score),
                recommendations = emptyList() // Podemos implementar recomendações mais tarde
            )
        )
    }
    
    /**
     * Converte uma lista de entidades de banco de dados para uma lista de entidades de domínio.
     */
    fun toDomainModelList(entities: List<AssessmentEntity>): List<Assessment> {
        return entities.map { toDomainModel(it) }
    }
    
    /**
     * Determina o nível de risco com base na pontuação.
     */
    private fun getRiskLevel(score: Int): com.example.mindwell.app.domain.entities.RiskLevel {
        return when {
            score < 30 -> com.example.mindwell.app.domain.entities.RiskLevel.LOW
            score < 70 -> com.example.mindwell.app.domain.entities.RiskLevel.MEDIUM
            else -> com.example.mindwell.app.domain.entities.RiskLevel.HIGH
        }
    }
} 