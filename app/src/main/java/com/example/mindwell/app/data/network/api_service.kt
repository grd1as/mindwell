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
     * Realiza logout.
     */
    @POST("auth/mobile/logout")
    suspend fun logout(): Map<String, String>
    
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
     * @return Resposta da API (Created 201)
     */
    @POST("forms/{formId}/responses")
    suspend fun submitFormResponses(
        @Path("formId") formId: Int,
        @Body request: FormResponseRequest
    ): ResponseWithLocation
    
    /**
     * Obtém o resumo dos check-ins por período.
     * @param month Mês no formato YYYY-MM (opcional)
     * @return Resumo consolidado
     */
    @GET("summary/checkin")
    suspend fun getSummary(
        @Query("month") month: String
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
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
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
     * @return Resposta da API (Created 201)
     */
    @POST("reports")
    suspend fun submitReport(@Body report: ReportDTO): ResponseWithLocation
} 