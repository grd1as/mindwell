package com.example.mindwell.app.domain.usecases.form

import com.example.mindwell.app.domain.entities.Form
import com.example.mindwell.app.domain.repositories.FormRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para obter formulários pendentes do usuário.
 */
interface GetPendingFormsUseCase {
    /**
     * Obtém a lista de formulários pendentes para o usuário.
     * @return Flow com o resultado contendo a lista de formulários pendentes
     */
    operator fun invoke(): Flow<Result<List<Form>>>
}

/**
 * Implementação do caso de uso para obter formulários pendentes.
 */
class GetPendingFormsUseCaseImpl @Inject constructor(
    private val form_repository: FormRepository
) : GetPendingFormsUseCase {
    override operator fun invoke(): Flow<Result<List<Form>>> = flow {
        try {
            // Buscar todos os formulários disponíveis e filtrar apenas SELF_ASSESS e CLIMATE
            val all_forms = form_repository.get_forms(type = null)
            val filtered_forms = all_forms.filter { form ->
                form.type == "SELF_ASSESSMENT" || form.type == "CLIMATE"
            }
            emit(Result.success(filtered_forms))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 