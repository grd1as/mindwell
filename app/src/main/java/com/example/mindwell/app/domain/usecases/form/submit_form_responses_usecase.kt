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
     * @param formId ID do formulário
     * @param answers Lista de respostas
     * @return Flow com o resultado contendo o ID da resposta enviada
     */
    operator fun invoke(formId: Int, answers: List<Answer>): Flow<Result<Int>>
}

/**
 * Implementação do caso de uso para enviar respostas de um formulário.
 */
class SubmitFormResponsesUseCaseImpl @Inject constructor(
    private val formRepository: FormRepository
) : SubmitFormResponsesUseCase {
    override operator fun invoke(formId: Int, answers: List<Answer>): Flow<Result<Int>> = flow {
        try {
            val responseId = formRepository.submitFormResponses(formId, answers)
            emit(Result.success(responseId))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 