package com.example.mindwell.app.presentation.screens.checkin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.domain.usecases.checkin.GetCheckinsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject

/**
 * Item de histórico de check-in para exibição na interface.
 */
data class CheckinHistoryItem(
    val id: Int,
    val date: String,
    val emoji: String,
    val mood: String,
    val note: String = "",
    val streak: Int = 0
)

/**
 * ViewModel para tela de histórico de check-ins.
 */
@HiltViewModel
class CheckinViewModel @Inject constructor(
    private val getCheckinsUseCase: GetCheckinsUseCase
) : ViewModel() {
    // Estado da tela de histórico de check-ins
    data class CheckinHistoryState(
        val checkins: List<CheckinHistoryItem> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )
    
    // Mapeamento de emojis para níveis de humor
    private val moodEmojis = mapOf(
        0 to Pair("❤️", "Muito Mal"),
        1 to Pair("🧡", "Mal"),
        2 to Pair("💛", "Normal"),
        3 to Pair("💚", "Bom"),
        4 to Pair("💙", "Muito Bom")
    )
    
    // Estado atual da tela
    var state by mutableStateOf(CheckinHistoryState(isLoading = true))
        private set
    
    // Formatador de data para exibição
    private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale("pt", "BR"))
    
    // Mock data para teste de layout
    private val mockCheckins = listOf(
        CheckinHistoryItem(
            id = 1,
            date = "15 de junho de 2023",
            emoji = "💚",
            mood = "Bom",
            note = "Foi um dia produtivo, consegui terminar várias tarefas pendentes.",
            streak = 3
        ),
        CheckinHistoryItem(
            id = 2,
            date = "14 de junho de 2023",
            emoji = "💚",
            mood = "Bom",
            note = "Dia tranquilo, tive uma reunião importante que foi bem sucedida.",
            streak = 2
        ),
        CheckinHistoryItem(
            id = 3,
            date = "13 de junho de 2023",
            emoji = "💛",
            mood = "Normal",
            note = "Dia comum, nada especial aconteceu.",
            streak = 1
        ),
        CheckinHistoryItem(
            id = 4,
            date = "11 de junho de 2023",
            emoji = "🧡",
            mood = "Mal",
            note = "Acordei me sentindo um pouco indisposto, tive dificuldade para me concentrar.",
            streak = 0
        ),
        CheckinHistoryItem(
            id = 5,
            date = "10 de junho de 2023",
            emoji = "💙",
            mood = "Muito Bom",
            note = "Dia excelente! Saí com amigos e me diverti bastante.",
            streak = 2
        ),
        CheckinHistoryItem(
            id = 6,
            date = "9 de junho de 2023",
            emoji = "💛",
            mood = "Normal",
            note = "",
            streak = 1
        ),
        CheckinHistoryItem(
            id = 7,
            date = "7 de junho de 2023",
            emoji = "❤️",
            mood = "Muito Mal",
            note = "Dia difícil, muitos problemas para resolver e pouco tempo.",
            streak = 0
        )
    )
    
    init {
        loadCheckins(useMockData = true) // Use mock data for layout testing
    }
    
    /**
     * Carrega o histórico de check-ins.
     * @param useMockData Se true, usa dados mockados para teste de layout
     */
    fun loadCheckins(useMockData: Boolean = true) {
        state = state.copy(isLoading = true, errorMessage = null)
        
        if (useMockData) {
            // Use mock data for testing
            viewModelScope.launch {
                // Simulate network delay
                delay(800)
                state = state.copy(
                    checkins = mockCheckins,
                    isLoading = false
                )
            }
            return
        }
        
        // Real implementation with API
        viewModelScope.launch {
            getCheckinsUseCase()
                .catch { e ->
                    state = state.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Erro ao carregar histórico"
                    )
                }
                .collect { result ->
                    result.onSuccess { checkinPage ->
                        // Mapear os check-ins do domínio para o modelo de UI
                        val checkinItems = checkinPage.items.map { checkin ->
                            // Obter o valor da emoção a partir das respostas
                            val emotionValue = checkin.answers.find { it.questionId == 1 }?.value?.toIntOrNull() ?: 2
                            val moodPair = moodEmojis[emotionValue] ?: Pair("😐", "Normal")
                            
                            // Obter a nota do check-in
                            val note = checkin.answers.find { it.questionId == 2 }?.value ?: ""
                            
                            CheckinHistoryItem(
                                id = checkin.checkinId,
                                date = checkin.timestamp.format(dateFormatter),
                                emoji = moodPair.first,
                                mood = moodPair.second,
                                note = note,
                                streak = checkin.streak ?: 0
                            )
                        }
                        
                        state = state.copy(
                            checkins = checkinItems,
                            isLoading = false
                        )
                    }
                    
                    result.onFailure { e ->
                        state = state.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Erro ao carregar histórico"
                        )
                    }
                }
        }
    }
} 