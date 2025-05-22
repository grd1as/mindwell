package com.example.mindwell.app.presentation.screens.resources

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResourceDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    var state by mutableStateOf(ResourceDetailState())
        private set
    
    fun loadResource(resourceId: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            
            try {
                // Simula carregamento da API
                delay(500)
                
                // Em um cenário real, isso viria de um repositório
                // Aqui estamos apenas simulando dados
                val resource = when (resourceId) {
                    "breathing_478" -> ResourceDetailModel(
                        id = "breathing_478",
                        title = "Técnica de Respiração 4-7-8",
                        description = "Uma técnica simples e eficaz para reduzir ansiedade e promover relaxamento em momentos de estresse. Desenvolvida pelo Dr. Andrew Weil, esta técnica é baseada na respiração pranayama da yoga e atua como um tranquilizante natural para o sistema nervoso.",
                        categoryId = "breathing",
                        durationMinutes = 5,
                        steps = listOf(
                            "Expire completamente pela boca, fazendo um som de whoosh",
                            "Feche a boca e inspire silenciosamente pelo nariz contando até 4",
                            "Segure a respiração contando até 7",
                            "Expire completamente pela boca, fazendo um som de whoosh e contando até 8",
                            "Isso completa um ciclo. Repita o ciclo mais três vezes"
                        )
                    )
                    "meditation_body_scan" -> ResourceDetailModel(
                        id = "meditation_body_scan",
                        title = "Meditação Body Scan",
                        description = "Uma meditação guiada que ajuda a reconectar com o corpo e liberar tensões acumuladas. Esta prática aumenta a consciência corporal e induz um estado de relaxamento profundo.",
                        categoryId = "meditation",
                        durationMinutes = 10,
                        steps = listOf(
                            "Deite-se de costas em uma posição confortável",
                            "Feche os olhos e traga sua atenção para a respiração",
                            "Gradualmente, mova sua atenção da cabeça aos pés",
                            "Observe cada parte do corpo sem julgamento",
                            "Note sensações, tensões ou desconfortos",
                            "Permita que cada área relaxe à medida que você direciona sua respiração para ela"
                        )
                    )
                    "journaling_gratitude" -> ResourceDetailModel(
                        id = "journaling_gratitude",
                        title = "Diário de Gratidão",
                        description = "Prática diária para focar nos aspectos positivos da vida e cultivar uma perspectiva otimista. Estudos mostram que escrever regularmente sobre gratidão pode melhorar o humor, aumentar a satisfação com a vida e até melhorar a saúde física.",
                        categoryId = "journaling",
                        durationMinutes = 5,
                        steps = listOf(
                            "Escolha um momento tranquilo do seu dia",
                            "Liste três coisas pelas quais você é grato hoje",
                            "Seja específico e explique por que você é grato por cada item",
                            "Reflita sobre como essas coisas positivas afetaram seu dia",
                            "Pratique diariamente para melhores resultados"
                        )
                    )
                    else -> null
                }
                
                state = if (resource != null) {
                    state.copy(
                        isLoading = false,
                        resource = resource
                    )
                } else {
                    state.copy(
                        isLoading = false,
                        error = "Recurso não encontrado"
                    )
                }
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar o recurso"
                )
            }
        }
    }
}

data class ResourceDetailState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val resource: ResourceDetailModel? = null
)

data class ResourceDetailModel(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val durationMinutes: Int,
    val steps: List<String> = emptyList(),
    val completed: Boolean = false
) 