package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.data.services.PersonalizedContentResponse
import com.example.mindwell.app.data.services.PersonalizedTip
import com.example.mindwell.app.data.services.UserProfileData

/**
 * Interface para repositório de AI (Gemini)
 */
interface AiRepository {
    
    /**
     * Gera conteúdo personalizado de recursos
     */
    suspend fun generate_personalized_resources(user_data: UserProfileData): Result<PersonalizedContentResponse>
    
    /**
     * Gera dicas personalizadas
     */
    suspend fun generate_personalized_tips(user_data: UserProfileData): Result<List<PersonalizedTip>>
    
    /**
     * Obtém dados do perfil do usuário baseado no histórico
     */
    suspend fun get_user_profile_data(): UserProfileData
} 