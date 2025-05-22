package com.example.mindwell.app.domain.usecases.form

import com.example.mindwell.app.domain.entities.Form
import com.example.mindwell.app.domain.repositories.FormRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para obter lista de formulários disponíveis.
 */
interface GetFormsUseCase {
    /**
     * Obtém lista de formulários disponíveis.
     * @param type Tipo opcional de formulário para filtrar
     * @return Flow com o resultado contendo a lista de formulários
     */
    operator fun invoke(type: String? = null): Flow<Result<List<Form>>>
}

/**
 * Implementação do caso de uso para obter lista de formulários.
 */
class GetFormsUseCaseImpl @Inject constructor(
    private val formRepository: FormRepository
) : GetFormsUseCase {
    override operator fun invoke(type: String?): Flow<Result<List<Form>>> = flow {
        try {
            val forms = formRepository.getForms(type)
            emit(Result.success(forms))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 