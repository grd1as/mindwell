package com.example.mindwell.app.presentation.screens.resources

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.domain.entities.ResourceDetail
import com.example.mindwell.app.domain.usecases.resource.GetResourceDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResourceDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getResourceDetailUseCase: GetResourceDetailUseCase
) : ViewModel() {
    private val TAG = "ResourceDetailViewModel"
    
    var state by mutableStateOf(ResourceDetailState())
        private set
    
    fun loadResource(resourceId: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            
            try {
                // Verificar se é uma dica personalizada do Gemini
                if (resourceId.startsWith("gemini_tip_") || resourceId.startsWith("breathing_") || resourceId.startsWith("meditation_")) {
                    Log.d(TAG, "💡 Carregando dica personalizada: $resourceId")
                    val mockResource = createMockResourceFromId(resourceId)
                    state = state.copy(
                        isLoading = false,
                        resource = mockResource
                    )
                    return@launch
                }
                
                Log.d(TAG, "🌐 Tentando carregar detalhes do recurso $resourceId da API")
                
                getResourceDetailUseCase(resourceId).collect { result ->
                    result.onSuccess { resource ->
                        Log.d(TAG, "✅ Sucesso ao carregar recurso da API")
                        state = state.copy(
                            isLoading = false,
                            resource = resource
                        )
                    }
                    result.onFailure { exception ->
                        Log.e(TAG, "❌ ERRO ao carregar recurso: ${exception.message}", exception)
                        state = state.copy(
                            isLoading = false,
                            error = exception.message ?: "Erro ao carregar o recurso"
                        )
                    }
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ ERRO ao carregar recurso: ${e.message}", e)
                state = state.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar o recurso"
                )
            }
        }
    }
    
    /**
     * Cria um recurso mockado baseado no ID da dica personalizada
     */
    private fun createMockResourceFromId(resourceId: String): ResourceDetail {
        return when {
            resourceId.contains("breathing") || resourceId.contains("respiração") -> {
                ResourceDetail(
                    id = resourceId,
                    title = "Exercício de Respiração Personalizado",
                    description = "Um exercício de respiração criado especialmente para você com base no seu estado atual.",
                    categoryId = "breathing",
                    durationMinutes = 5,
                    steps = listOf(
                        "Encontre um local calmo e confortável",
                        "Sente-se ou deite-se em uma posição relaxada",
                        "Inspire profundamente pelo nariz por 4 segundos",
                        "Segure a respiração por 7 segundos",
                        "Expire lentamente pela boca por 8 segundos",
                        "Repita o ciclo por 3-5 vezes",
                        "Observe como seu corpo se sente mais relaxado"
                    ),
                    completed = false
                )
            }
            resourceId.contains("meditation") || resourceId.contains("meditação") -> {
                ResourceDetail(
                    id = resourceId,
                    title = "Meditação Personalizada",
                    description = "Uma sessão de meditação criada com base no seu perfil e necessidades atuais.",
                    categoryId = "meditation",
                    durationMinutes = 10,
                    steps = listOf(
                        "Encontre um espaço tranquilo",
                        "Sente-se confortavelmente com as costas eretas",
                        "Feche os olhos suavemente",
                        "Concentre-se na sua respiração natural",
                        "Quando a mente divagar, gentilmente retorne à respiração",
                        "Continue por 10 minutos",
                        "Termine lentamente, movendo dedos e dedos dos pés",
                        "Abra os olhos quando se sentir pronto"
                    ),
                    completed = false
                )
            }
            resourceId.contains("exercise") || resourceId.contains("exercício") -> {
                ResourceDetail(
                    id = resourceId,
                    title = "Exercício Personalizado",
                    description = "Uma atividade física adaptada ao seu nível e preferências.",
                    categoryId = "exercise",
                    durationMinutes = 15,
                    steps = listOf(
                        "Faça um aquecimento leve por 2 minutos",
                        "Realize 10 polichinelos",
                        "Faça 15 agachamentos",
                        "Execute 10 flexões (adapte conforme necessário)",
                        "Prancha por 30 segundos",
                        "Descanse por 1 minuto",
                        "Repita a sequência 2-3 vezes",
                        "Termine com alongamentos suaves"
                    ),
                    completed = false
                )
            }
            resourceId.contains("sleep") || resourceId.contains("sono") -> {
                ResourceDetail(
                    id = resourceId,
                    title = "Rotina para Melhor Sono",
                    description = "Técnicas personalizadas para melhorar a qualidade do seu sono.",
                    categoryId = "sleep",
                    durationMinutes = 20,
                    steps = listOf(
                        "Desligue dispositivos eletrônicos 1 hora antes de dormir",
                        "Tome um banho morno relaxante",
                        "Prepare seu quarto: temperatura fresca, escuro, silencioso",
                        "Pratique respiração profunda por 5 minutos",
                        "Leia algo relaxante ou ouça música suave",
                        "Evite pensamentos estressantes",
                        "Mantenha horários regulares de sono",
                        "Se não conseguir dormir em 20 min, levante e faça algo calmo"
                    ),
                    completed = false
                )
            }
            resourceId.contains("journaling") || resourceId.contains("diário") -> {
                ResourceDetail(
                    id = resourceId,
                    title = "Exercício de Escrita Reflexiva",
                    description = "Uma atividade de escrita para processar seus sentimentos e pensamentos.",
                    categoryId = "journaling",
                    durationMinutes = 15,
                    steps = listOf(
                        "Pegue papel e caneta ou abra um aplicativo de notas",
                        "Responda: Como me sinto neste momento?",
                        "Escreva sobre 3 coisas pelas quais é grato hoje",
                        "Descreva um desafio atual e possíveis soluções",
                        "Anote um objetivo pequeno para amanhã",
                        "Termine com uma afirmação positiva sobre si mesmo",
                        "Releia o que escreveu sem julgamentos",
                        "Guarde suas reflexões em local privado"
                    ),
                    completed = false
                )
            }
            else -> {
                ResourceDetail(
                    id = resourceId,
                    title = "Dica Personalizada",
                    description = "Uma recomendação criada especialmente para você com base no seu perfil atual.",
                    categoryId = "mindfulness",
                    durationMinutes = 5,
                    steps = listOf(
                        "Reserve um momento para si mesmo",
                        "Respire profundamente três vezes",
                        "Observe seus pensamentos sem julgamento",
                        "Lembre-se de que cada dia é uma nova oportunidade",
                        "Pratique autocompaixão",
                        "Concentre-se no momento presente",
                        "Termine com gratidão pelo que tem"
                    ),
                    completed = false
                )
            }
        }
    }
}

data class ResourceDetailState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val resource: ResourceDetail? = null
) 