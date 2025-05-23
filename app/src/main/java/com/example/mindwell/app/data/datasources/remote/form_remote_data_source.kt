package com.example.mindwell.app.data.datasources.remote

import com.example.mindwell.app.data.model.*
import com.example.mindwell.app.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fonte de dados remota para formulários.
 */
@Singleton
class FormRemoteDataSource @Inject constructor(
    private val api_service: ApiService
) {
    /**
     * Obtém a lista de formulários disponíveis.
     * @param type Tipo opcional de formulário para filtrar
     * @return Lista de formulários
     */
    suspend fun get_forms(type: String? = null): List<FormDTO> {
        return api_service.get_forms(type = type)
    }
    
    /**
     * Obtém os detalhes de um formulário específico.
     * @param form_id ID do formulário
     * @return Detalhes do formulário com perguntas
     */
    suspend fun get_form_detail(form_id: Int): FormDetailDTO {
        return api_service.get_form_detail(form_id)
    }
    
    /**
     * Envia respostas para um formulário.
     * @param form_id ID do formulário
     * @param request Dados das respostas
     * @return ID da resposta extraído da URL retornada
     */
    suspend fun submit_form_responses(form_id: Int, request: FormResponseRequest): Int {
        val response = api_service.submit_form_responses(form_id, request)
        // Extrai o ID da resposta a partir do header Location (exemplo: /forms/1/responses/42)
        val locationHeader = response.headers()["Location"] ?: ""
        return locationHeader.substringAfterLast("/").toIntOrNull() ?: -1
    }
} 