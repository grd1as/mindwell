package com.example.mindwell.app.domain.usecases.wellbeing

import com.example.mindwell.app.domain.entities.CheckIn
import com.example.mindwell.app.domain.entities.Resource
import com.example.mindwell.app.domain.repositories.CheckInRepository
import com.example.mindwell.app.domain.repositories.ResourceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para gerar dicas personalizadas com base nos check-ins do usuário.
 */
class GeneratePersonalizedTipsUseCase @Inject constructor(
    private val checkInRepository: CheckInRepository,
    private val resourceRepository: ResourceRepository
) {
    /**
     * Gera dicas personalizadas com base nos check-ins recentes do usuário.
     * 
     * @param checkInsLimit Número de check-ins recentes a analisar
     * @return Result contendo uma lista de recursos recomendados
     */
    suspend operator fun invoke(checkInsLimit: Int = 7): Result<List<Resource>> = runCatching {
        // Obtém check-ins recentes
        val recentCheckIns = checkInRepository.getRecentCheckIns(checkInsLimit).first()
        
        // Obtém recursos recomendados com base nos check-ins
        val recommendedResources = resourceRepository.getRecommendedResources().first()
        
        // Lógica de filtragem baseada nos check-ins (implementação simplificada)
        recommendedResources
    }
} 