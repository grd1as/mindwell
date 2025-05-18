package com.example.mindwell.app.data.mappers

import com.example.mindwell.app.data.datasources.local.entities.WellbeingMetricsEntity
import com.example.mindwell.app.domain.entities.WellbeingMetrics

/**
 * Mapper para converter entre entidades de domínio e entidades de banco de dados para WellbeingMetrics.
 */
object WellbeingMetricsMapper {
    
    /**
     * Converte de entidade de domínio para entidade de banco de dados.
     */
    fun toEntity(domainModel: WellbeingMetrics): WellbeingMetricsEntity {
        return WellbeingMetricsEntity(
            date = domainModel.date,
            averageMood = domainModel.averageMood,
            averageStress = domainModel.averageStress,
            workloadScore = domainModel.workloadScore,
            environmentScore = domainModel.environmentScore,
            wellbeingScore = domainModel.wellbeingScore
        )
    }
    
    /**
     * Converte de entidade de banco de dados para entidade de domínio.
     */
    fun toDomainModel(entity: WellbeingMetricsEntity): WellbeingMetrics {
        return WellbeingMetrics(
            date = entity.date,
            averageMood = entity.averageMood,
            averageStress = entity.averageStress,
            workloadScore = entity.workloadScore,
            environmentScore = entity.environmentScore,
            wellbeingScore = entity.wellbeingScore
        )
    }
    
    /**
     * Converte uma lista de entidades de banco de dados para uma lista de entidades de domínio.
     */
    fun toDomainModelList(entities: List<WellbeingMetricsEntity>): List<WellbeingMetrics> {
        return entities.map { toDomainModel(it) }
    }
} 