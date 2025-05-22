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
    private val remoteDataSource: FormRemoteDataSource
) : FormRepository {
    /**
     * Obtém a lista de formulários disponíveis.
     * @param type Tipo opcional de formulário para filtrar
     * @return Lista de formulários
     */
    override suspend fun getForms(type: String?): List<Form> {
        val formDtos = remoteDataSource.getForms(type)
        return FormMapper.mapToDomain(formDtos)
    }
    
    /**
     * Obtém os detalhes de um formulário específico.
     * @param formId ID do formulário
     * @return Detalhes do formulário com perguntas
     */
    override suspend fun getFormDetail(formId: Int): FormDetail {
        val formDetailDto = remoteDataSource.getFormDetail(formId)
        return FormMapper.mapToDomain(formDetailDto)
    }
    
    /**
     * Envia respostas para um formulário.
     * @param formId ID do formulário
     * @param answers Lista de respostas
     * @return ID da resposta enviada
     */
    override suspend fun submitFormResponses(formId: Int, answers: List<Answer>): Int {
        val answerDtos = FormMapper.mapToDto(answers)
        val request = FormResponseRequest(answerDtos)
        return remoteDataSource.submitFormResponses(formId, request)
    }
} 