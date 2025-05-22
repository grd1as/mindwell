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
    private val get_checkins_use_case: GetCheckinsUseCase
) : ViewModel() {
    // Estado da tela de histórico de check-ins
    data class CheckinHistoryState(
        val checkins: List<CheckinHistoryItem> = emptyList(),
        val is_loading: Boolean = false,
        val error_message: String? = null
    )
    
    // Mapeamento de emojis para níveis de humor
    private val mood_emojis = mapOf(
        0 to Pair("❤️", "Muito Mal"),
        1 to Pair("🧡", "Mal"),
        2 to Pair("💛", "Normal"),
        3 to Pair("💚", "Bom"),
        4 to Pair("💙", "Muito Bom")
    )
    
    // Estado atual da tela
    var state by mutableStateOf(CheckinHistoryState(is_loading = true))
        private set
    
    // Formatador de data para exibição
    private val date_formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale("pt", "BR"))
    
    // Mock data para teste de layout
    private val mock_checkins = listOf(
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
        load_checkins(use_mock_data = true) // Use mock data for layout testing
    }
    
    /**
     * Carrega o histórico de check-ins.
     * @param use_mock_data Se true, usa dados mockados para teste de layout
     */
    fun load_checkins(use_mock_data: Boolean = true) {
        state = state.copy(is_loading = true, error_message = null)
        
        if (use_mock_data) {
            // Use mock data for testing
            viewModelScope.launch {
                // Simulate network delay
                delay(800)
                state = state.copy(
                    checkins = mock_checkins,
                    is_loading = false
                )
            }
            return
        }
        
        // Real implementation with API
        viewModelScope.launch {
            get_checkins_use_case()
                .catch { e ->
                    state = state.copy(
                        is_loading = false,
                        error_message = e.message ?: "Erro ao carregar histórico"
                    )
                }
                .collect { result ->
                    result.onSuccess { checkin_page ->
                        // Mapear os check-ins do domínio para o modelo de UI
                        val checkin_items = checkin_page.items.map { checkin ->
                            // Obter o valor da emoção
                            val emotion_value = checkin.emotion.value
                            val mood_pair = mood_emojis[emotion_value] ?: Pair("😐", "Normal")
                            
                            CheckinHistoryItem(
                                id = checkin.id.toInt(),
                                date = checkin.date,
                                emoji = mood_pair.first,
                                mood = mood_pair.second,
                                note = checkin.note ?: "",
                                streak = checkin.streak ?: 0
                            )
                        }
                        
                        state = state.copy(
                            checkins = checkin_items,
                            is_loading = false
                        )
                    }
                    
                    result.onFailure { e ->
                        state = state.copy(
                            is_loading = false,
                            error_message = e.message ?: "Erro ao carregar histórico"
                        )
                    }
                }
        }
    }
} 