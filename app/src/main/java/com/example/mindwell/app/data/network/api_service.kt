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
    suspend fun get_forms(@Query("type") type: String? = null): List<FormDTO>
    
    /**
     * Obtém os detalhes de um formulário específico.
     * @param formId ID do formulário
     * @return Detalhes do formulário com perguntas
     */
    @GET("forms/{formId}")
    suspend fun get_form_detail(@Path("formId") form_id: Int): FormDetailDTO
    
    /**
     * Envia respostas para um formulário.
     * @param formId ID do formulário
     * @param request Dados das respostas
     * @return Resposta da API (Created 201)
     */
    @POST("forms/{formId}/responses")
    suspend fun submit_form_responses(
        @Path("formId") form_id: Int,
        @Body request: FormResponseRequest
    ): ResponseWithLocation
    
    /**
     * Obtém o resumo dos check-ins por período.
     * @param month Mês no formato YYYY-MM (opcional)
     * @return Resumo consolidado
     */
    @GET("summary/checkin")
    suspend fun get_summary(
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
    suspend fun get_checkins(
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
    suspend fun get_preferences(): PreferenceDTO
    
    /**
     * Atualiza as preferências do usuário.
     * @param preference Novas preferências
     */
    @PUT("preferences")
    suspend fun update_preferences(@Body preference: PreferenceDTO)
    
    /**
     * Obtém os lembretes ativos.
     * @param due Se true, retorna apenas lembretes pendentes
     * @return Lista de lembretes
     */
    @GET("reminders")
    suspend fun get_reminders(@Query("due") due: Boolean? = null): List<ReminderDTO>
    
    /**
     * Envia uma nova denúncia/report.
     * @param report Dados da denúncia
     * @return Resposta da API (Created 201)
     */
    @POST("reports")
    suspend fun submit_report(@Body report: ReportDTO): ResponseWithLocation
    
    /**
     * Obtém a lista de recursos disponíveis.
     * @param category Categoria opcional para filtrar recursos
     * @return Lista de recursos
     */
    @GET("resources")
    suspend fun get_resources(@Query("category") category: String? = null): List<ResourceDTO>
    
    /**
     * Obtém os detalhes de um recurso específico.
     * @param resourceId ID do recurso
     * @return Detalhes do recurso
     */
    @GET("resources/{resourceId}")
    suspend fun get_resource_detail(@Path("resourceId") resource_id: String): ResourceDetailDTO
    
    /**
     * Obtém as categorias de recursos disponíveis.
     * @return Lista de categorias de recursos
     */
    @GET("resources/categories")
    suspend fun get_resource_categories(): List<ResourceCategoryDTO>
} 