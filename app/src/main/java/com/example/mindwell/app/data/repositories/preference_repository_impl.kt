package com.example.mindwell.app.data.repositories

import android.util.Log
import com.example.mindwell.app.data.datasources.remote.PreferenceRemoteDataSource
import com.example.mindwell.app.data.mappers.PreferenceMapper
import com.example.mindwell.app.domain.entities.Preference
import com.example.mindwell.app.domain.repositories.PreferenceRepository
import com.example.mindwell.app.domain.repositories.UserPreferenceRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de preferências que usa a API.
 */
@Singleton
class UserPreferenceRepositoryImpl @Inject constructor(
    private val remoteDataSource: PreferenceRemoteDataSource
) : UserPreferenceRepository {
    private val TAG = "UserPreferenceRepository"
    
    override suspend fun getPreferences(): Preference {
        Log.d(TAG, "🌐 Tentando buscar preferências da API")
        try {
            val preferenceDTO = remoteDataSource.get_preferences()
            Log.d(TAG, "✅ Sucesso na busca de preferências da API")
            return PreferenceMapper.mapToDomain(preferenceDTO)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro na busca de preferências da API: ${e.message}")
            throw e
        }
    }
    
    override suspend fun updatePreferences(preference: Preference) {
        Log.d(TAG, "🌐 Tentando atualizar preferências na API")
        try {
            val preferenceDTO = PreferenceMapper.mapToDto(preference)
            remoteDataSource.update_preferences(preferenceDTO)
            Log.d(TAG, "✅ Sucesso na atualização de preferências na API")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro na atualização de preferências na API: ${e.message}")
            throw e
        }
    }
}

/**
 * Implementação do repositório de preferências alternativo.
 * Esta classe implementa a interface PreferenceRepository.
 */
@Singleton
class PreferenceRepositoryImpl @Inject constructor(
    private val remoteDataSource: PreferenceRemoteDataSource
) : PreferenceRepository {
    private val TAG = "PreferenceRepository"
    
    override suspend fun getPreferences(): Preference {
        Log.d(TAG, "🌐 Tentando buscar preferências da API")
        try {
            val preferenceDTO = remoteDataSource.get_preferences()
            Log.d(TAG, "✅ Sucesso na busca de preferências da API")
            return PreferenceMapper.mapToDomain(preferenceDTO)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro na busca de preferências da API: ${e.message}")
            throw e
        }
    }
    
    override suspend fun updatePreferences(preferences: Preference) {
        Log.d(TAG, "🌐 Tentando atualizar preferências na API")
        try {
            val preferenceDTO = PreferenceMapper.mapToDto(preferences)
            remoteDataSource.update_preferences(preferenceDTO)
            Log.d(TAG, "✅ Sucesso na atualização de preferências na API")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro na atualização de preferências na API: ${e.message}")
            throw e
        }
    }
} 