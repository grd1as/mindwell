package com.example.mindwell.app.data.datasources.remote

import com.example.mindwell.app.data.model.FeelingDTO
import com.example.mindwell.app.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fonte de dados remota para sentimentos.
 */
@Singleton
class FeelingRemoteDataSource @Inject constructor(
    private val api_service: ApiService
) {
    /**
     * Obtém a lista de sentimentos disponíveis para check-in.
     * Os sentimentos vêm da segunda pergunta do formulário com ID 1.
     * @return Lista de sentimentos
     */
    suspend fun get_feelings(): List<FeelingDTO> {
        // Busca os sentimentos através do formulário de check-in (ID 1)
        val formDetail = api_service.get_form_detail(1)
        
        // Extrai os sentimentos das opções da SEGUNDA pergunta do formulário (índice 1)
        // Pergunta 2: "Como você se sente hoje?" - Motivado, Cansado, Preocupado, Estressado, Animado, Satisfeito
        return formDetail.questions.getOrNull(1)?.options?.map { option ->
            FeelingDTO(
                id = option.id.toString(),
                label = option.label,
                emoji = null, // OptionDTO não tem emoji, mantemos null
                value = option.value.toIntOrNull() // Converte String para Int
            )
        } ?: emptyList()
    }
} 