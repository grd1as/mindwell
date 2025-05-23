package com.example.mindwell.app.domain.usecases.resource

import com.example.mindwell.app.data.services.PersonalizedTip
import com.example.mindwell.app.domain.repositories.AiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para obter dicas personalizadas via Gemini AI
 */
interface GetPersonalizedTipsUseCase {
    /**
     * Obtém dicas personalizadas baseadas no perfil do usuário
     * @return Flow com o resultado contendo dicas personalizadas
     */
    operator fun invoke(): Flow<Result<List<PersonalizedTip>>>
}

/**
 * Implementação do caso de uso para obter dicas personalizadas
 */
class GetPersonalizedTipsUseCaseImpl @Inject constructor(
    private val ai_repository: AiRepository
) : GetPersonalizedTipsUseCase {
    override operator fun invoke(): Flow<Result<List<PersonalizedTip>>> = flow {
        try {
            // Obter dados do perfil do usuário
            val user_data = ai_repository.get_user_profile_data()
            
            // Gerar dicas personalizadas
            val result = ai_repository.generate_personalized_tips(user_data)
            emit(result)
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 