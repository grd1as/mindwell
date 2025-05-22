package com.example.mindwell.app.domain.usecases.checkin

import com.example.mindwell.app.domain.entities.CheckinPage
import com.example.mindwell.app.domain.repositories.CheckinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Caso de uso para obter histórico de check-ins.
 */
interface GetCheckinsUseCase {
    /**
     * Obtém histórico de check-ins com paginação e filtros.
     * @param page Número da página (padrão 0)
     * @param size Tamanho da página (padrão 20)
     * @param from Data inicial para filtro (opcional)
     * @param to Data final para filtro (opcional)
     * @return Flow com o resultado contendo a página de check-ins
     */
    operator fun invoke(
        page: Int = 0,
        size: Int = 20,
        from: LocalDate? = null,
        to: LocalDate? = null
    ): Flow<Result<CheckinPage>>
}

/**
 * Implementação do caso de uso para obter histórico de check-ins.
 */
class GetCheckinsUseCaseImpl @Inject constructor(
    private val checkin_repository: CheckinRepository
) : GetCheckinsUseCase {
    override operator fun invoke(
        page: Int,
        size: Int,
        from: LocalDate?,
        to: LocalDate?
    ): Flow<Result<CheckinPage>> = flow {
        try {
            val checkins = checkin_repository.get_checkins(page, size, from, to)
            emit(Result.success(checkins))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 