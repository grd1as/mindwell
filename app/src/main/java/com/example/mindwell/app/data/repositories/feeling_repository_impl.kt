package com.example.mindwell.app.data.repositories

import android.util.Log
import com.example.mindwell.app.data.datasources.remote.FeelingRemoteDataSource
import com.example.mindwell.app.data.mappers.FeelingMapper
import com.example.mindwell.app.domain.entities.Feeling
import com.example.mindwell.app.domain.repositories.FeelingRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de sentimentos que usa a API.
 */
@Singleton
class FeelingRepositoryImpl @Inject constructor(
    private val remoteDataSource: FeelingRemoteDataSource
) : FeelingRepository {
    private val TAG = "FeelingRepository"
    
    override suspend fun get_feelings(): List<Feeling> {
        Log.d(TAG, "🌐 Tentando buscar sentimentos da API")
        try {
            val feelingDTOs = remoteDataSource.get_feelings()
            Log.d(TAG, "✅ Sucesso na busca de ${feelingDTOs.size} sentimentos da API")
            return FeelingMapper.mapToDomain(feelingDTOs)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro na busca de sentimentos da API: ${e.message}")
            throw e
        }
    }
} 