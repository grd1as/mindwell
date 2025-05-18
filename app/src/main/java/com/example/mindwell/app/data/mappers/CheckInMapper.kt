package com.example.mindwell.app.data.mappers

import com.example.mindwell.app.data.datasources.local.entities.CheckInEntity
import com.example.mindwell.app.domain.entities.CheckIn

/**
 * Mapper para converter entre entidades de domínio e entidades de banco de dados para CheckIn.
 */
object CheckInMapper {
    
    /**
     * Converte de entidade de domínio para entidade de banco de dados.
     */
    fun toEntity(domainModel: CheckIn): CheckInEntity {
        return CheckInEntity(
            id = domainModel.id,
            timestamp = domainModel.timestamp,
            moodLevel = domainModel.moodLevel,
            stressLevel = domainModel.stressLevel,
            notes = domainModel.notes
        )
    }
    
    /**
     * Converte de entidade de banco de dados para entidade de domínio.
     */
    fun toDomainModel(entity: CheckInEntity): CheckIn {
        return CheckIn(
            id = entity.id,
            timestamp = entity.timestamp,
            moodLevel = entity.moodLevel,
            stressLevel = entity.stressLevel,
            notes = entity.notes
        )
    }
    
    /**
     * Converte uma lista de entidades de banco de dados para uma lista de entidades de domínio.
     */
    fun toDomainModelList(entities: List<CheckInEntity>): List<CheckIn> {
        return entities.map { toDomainModel(it) }
    }
} 