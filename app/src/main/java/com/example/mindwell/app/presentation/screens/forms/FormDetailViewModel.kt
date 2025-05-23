package com.example.mindwell.app.presentation.screens.forms

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.domain.entities.Answer
import com.example.mindwell.app.domain.entities.FormDetail
import com.example.mindwell.app.domain.entities.Option
import com.example.mindwell.app.domain.entities.Question
import com.example.mindwell.app.domain.usecases.form.GetFormDetailUseCase
import com.example.mindwell.app.domain.usecases.form.SubmitFormResponsesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para tela de detalhes do formulário.
 */
@HiltViewModel
class FormDetailViewModel @Inject constructor(
    private val getFormDetailUseCase: GetFormDetailUseCase,
    private val submitFormResponsesUseCase: SubmitFormResponsesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // ID do formulário
    private val formId: Int = checkNotNull(savedStateHandle.get<String>("formId")).toInt()
    
    // Verifica se é o formulário de checkin
    private val isCheckinForm: Boolean = formId == CHECKIN_FORM_ID
    
    // ID do formulário de checkin
    companion object {
        const val CHECKIN_FORM_ID = 1
    }
    
    // Estado da tela de detalhes do formulário
    data class FormDetailState(
        val formDetail: FormDetail? = null,
        val currentQuestionIndex: Int = 0,
        val answers: Map<Int, Answer> = emptyMap(),
        val isLoading: Boolean = false,
        val isSubmitting: Boolean = false,
        val isSuccess: Boolean = false,
        val error: String? = null
    )
    
    // Estado atual da tela
    var state by mutableStateOf(FormDetailState(isLoading = true))
        private set
    
    init {
        loadFormDetail()
    }
    
    /**
     * Carrega os detalhes do formulário.
     */
    private fun loadFormDetail() {
        state = state.copy(isLoading = true, error = null)
        
        // Implementação real com API
        viewModelScope.launch {
            getFormDetailUseCase(formId).collect { result ->
                if (result.isSuccess) {
                    state = state.copy(
                        formDetail = result.getOrNull(),
                        isLoading = false
                    )
                } else {
                    state = state.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Erro ao carregar detalhes do formulário"
                    )
                }
            }
        }
    }
    
    /**
     * Recarrega os detalhes do formulário.
     * Este método público permite que a UI recarregue os dados quando necessário.
     */
    fun reloadFormDetail() {
        loadFormDetail()
    }
    
    /**
     * Avança para a próxima pergunta.
     */
    fun nextQuestion() {
        val currentForm = state.formDetail ?: return
        val totalQuestions = currentForm.questions.size
        
        if (state.currentQuestionIndex < totalQuestions - 1) {
            state = state.copy(currentQuestionIndex = state.currentQuestionIndex + 1)
        }
    }
    
    /**
     * Volta para a pergunta anterior.
     */
    fun previousQuestion() {
        if (state.currentQuestionIndex > 0) {
            state = state.copy(currentQuestionIndex = state.currentQuestionIndex - 1)
        }
    }
    
    /**
     * Registra uma resposta para a pergunta atual.
     * @param question Pergunta respondida
     * @param selectedOptionId ID da opção selecionada
     */
    fun answerQuestion(question: Question, selectedOptionId: Int) {
        val answer = Answer(
            question_id = question.id,
            option_id = selectedOptionId
        )
        
        val updatedAnswers = state.answers.toMutableMap().apply {
            put(question.id, answer)
        }
        
        state = state.copy(answers = updatedAnswers)
    }
    
    /**
     * Verifica se pode avançar para a próxima pergunta.
     * @return true se pode avançar
     */
    fun canAdvance(): Boolean {
        val currentForm = state.formDetail ?: return false
        val currentQuestion = getCurrentQuestion() ?: return false
        return state.answers.containsKey(currentQuestion.id)
    }
    
    /**
     * Verifica se pode enviar o formulário.
     * @return true se pode enviar
     */
    fun canSubmit(): Boolean {
        val currentForm = state.formDetail ?: return false
        val totalQuestions = currentForm.questions.size
        
        // Verifica se é a última pergunta e se todas foram respondidas
        return state.currentQuestionIndex == totalQuestions - 1 && 
               state.answers.size == totalQuestions
    }
    
    /**
     * Obtém a pergunta atual.
     * @return Pergunta atual ou null
     */
    fun getCurrentQuestion(): Question? {
        return state.formDetail?.questions?.getOrNull(state.currentQuestionIndex)
    }
    
    /**
     * Envia as respostas do formulário.
     */
    fun submitForm() {
        if (!canSubmit()) return
        
        state = state.copy(isSubmitting = true, error = null)
        
        // Real implementation with API
        viewModelScope.launch {
            submitFormResponsesUseCase(formId, state.answers.values.toList()).collect { result ->
                result.onSuccess { responseId ->
                    state = state.copy(isSubmitting = false, isSuccess = true)
                }
                
                result.onFailure { exception ->
                    state = state.copy(
                        isSubmitting = false,
                        error = exception.message ?: "Erro ao enviar respostas"
                    )
                }
            }
        }
    }
} 