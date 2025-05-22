package com.example.mindwell.app.data.network

import com.example.mindwell.app.data.model.*
import retrofit2.http.*

/**
 * Interface que define os endpoints da API do MindWell.
 */
interface ApiService {
    /**
     * Realiza login com token do Google.
     * @param request Dados de login
     * @return Resposta com token JWT
     */
    @POST("auth/mobile")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    
    /**
     * Obtém a lista de formulários disponíveis.
     * @param type Tipo opcional de formulário para filtrar
     * @return Lista de formulários
     */
    @GET("forms")
    suspend fun getForms(@Query("type") type: String? = null): List<FormDTO>
    
    /**
     * Obtém os detalhes de um formulário específico.
     * @param formId ID do formulário
     * @return Detalhes do formulário com perguntas
     */
    @GET("forms/{formId}")
    suspend fun getFormDetail(@Path("formId") formId: Int): FormDetailDTO
    
    /**
     * Envia respostas para um formulário.
     * @param formId ID do formulário
     * @param request Dados das respostas
     */
    @POST("forms/{formId}/responses")
    suspend fun submitFormResponses(
        @Path("formId") formId: Int,
        @Body request: FormResponseRequest
    )
    
    /**
     * Obtém o resumo dos check-ins por período.
     * @param month Mês no formato YYYY-MM (opcional)
     * @param week Semana no formato YYYY-Www (opcional)
     * @return Resumo consolidado
     */
    @GET("summary/checkin")
    suspend fun getSummary(
        @Query("month") month: String? = null,
        @Query("week") week: String? = null
    ): SummaryDTO
    
    /**
     * Obtém os check-ins do usuário com paginação e filtro.
     * @param page Número da página
     * @param size Tamanho da página
     * @param from Data inicial para filtro
     * @param to Data final para filtro
     * @return Página de check-ins
     */
    @GET("checkins")
    suspend fun getCheckins(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): CheckinPageDTO
    
    /**
     * Obtém as preferências do usuário.
     * @return Preferências atuais
     */
    @GET("preferences")
    suspend fun getPreferences(): PreferenceDTO
    
    /**
     * Atualiza as preferências do usuário.
     * @param preference Novas preferências
     */
    @PUT("preferences")
    suspend fun updatePreferences(@Body preference: PreferenceDTO)
    
    /**
     * Obtém os lembretes ativos.
     * @param due Se true, retorna apenas lembretes pendentes
     * @return Lista de lembretes
     */
    @GET("reminders")
    suspend fun getReminders(@Query("due") due: Boolean? = null): List<ReminderDTO>
    
    /**
     * Envia uma nova denúncia/report.
     * @param report Dados da denúncia
     */
    @POST("reports")
    suspend fun submitReport(@Body report: ReportDTO)
} 