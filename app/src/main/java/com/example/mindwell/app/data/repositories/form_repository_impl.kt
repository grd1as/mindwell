package com.example.mindwell.app.data.repositories

import android.util.Log
import com.example.mindwell.app.data.datasources.remote.FormRemoteDataSource
import com.example.mindwell.app.data.mappers.FormMapper
import com.example.mindwell.app.data.model.FormResponseRequest
import com.example.mindwell.app.domain.entities.Answer
import com.example.mindwell.app.domain.entities.Form
import com.example.mindwell.app.domain.entities.FormDetail
import com.example.mindwell.app.domain.repositories.FormRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de formulários.
 */
@Singleton
class FormRepositoryImpl @Inject constructor(
    private val remote_data_source: FormRemoteDataSource
) : FormRepository {
    private val TAG = "FormRepository"

    /**
     * Obtém a lista de formulários disponíveis.
     * @param type Tipo opcional de formulário para filtrar
     * @return Lista de formulários
     */
    override suspend fun get_forms(type: String?): List<Form> {
        Log.d(TAG, "🌐 Tentando buscar formulários da API... Tipo: $type")
        try {
            val form_dtos = remote_data_source.get_forms(type)
            Log.d(TAG, "✅ Sucesso na busca de formulários da API. Quantidade: ${form_dtos.size}")
            return FormMapper.mapToDomain(form_dtos)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro na busca de formulários da API: ${e.message}")
            throw e
        }
    }
    
    /**
     * Obtém os detalhes de um formulário específico.
     * @param form_id ID do formulário
     * @return Detalhes do formulário com perguntas
     */
    override suspend fun get_form_detail(form_id: Int): FormDetail {
        Log.d(TAG, "🌐 Tentando buscar detalhes do formulário da API... ID: $form_id")
        try {
            val form_detail_dto = remote_data_source.get_form_detail(form_id)
            Log.d(TAG, "✅ Sucesso na busca de detalhes do formulário da API. Nome: ${form_detail_dto.name}")
            return FormMapper.mapToDomain(form_detail_dto)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro na busca de detalhes do formulário da API: ${e.message}")
            throw e
        }
    }
    
    /**
     * Envia respostas para um formulário.
     * @param form_id ID do formulário
     * @param answers Lista de respostas
     * @return ID da resposta enviada
     */
    override suspend fun submit_form_responses(form_id: Int, answers: List<Answer>): Int {
        Log.d(TAG, "🌐 Tentando enviar respostas do formulário para API... ID: $form_id, Respostas: ${answers.size}")
        try {
            val answer_dtos = FormMapper.mapToDto(answers)
            val request = FormResponseRequest(answer_dtos)
            val response_id = remote_data_source.submit_form_responses(form_id, request)
            Log.d(TAG, "✅ Sucesso no envio de respostas do formulário para API. Response ID: $response_id")
            return response_id
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro no envio de respostas do formulário para API: ${e.message}")
            throw e
        }
    }
} 