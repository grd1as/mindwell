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
    private val formRepository: FormRepository
) : GetPendingFormsUseCase {
    override operator fun invoke(): Flow<Result<List<Form>>> = flow {
        try {
            // Buscar todos os formulários e filtrar os pendentes
            // Utilizamos o parâmetro "pending" para indicar que queremos apenas formulários pendentes
            val pendingForms = formRepository.getForms(type = "pending")
            emit(Result.success(pendingForms))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 