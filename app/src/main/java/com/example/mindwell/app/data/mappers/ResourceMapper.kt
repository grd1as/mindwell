package com.example.mindwell.app.data.mappers

import com.example.mindwell.app.data.datasources.local.entities.ResourceEntity
import com.example.mindwell.app.domain.entities.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Mapper para converter entre entidades de domínio e entidades de banco de dados para Resource.
 */
object ResourceMapper {
    private val gson = Gson()
    
    /**
     * Converte de entidade de domínio para entidade de banco de dados.
     */
    fun toEntity(domainModel: Resource): ResourceEntity {
        return ResourceEntity(
            id = domainModel.id,
            title = domainModel.title,
            description = domainModel.description,
            type = domainModel.type,
            tagsJson = gson.toJson(domainModel.tags),
            content = domainModel.content,
            isRecommended = domainModel.isRecommended
        )
    }
    
    /**
     * Converte de entidade de banco de dados para entidade de domínio.
     */
    fun toDomainModel(entity: ResourceEntity): Resource {
        val listType = object : TypeToken<List<String>>() {}.type
        val tags: List<String> = gson.fromJson(entity.tagsJson, listType) ?: emptyList()
        
        return Resource(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            type = entity.type,
            tags = tags,
            content = entity.content,
            isRecommended = entity.isRecommended
        )
    }
    
    /**
     * Converte uma lista de entidades de banco de dados para uma lista de entidades de domínio.
     */
    fun toDomainModelList(entities: List<ResourceEntity>): List<Resource> {
        return entities.map { toDomainModel(it) }
    }
} 