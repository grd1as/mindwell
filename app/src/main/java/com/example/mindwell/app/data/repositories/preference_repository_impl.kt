package com.example.mindwell.app.data.repositories

import com.example.mindwell.app.data.datasources.remote.PreferenceRemoteDataSource
import com.example.mindwell.app.data.mappers.PreferenceMapper
import com.example.mindwell.app.domain.entities.Preference
import com.example.mindwell.app.domain.repositories.PreferenceRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de preferências.
 */
@Singleton
class PreferenceRepositoryImpl @Inject constructor(
    private val remoteDataSource: PreferenceRemoteDataSource
) : PreferenceRepository {
    /**
     * Obtém as preferências do usuário.
     * @return Preferências atuais
     */
    override suspend fun getPreferences(): Preference {
        val preferencesDto = remoteDataSource.getPreferences()
        return PreferenceMapper.mapToDomain(preferencesDto)
    }
    
    /**
     * Atualiza as preferências do usuário.
     * @param preferences Novas preferências
     */
    override suspend fun updatePreferences(preferences: Preference) {
        val preferencesDto = PreferenceMapper.mapToDto(preferences)
        remoteDataSource.updatePreferences(preferencesDto)
    }
} 