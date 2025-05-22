package com.example.mindwell.app.domain.usecases.form

import com.example.mindwell.app.domain.entities.FormDetail
import com.example.mindwell.app.domain.repositories.FormRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para obter detalhes de um formulário.
 */
interface GetFormDetailUseCase {
    /**
     * Obtém detalhes de um formulário específico.
     * @param formId ID do formulário
     * @return Flow com o resultado contendo os detalhes do formulário
     */
    operator fun invoke(formId: Int): Flow<Result<FormDetail>>
}

/**
 * Implementação do caso de uso para obter detalhes de um formulário.
 */
class GetFormDetailUseCaseImpl @Inject constructor(
    private val formRepository: FormRepository
) : GetFormDetailUseCase {
    override operator fun invoke(formId: Int): Flow<Result<FormDetail>> = flow {
        try {
            val formDetail = formRepository.getFormDetail(formId)
            emit(Result.success(formDetail))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 