package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.AnonymousUser
import com.example.mindwell.app.domain.entities.AppTheme
import kotlinx.coroutines.flow.Flow

/**
 * Interface para gerenciamento do usuário anônimo.
 * Responsável por manter o ID de dispositivo anônimo e configurações do usuário
 * sem coletar dados pessoais identificáveis.
 */
interface AnonymousUserRepository {
    /**
     * Obtém o usuário anônimo atual ou cria um novo se não existir.
     * @return Flow contendo o usuário anônimo
     */
    fun getAnonymousUser(): Flow<AnonymousUser>
    
    /**
     * Atualiza as configurações do usuário anônimo.
     * @param user Dados atualizados do usuário anônimo
     */
    suspend fun updateAnonymousUser(user: AnonymousUser)
    
    /**
     * Registra o consentimento do usuário para coleta de dados anônimos.
     * @param hasConsent Indica se o usuário consentiu com a coleta de dados
     */
    suspend fun updateDataCollectionConsent(hasConsent: Boolean)
    
    /**
     * Atualiza a preferência de notificações do usuário.
     * @param enabled Indica se as notificações estão habilitadas
     */
    suspend fun updateNotificationsEnabled(enabled: Boolean)
    
    /**
     * Atualiza o tema preferido do aplicativo.
     * @param theme Tema selecionado pelo usuário
     */
    suspend fun updateAppTheme(theme: com.example.mindwell.app.domain.entities.AppTheme)
    
    /**
     * Atualiza o horário preferido para receber lembretes.
     * @param time Horário no formato "HH:mm"
     */
    suspend fun updatePreferredReminderTime(time: String?)
    
    /**
     * Gera um novo ID anônimo para o dispositivo.
     * Usado apenas em casos específicos, como a limpeza completa de dados.
     * @return Novo usuário anônimo gerado
     */
    suspend fun generateNewAnonymousId(): AnonymousUser

    /**
     * Verifica se o usuário já completou o processo de onboarding.
     * @return Flow com valor booleano indicando se o onboarding foi concluído
     */
    fun hasCompletedOnboarding(): Flow<Boolean>
    
    /**
     * Marca o processo de onboarding como concluído.
     */
    suspend fun markOnboardingCompleted()
} 