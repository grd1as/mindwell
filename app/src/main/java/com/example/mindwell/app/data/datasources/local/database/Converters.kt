package com.example.mindwell.app.data.datasources.local.database

import androidx.room.TypeConverter
import com.example.mindwell.app.domain.entities.AssessmentType
import com.example.mindwell.app.domain.entities.ResourceType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Classe de conversores para tipos complexos no Room.
 */
class Converters {
    private val gson = Gson()
    
    // LocalDateTime conversores
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
    
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
    }
    
    // LocalDate conversores
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }
    
    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
    }
    
    // AssessmentType conversores
    @TypeConverter
    fun fromAssessmentType(value: AssessmentType): String {
        return value.name
    }
    
    @TypeConverter
    fun toAssessmentType(value: String): AssessmentType {
        return AssessmentType.valueOf(value)
    }
    
    // ResourceType conversores
    @TypeConverter
    fun fromResourceType(value: ResourceType): String {
        return value.name
    }
    
    @TypeConverter
    fun toResourceType(value: String): ResourceType {
        return ResourceType.valueOf(value)
    }
    
    // Map<String, Int> conversores (para respostas de avaliação)
    @TypeConverter
    fun fromStringIntMap(value: Map<String, Int>?): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toStringIntMap(value: String): Map<String, Int> {
        val mapType = object : TypeToken<Map<String, Int>>() {}.type
        return gson.fromJson(value, mapType) ?: emptyMap()
    }
    
    // List<String> conversores (para tags)
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { 
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()) 
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }
} 