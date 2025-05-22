package com.example.mindwell.app.data.repositories

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
    /**
     * Obtém a lista de formulários disponíveis.
     * @param type Tipo opcional de formulário para filtrar
     * @return Lista de formulários
     */
    override suspend fun get_forms(type: String?): List<Form> {
        val form_dtos = remote_data_source.get_forms(type)
        return FormMapper.mapToDomain(form_dtos)
    }
    
    /**
     * Obtém os detalhes de um formulário específico.
     * @param form_id ID do formulário
     * @return Detalhes do formulário com perguntas
     */
    override suspend fun get_form_detail(form_id: Int): FormDetail {
        val form_detail_dto = remote_data_source.get_form_detail(form_id)
        return FormMapper.mapToDomain(form_detail_dto)
    }
    
    /**
     * Envia respostas para um formulário.
     * @param form_id ID do formulário
     * @param answers Lista de respostas
     * @return ID da resposta enviada
     */
    override suspend fun submit_form_responses(form_id: Int, answers: List<Answer>): Int {
        val answer_dtos = FormMapper.mapToDto(answers)
        val request = FormResponseRequest(answer_dtos)
        return remote_data_source.submit_form_responses(form_id, request)
    }
} 