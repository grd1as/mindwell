package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.Form
import com.example.mindwell.app.domain.entities.FormDetail
import com.example.mindwell.app.domain.entities.Answer

/**
 * Interface para o repositório de formulários.
 */
interface FormRepository {
    /**
     * Obtém a lista de formulários disponíveis.
     * @param type Tipo opcional de formulário para filtrar
     * @return Lista de formulários
     */
    suspend fun get_forms(type: String? = null): List<Form>
    
    /**
     * Obtém os detalhes de um formulário específico.
     * @param form_id ID do formulário
     * @return Detalhes do formulário com perguntas
     */
    suspend fun get_form_detail(form_id: Int): FormDetail
    
    /**
     * Envia respostas para um formulário.
     * @param form_id ID do formulário
     * @param answers Lista de respostas
     * @return ID da resposta enviada
     */
    suspend fun submit_form_responses(form_id: Int, answers: List<Answer>): Int
} 