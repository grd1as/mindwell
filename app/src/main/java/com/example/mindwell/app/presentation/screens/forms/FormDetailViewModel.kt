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
    
    // Mock data para teste de layout
    private val mockFormDetails = mapOf(
        1 to FormDetail(
            id = 1,
            code = "PHQ9",
            name = "Questionário de Saúde do Paciente (PHQ-9)",
            questions = listOf(
                Question(
                    id = 101,
                    ordinal = 1,
                    type = "single",
                    text = "Nas últimas duas semanas, com que frequência você sentiu pouco interesse ou prazer em fazer as coisas?",
                    options = listOf(
                        Option(id = 1001, value = "0", label = "Nenhuma vez"),
                        Option(id = 1002, value = "1", label = "Vários dias"),
                        Option(id = 1003, value = "2", label = "Mais da metade dos dias"),
                        Option(id = 1004, value = "3", label = "Quase todos os dias")
                    )
                ),
                Question(
                    id = 102,
                    ordinal = 2,
                    type = "single",
                    text = "Nas últimas duas semanas, com que frequência você se sentiu para baixo, deprimido(a) ou sem esperança?",
                    options = listOf(
                        Option(id = 1005, value = "0", label = "Nenhuma vez"),
                        Option(id = 1006, value = "1", label = "Vários dias"),
                        Option(id = 1007, value = "2", label = "Mais da metade dos dias"),
                        Option(id = 1008, value = "3", label = "Quase todos os dias")
                    )
                ),
                Question(
                    id = 103,
                    ordinal = 3,
                    type = "single",
                    text = "Nas últimas duas semanas, com que frequência você teve dificuldade para pegar no sono ou permanecer dormindo, ou dormiu mais do que de costume?",
                    options = listOf(
                        Option(id = 1009, value = "0", label = "Nenhuma vez"),
                        Option(id = 1010, value = "1", label = "Vários dias"),
                        Option(id = 1011, value = "2", label = "Mais da metade dos dias"),
                        Option(id = 1012, value = "3", label = "Quase todos os dias")
                    )
                )
            )
        ),
        2 to FormDetail(
            id = 2,
            code = "GAD7",
            name = "Transtorno de Ansiedade Generalizada (GAD-7)",
            questions = listOf(
                Question(
                    id = 201,
                    ordinal = 1,
                    type = "single",
                    text = "Nas últimas duas semanas, com que frequência você se sentiu nervoso(a), ansioso(a) ou muito tenso(a)?",
                    options = listOf(
                        Option(id = 2001, value = "0", label = "Nenhuma vez"),
                        Option(id = 2002, value = "1", label = "Vários dias"),
                        Option(id = 2003, value = "2", label = "Mais da metade dos dias"),
                        Option(id = 2004, value = "3", label = "Quase todos os dias")
                    )
                ),
                Question(
                    id = 202,
                    ordinal = 2,
                    type = "single",
                    text = "Nas últimas duas semanas, com que frequência você não foi capaz de impedir ou de controlar as preocupações?",
                    options = listOf(
                        Option(id = 2005, value = "0", label = "Nenhuma vez"),
                        Option(id = 2006, value = "1", label = "Vários dias"),
                        Option(id = 2007, value = "2", label = "Mais da metade dos dias"),
                        Option(id = 2008, value = "3", label = "Quase todos os dias")
                    )
                ),
                Question(
                    id = 203,
                    ordinal = 3,
                    type = "single",
                    text = "Nas últimas duas semanas, com que frequência você se sentiu agitado(a) de tal maneira que era difícil ficar parado(a)?",
                    options = listOf(
                        Option(id = 2009, value = "0", label = "Nenhuma vez"),
                        Option(id = 2010, value = "1", label = "Vários dias"),
                        Option(id = 2011, value = "2", label = "Mais da metade dos dias"),
                        Option(id = 2012, value = "3", label = "Quase todos os dias")
                    )
                )
            )
        ),
        3 to FormDetail(
            id = 3,
            code = "PSS10",
            name = "Escala de Estresse Percebido (PSS-10)",
            questions = listOf(
                Question(
                    id = 301,
                    ordinal = 1,
                    type = "single",
                    text = "No último mês, com que frequência você tem ficado triste por causa de algo que aconteceu inesperadamente?",
                    options = listOf(
                        Option(id = 3001, value = "0", label = "Nunca"),
                        Option(id = 3002, value = "1", label = "Quase nunca"),
                        Option(id = 3003, value = "2", label = "Às vezes"),
                        Option(id = 3004, value = "3", label = "Frequentemente"),
                        Option(id = 3005, value = "4", label = "Muito frequentemente")
                    )
                ),
                Question(
                    id = 302,
                    ordinal = 2,
                    type = "single",
                    text = "No último mês, com que frequência você tem se sentido incapaz de controlar as coisas importantes em sua vida?",
                    options = listOf(
                        Option(id = 3006, value = "0", label = "Nunca"),
                        Option(id = 3007, value = "1", label = "Quase nunca"),
                        Option(id = 3008, value = "2", label = "Às vezes"),
                        Option(id = 3009, value = "3", label = "Frequentemente"),
                        Option(id = 3010, value = "4", label = "Muito frequentemente")
                    )
                )
            )
        ),
        4 to FormDetail(
            id = 4,
            code = "SLEEP",
            name = "Questionário de Qualidade do Sono",
            questions = listOf(
                Question(
                    id = 401,
                    ordinal = 1,
                    type = "single",
                    text = "Durante a última semana, como você classificaria a qualidade do seu sono de maneira geral?",
                    options = listOf(
                        Option(id = 4001, value = "0", label = "Muito boa"),
                        Option(id = 4002, value = "1", label = "Boa"),
                        Option(id = 4003, value = "2", label = "Regular"),
                        Option(id = 4004, value = "3", label = "Ruim"),
                        Option(id = 4005, value = "4", label = "Muito ruim")
                    )
                ),
                Question(
                    id = 402,
                    ordinal = 2,
                    type = "single",
                    text = "Durante a última semana, com que frequência você teve dificuldade para adormecer em até 30 minutos?",
                    options = listOf(
                        Option(id = 4006, value = "0", label = "Nenhuma vez"),
                        Option(id = 4007, value = "1", label = "Menos de uma vez por semana"),
                        Option(id = 4008, value = "2", label = "Uma ou duas vezes por semana"),
                        Option(id = 4009, value = "3", label = "Três ou mais vezes por semana")
                    )
                )
            )
        ),
        5 to FormDetail(
            id = 5,
            code = "MOOD",
            name = "Rastreamento de Humor Diário",
            questions = listOf(
                Question(
                    id = 501,
                    ordinal = 1,
                    type = "single",
                    text = "Como você avalia seu humor hoje?",
                    options = listOf(
                        Option(id = 5001, value = "0", label = "Muito ruim"),
                        Option(id = 5002, value = "1", label = "Ruim"),
                        Option(id = 5003, value = "2", label = "Regular"),
                        Option(id = 5004, value = "3", label = "Bom"),
                        Option(id = 5005, value = "4", label = "Muito bom")
                    )
                ),
                Question(
                    id = 502,
                    ordinal = 2,
                    type = "single",
                    text = "Quão bem você conseguiu lidar com o estresse hoje?",
                    options = listOf(
                        Option(id = 5006, value = "0", label = "Muito mal"),
                        Option(id = 5007, value = "1", label = "Mal"),
                        Option(id = 5008, value = "2", label = "Razoável"),
                        Option(id = 5009, value = "3", label = "Bem"),
                        Option(id = 5010, value = "4", label = "Muito bem")
                    )
                )
            )
        )
    )
    
    init {
        loadFormDetail(useMockData = true) // Use mock data for layout testing
    }
    
    /**
     * Carrega os detalhes do formulário.
     * @param useMockData Se true, usa dados mockados para teste de layout
     */
    private fun loadFormDetail(useMockData: Boolean = true) {
        state = state.copy(isLoading = true, error = null)
        
        if (useMockData) {
            // Use mock data for testing
            viewModelScope.launch {
                // Simulate network delay
                delay(800)
                val mockDetail = mockFormDetails[formId]
                if (mockDetail != null) {
                    state = state.copy(
                        formDetail = mockDetail,
                        isLoading = false
                    )
                } else {
                    state = state.copy(
                        isLoading = false,
                        error = "Formulário não encontrado"
                    )
                }
            }
            return
        }
        
        // Real implementation with API
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
            questionId = question.id,
            optionId = selectedOptionId
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
        
        // Use mock submission for testing
        if (true) {
            viewModelScope.launch {
                // Simulate network delay
                delay(1500)
                state = state.copy(isSubmitting = false, isSuccess = true)
            }
            return
        }
        
        // Real implementation with API
        viewModelScope.launch {
            try {
                submitFormResponsesUseCase(formId, state.answers.values.toList())
                state = state.copy(isSubmitting = false, isSuccess = true)
            } catch (e: Exception) {
                state = state.copy(
                    isSubmitting = false,
                    error = e.message ?: "Erro ao enviar respostas"
                )
            }
        }
    }
} 