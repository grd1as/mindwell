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
     * @param form_id ID do formulário
     * @return Flow com o resultado contendo os detalhes do formulário
     */
    operator fun invoke(form_id: Int): Flow<Result<FormDetail>>
}

/**
 * Implementação do caso de uso para obter detalhes de um formulário.
 */
class GetFormDetailUseCaseImpl @Inject constructor(
    private val form_repository: FormRepository
) : GetFormDetailUseCase {
    override operator fun invoke(form_id: Int): Flow<Result<FormDetail>> = flow {
        try {
            val form_detail = form_repository.get_form_detail(form_id)
            emit(Result.success(form_detail))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 