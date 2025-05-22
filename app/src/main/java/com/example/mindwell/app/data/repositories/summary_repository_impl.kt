package com.example.mindwell.app.data.repositories

import com.example.mindwell.app.data.datasources.remote.SummaryRemoteDataSource
import com.example.mindwell.app.data.mappers.SummaryMapper
import com.example.mindwell.app.domain.entities.Summary
import com.example.mindwell.app.domain.repositories.SummaryRepository
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de resumos/consolidações.
 */
@Singleton
class SummaryRepositoryImpl @Inject constructor(
    private val remote_data_source: SummaryRemoteDataSource
) : SummaryRepository {
    /**
     * Obtém o resumo mensal dos check-ins.
     * @param month Mês para o qual obter o resumo
     * @return Resumo com estatísticas consolidadas
     */
    override suspend fun get_checkin_month_summary(month: YearMonth): Summary {
        val summary_dto = remote_data_source.get_checkin_month_summary(month)
        return SummaryMapper.mapToDomain(summary_dto)
    }
    
    /**
     * Obtém o resumo semanal dos check-ins.
     * @param year Ano da semana
     * @param week_number Número da semana no ano
     * @return Resumo com estatísticas consolidadas
     */
    override suspend fun get_checkin_week_summary(year: Int, week_number: Int): Summary {
        val summary_dto = remote_data_source.get_checkin_week_summary(year, week_number)
        return SummaryMapper.mapToDomain(summary_dto)
    }
} 