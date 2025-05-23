package com.example.mindwell.app.presentation.screens.forms

import android.util.Log
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
    private val TAG = "FormDetailViewModel"
    
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
        logDebugInfo("Inicializando FormDetailViewModel para formId=$formId")
        loadFormDetail()
    }
    
    /**
     * Carrega os detalhes do formulário.
     */
    private fun loadFormDetail() {
        state = state.copy(isLoading = true, error = null)
        
        // Implementação real com API
        Log.d(TAG, "🌐 Tentando carregar detalhes do formulário $formId da API")
        logDebugInfo("Iniciando carregamento do formulário $formId, isCheckinForm=$isCheckinForm")
        
        viewModelScope.launch {
            getFormDetailUseCase(formId).collect { result ->
                if (result.isSuccess) {
                    val formDetail = result.getOrNull()
                    Log.d(TAG, "✅ Sucesso ao carregar formulário ${formDetail?.name} da API")
                    logDebugInfo("Formulário carregado com sucesso: ${formDetail?.name}, questões: ${formDetail?.questions?.size ?: 0}")
                    state = state.copy(
                        formDetail = formDetail,
                        isLoading = false
                    )
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Erro ao carregar detalhes do formulário"
                    Log.e(TAG, "❌ ERRO ao carregar detalhes do formulário da API: $errorMsg", result.exceptionOrNull())
                    logDebugInfo("ERRO ao carregar formulário: $errorMsg")
                    
                    // Log da exceção completa para diagnóstico
                    result.exceptionOrNull()?.stackTraceToString()?.let {
                        Log.e(TAG, "Stack trace completa: $it")
                    }
                    
                    state = state.copy(
                        isLoading = false,
                        error = errorMsg
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
        logDebugInfo("Recarregando detalhes do formulário $formId")
        loadFormDetail()
    }
    
    /**
     * Avança para a próxima pergunta.
     */
    fun nextQuestion() {
        val currentForm = state.formDetail ?: return
        val totalQuestions = currentForm.questions.size
        
        if (state.currentQuestionIndex < totalQuestions - 1) {
            logDebugInfo("Avançando para próxima pergunta: ${state.currentQuestionIndex + 1}/${totalQuestions}")
            state = state.copy(currentQuestionIndex = state.currentQuestionIndex + 1)
        }
    }
    
    /**
     * Volta para a pergunta anterior.
     */
    fun previousQuestion() {
        if (state.currentQuestionIndex > 0) {
            logDebugInfo("Voltando para pergunta anterior: ${state.currentQuestionIndex - 1}")
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
        
        logDebugInfo("Respondendo questão ${question.id} com opção $selectedOptionId")
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
        val result = state.currentQuestionIndex == totalQuestions - 1 && 
                     state.answers.size == totalQuestions
        
        logDebugInfo("Verificando se pode enviar: última pergunta=${state.currentQuestionIndex == totalQuestions - 1}, " +
                    "todas respondidas=${state.answers.size == totalQuestions}, resultado=$result")
        return result
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
        if (!canSubmit()) {
            logDebugInfo("Tentativa de envio rejeitada: não pode enviar")
            return
        }
        
        state = state.copy(isSubmitting = true, error = null)
        
        // Real implementation with API
        Log.d(TAG, "🌐 Tentando enviar respostas do formulário $formId para API")
        logDebugInfo("Enviando respostas para formulário $formId, total de respostas: ${state.answers.size}")
        
        viewModelScope.launch {
            submitFormResponsesUseCase(formId, state.answers.values.toList()).collect { result ->
                result.onSuccess { responseId ->
                    Log.d(TAG, "✅ Sucesso ao enviar respostas do formulário para API. ID: $responseId")
                    logDebugInfo("Sucesso ao enviar respostas. Response ID: $responseId")
                    state = state.copy(isSubmitting = false, isSuccess = true)
                }
                
                result.onFailure { exception ->
                    val errorMsg = exception.message ?: "Erro ao enviar respostas"
                    Log.e(TAG, "❌ ERRO ao enviar respostas do formulário para API: $errorMsg", exception)
                    logDebugInfo("ERRO ao enviar respostas: $errorMsg")
                    
                    // Log da exceção completa para diagnóstico
                    exception.stackTraceToString().let {
                        Log.e(TAG, "Stack trace completa: $it")
                    }
                    
                    state = state.copy(
                        isSubmitting = false,
                        error = errorMsg
                    )
                }
            }
        }
    }
    
    /**
     * Verifica o status atual do check-in.
     * @return String com informações de diagnóstico
     */
    fun getCheckinStatus(): String {
        val status = """
            === DIAGNÓSTICO DE CHECK-IN ===
            formId: $formId
            isCheckinForm: $isCheckinForm
            Formulário carregado: ${state.formDetail != null}
            Nome do formulário: ${state.formDetail?.name}
            Questões: ${state.formDetail?.questions?.size ?: 0}
            Questão atual: ${state.currentQuestionIndex + 1}/${state.formDetail?.questions?.size ?: 0}
            Respostas: ${state.answers.size}
            Erro: ${state.error ?: "Nenhum"}
            Carregando: ${state.isLoading}
            Enviando: ${state.isSubmitting}
            Sucesso: ${state.isSuccess}
            ==========================
        """.trimIndent()
        
        Log.d(TAG, status)
        return status
    }
    
    /**
     * Registra informações detalhadas de depuração.
     */
    private fun logDebugInfo(message: String) {
        Log.d(TAG, "🔍 DEBUG: $message")
    }
} 