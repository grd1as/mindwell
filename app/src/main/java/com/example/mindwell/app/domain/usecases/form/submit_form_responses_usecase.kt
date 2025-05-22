package com.example.mindwell.app.domain.usecases.form

import com.example.mindwell.app.domain.entities.Answer
import com.example.mindwell.app.domain.repositories.FormRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para enviar respostas de um formulário.
 */
interface SubmitFormResponsesUseCase {
    /**
     * Envia as respostas de um formulário.
     * @param form_id ID do formulário
     * @param answers Lista de respostas
     * @return Flow com o resultado contendo o ID da resposta enviada
     */
    operator fun invoke(form_id: Int, answers: List<Answer>): Flow<Result<Int>>
}

/**
 * Implementação do caso de uso para enviar respostas de um formulário.
 */
class SubmitFormResponsesUseCaseImpl @Inject constructor(
    private val form_repository: FormRepository
) : SubmitFormResponsesUseCase {
    override operator fun invoke(form_id: Int, answers: List<Answer>): Flow<Result<Int>> = flow {
        try {
            val response_id = form_repository.submit_form_responses(form_id, answers)
            emit(Result.success(response_id))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 