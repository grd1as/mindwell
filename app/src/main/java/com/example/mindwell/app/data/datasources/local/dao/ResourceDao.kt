package com.example.mindwell.app.data.datasources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mindwell.app.data.datasources.local.entities.ResourceEntity
import com.example.mindwell.app.domain.entities.ResourceType
import kotlinx.coroutines.flow.Flow

/**
 * DAO para acesso a dados de recursos no banco de dados.
 */
@Dao
interface ResourceDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(resource: ResourceEntity): Long
    
    @Query("SELECT * FROM resources WHERE id = :id")
    suspend fun getById(id: Long): ResourceEntity?
    
    @Query("SELECT * FROM resources ORDER BY title")
    fun getAll(): Flow<List<ResourceEntity>>
    
    @Query("SELECT * FROM resources WHERE type = :type ORDER BY title")
    fun getByType(type: ResourceType): Flow<List<ResourceEntity>>
    
    @Query("SELECT * FROM resources WHERE isRecommended = 1 ORDER BY title")
    fun getRecommended(): Flow<List<ResourceEntity>>
    
    @Query("SELECT * FROM resources WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<ResourceEntity>>
    
    @Query("DELETE FROM resources WHERE id = :id")
    suspend fun delete(id: Long): Int
} 