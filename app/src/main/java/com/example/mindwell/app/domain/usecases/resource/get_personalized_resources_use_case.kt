package com.example.mindwell.app.domain.usecases.resource

import com.example.mindwell.app.data.services.PersonalizedContentResponse
import com.example.mindwell.app.domain.repositories.AiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para obter recursos personalizados via Gemini AI
 */
interface GetPersonalizedResourcesUseCase {
    /**
     * Obtém recursos personalizados baseados no perfil do usuário
     * @return Flow com o resultado contendo recursos personalizados
     */
    operator fun invoke(): Flow<Result<PersonalizedContentResponse>>
}

/**
 * Implementação do caso de uso para obter recursos personalizados
 */
class GetPersonalizedResourcesUseCaseImpl @Inject constructor(
    private val ai_repository: AiRepository
) : GetPersonalizedResourcesUseCase {
    override operator fun invoke(): Flow<Result<PersonalizedContentResponse>> = flow {
        try {
            // Obter dados do perfil do usuário
            val user_data = ai_repository.get_user_profile_data()
            
            // Gerar recursos personalizados
            val result = ai_repository.generate_personalized_resources(user_data)
            emit(result)
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 