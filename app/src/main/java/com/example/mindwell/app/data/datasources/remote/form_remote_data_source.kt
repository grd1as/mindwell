package com.example.mindwell.app.data.datasources.remote

import com.example.mindwell.app.data.model.AnswerDTO
import com.example.mindwell.app.data.model.FormDTO
import com.example.mindwell.app.data.model.FormDetailDTO
import com.example.mindwell.app.data.model.FormResponseRequest
import com.example.mindwell.app.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fonte de dados remota para formulários.
 */
@Singleton
class FormRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * Obtém a lista de formulários disponíveis.
     * @param type Tipo opcional de formulário para filtrar
     * @return Lista de formulários
     */
    suspend fun getForms(type: String? = null): List<FormDTO> {
        return apiService.getForms(type)
    }
    
    /**
     * Obtém os detalhes de um formulário específico.
     * @param formId ID do formulário
     * @return Detalhes do formulário com perguntas
     */
    suspend fun getFormDetail(formId: Int): FormDetailDTO {
        return apiService.getFormDetail(formId)
    }
    
    /**
     * Envia respostas para um formulário.
     * @param formId ID do formulário
     * @param answers Lista de respostas
     * @return ID da resposta (obtido do header Location)
     */
    suspend fun submitFormResponses(formId: Int, answers: List<AnswerDTO>): Int {
        val request = FormResponseRequest(answers)
        apiService.submitFormResponses(formId, request)
        // Em um cenário real, parsearíamos o header Location para obter o ID da resposta
        // Por enquanto, retornamos um valor fictício
        return 0
    }
} 