package com.example.mindwell.app.presentation.screens.checkin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.domain.usecases.checkin.GetCheckinsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject
import android.util.Log

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
    private val TAG = "CheckinViewModel"
    
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
    
    init {
        load_checkins()
    }
    
    /**
     * Carrega o histórico de check-ins.
     */
    fun load_checkins() {
        state = state.copy(is_loading = true, error_message = null)
        
        Log.d(TAG, "🌐 Tentando carregar histórico de check-ins da API")
        
        // Real implementation with API
        viewModelScope.launch {
            get_checkins_use_case()
                .catch { e ->
                    Log.e(TAG, "❌ ERRO ao carregar histórico de check-ins: ${e.message}", e)
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
                        
                        Log.d(TAG, "✅ Sucesso ao carregar ${checkin_items.size} check-ins da API")
                        
                        state = state.copy(
                            checkins = checkin_items,
                            is_loading = false
                        )
                    }
                    
                    result.onFailure { e ->
                        Log.e(TAG, "❌ ERRO ao carregar histórico de check-ins: ${e.message}", e)
                        state = state.copy(
                            is_loading = false,
                            error_message = e.message ?: "Erro ao carregar histórico"
                        )
                    }
                }
        }
    }
} 