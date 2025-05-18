package com.example.mindwell.app.presentation.screens.assessment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.domain.entities.Assessment
import com.example.mindwell.app.domain.entities.AssessmentQuestion
import com.example.mindwell.app.domain.entities.AssessmentResult
import com.example.mindwell.app.domain.entities.QuestionCategory
import com.example.mindwell.app.domain.entities.QuestionType
import com.example.mindwell.app.domain.entities.RiskLevel
import com.example.mindwell.app.domain.usecases.assessment.SaveAssessmentUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Estado da UI para a tela de avaliação
 */
data class AssessmentUiState(
    val assessment: Assessment? = null,
    val currentQuestionIndex: Int = 0,
    val isSubmitting: Boolean = false,
    val isComplete: Boolean = false,
    val errorMessage: String? = null,
    val progressPercent: Float = 0f,
    val recommendationAfterCompletion: List<String> = emptyList()
)

/**
 * ViewModel para a tela de avaliação
 */
class AssessmentViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(AssessmentUiState())
    val uiState: StateFlow<AssessmentUiState> = _uiState.asStateFlow()
    
    // Mock implementation of SaveAssessmentUseCase
    private val saveAssessmentUseCase: SaveAssessmentUseCase = object : SaveAssessmentUseCase {
        override suspend operator fun invoke(assessment: Assessment): Result<Unit> {
            return Result.success(Unit)
        }
    }
    
    init {
        initializeAssessment()
    }
    
    /**
     * Inicializa uma nova avaliação
     */
    private fun initializeAssessment() {
        val questions = generateQuestions()
        val assessment = Assessment(
            date = LocalDate.now(),
            questions = questions,
            completed = false
        )
        
        _uiState.update { 
            it.copy(
                assessment = assessment,
                currentQuestionIndex = 0,
                progressPercent = 0f
            )
        }
    }
    
    /**
     * Gera as perguntas da avaliação
     */
    private fun generateQuestions(): List<AssessmentQuestion> {
        return listOf(
            // Carga de trabalho
            AssessmentQuestion(
                id = "workload_1",
                text = "Tenho que trabalhar muito intensamente?",
                type = QuestionType.SCALE,
                category = QuestionCategory.WORKLOAD
            ),
            AssessmentQuestion(
                id = "workload_2",
                text = "Tenho que negligenciar algumas tarefas porque tenho muito o que fazer?",
                type = QuestionType.SCALE,
                category = QuestionCategory.WORKLOAD
            ),
            AssessmentQuestion(
                id = "workload_3",
                text = "Sinto-me pressionado para trabalhar horas extras?",
                type = QuestionType.SCALE,
                category = QuestionCategory.WORKLOAD
            ),
            
            // Autonomia
            AssessmentQuestion(
                id = "autonomy_1",
                text = "Tenho controle sobre como fazer meu trabalho?",
                type = QuestionType.SCALE,
                category = QuestionCategory.AUTONOMY
            ),
            AssessmentQuestion(
                id = "autonomy_2",
                text = "Posso decidir quando fazer uma pausa?",
                type = QuestionType.SCALE,
                category = QuestionCategory.AUTONOMY
            ),
            
            // Relacionamentos
            AssessmentQuestion(
                id = "relationships_1",
                text = "Sou sujeito a bullying ou assédio pessoal no trabalho?",
                type = QuestionType.SCALE,
                category = QuestionCategory.RELATIONSHIPS
            ),
            AssessmentQuestion(
                id = "relationships_2",
                text = "Existem atritos ou conflitos entre colegas?",
                type = QuestionType.SCALE,
                category = QuestionCategory.RELATIONSHIPS
            ),
            
            // Clareza de papéis
            AssessmentQuestion(
                id = "role_1",
                text = "Sei o que é esperado de mim no trabalho?",
                type = QuestionType.SCALE,
                category = QuestionCategory.ROLE_CLARITY
            ),
            AssessmentQuestion(
                id = "role_2",
                text = "Minhas responsabilidades são claras?",
                type = QuestionType.YES_NO,
                category = QuestionCategory.ROLE_CLARITY
            ),
            
            // Gerenciamento de mudanças
            AssessmentQuestion(
                id = "change_1",
                text = "Participo de consultas sobre mudanças no trabalho?",
                type = QuestionType.SCALE,
                category = QuestionCategory.CHANGE
            ),
            
            // Suporte
            AssessmentQuestion(
                id = "support_1",
                text = "Recebo apoio adequado dos meus colegas?",
                type = QuestionType.SCALE,
                category = QuestionCategory.SUPPORT
            ),
            AssessmentQuestion(
                id = "support_2",
                text = "Recebo feedback regular sobre meu trabalho?",
                type = QuestionType.SCALE,
                category = QuestionCategory.SUPPORT
            ),
            
            // Equilíbrio entre vida pessoal e trabalho
            AssessmentQuestion(
                id = "balance_1",
                text = "Consigo conciliar minha vida pessoal com o trabalho?",
                type = QuestionType.SCALE,
                category = QuestionCategory.WORK_LIFE_BALANCE
            ),
            AssessmentQuestion(
                id = "balance_2",
                text = "O trabalho interfere negativamente na minha vida pessoal?",
                type = QuestionType.SCALE,
                category = QuestionCategory.WORK_LIFE_BALANCE
            ),
            
            // Pergunta final aberta
            AssessmentQuestion(
                id = "feedback",
                text = "Há algo mais que você gostaria de compartilhar sobre seu ambiente de trabalho?",
                type = QuestionType.TEXT,
                category = QuestionCategory.SUPPORT,
                required = false
            )
        )
    }
    
    /**
     * Responde a pergunta atual
     */
    fun answerCurrentQuestion(answer: Any) {
        val currentState = _uiState.value
        val assessment = currentState.assessment ?: return
        val questions = assessment.questions.toMutableList()
        val currentIndex = currentState.currentQuestionIndex
        
        if (currentIndex >= questions.size) return
        
        // Atualiza a resposta da pergunta atual
        val updatedQuestion = questions[currentIndex].copy(answer = answer)
        questions[currentIndex] = updatedQuestion
        
        // Verifica se é a última pergunta
        val isLastQuestion = currentIndex == questions.size - 1
        
        // Atualiza a avaliação com a nova resposta
        val updatedAssessment = assessment.copy(questions = questions)
        
        // Atualiza o estado
        _uiState.update { state ->
            state.copy(
                assessment = updatedAssessment,
                currentQuestionIndex = if (isLastQuestion) currentIndex else currentIndex + 1,
                progressPercent = calculateProgress(currentIndex + 1, questions.size),
                isComplete = isLastQuestion && isAssessmentComplete(updatedAssessment)
            )
        }
        
        // Se for a última pergunta, processa os resultados
        if (isLastQuestion) {
            processAssessmentResults()
        }
    }
    
    /**
     * Avança para a próxima pergunta sem responder a atual
     * (usado para perguntas não obrigatórias)
     */
    fun skipCurrentQuestion() {
        val currentState = _uiState.value
        val currentIndex = currentState.currentQuestionIndex
        val assessment = currentState.assessment ?: return
        val questions = assessment.questions
        
        if (currentIndex >= questions.size) return
        
        // Verifica se a pergunta atual é obrigatória
        if (questions[currentIndex].required) return
        
        // Verifica se é a última pergunta
        val isLastQuestion = currentIndex == questions.size - 1
        
        // Atualiza o estado
        _uiState.update { state ->
            state.copy(
                currentQuestionIndex = if (isLastQuestion) currentIndex else currentIndex + 1,
                progressPercent = calculateProgress(currentIndex + 1, questions.size),
                isComplete = isLastQuestion && isAssessmentComplete(assessment)
            )
        }
        
        // Se for a última pergunta, processa os resultados
        if (isLastQuestion) {
            processAssessmentResults()
        }
    }
    
    /**
     * Calcula o progresso atual
     */
    private fun calculateProgress(answered: Int, total: Int): Float {
        return (answered.toFloat() / total.toFloat()) * 100f
    }
    
    /**
     * Verifica se a avaliação está completa
     */
    private fun isAssessmentComplete(assessment: Assessment): Boolean {
        return assessment.questions.all { !it.required || it.answer != null }
    }
    
    /**
     * Processa os resultados da avaliação
     */
    private fun processAssessmentResults() {
        val assessment = _uiState.value.assessment ?: return
        
        // Calculate the score
        val (totalScore, categorizedScores) = calculateScores(assessment)
        
        // Determine risk level
        val riskLevel = determineRiskLevel(totalScore)
        
        // Generate recommendations
        val recommendations = generateRecommendations(riskLevel, categorizedScores)
        
        // Create the result
        val result = AssessmentResult(
            score = totalScore,
            riskLevel = riskLevel,
            recommendations = recommendations
        )
        
        // Update the assessment with the results
        val updatedAssessment = assessment.copy(
            completed = true,
            result = result
        )
        
        // Save the completed assessment
        viewModelScope.launch {
            try {
                val result = saveAssessmentUseCase(updatedAssessment)
                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            assessment = updatedAssessment,
                            isComplete = true,
                            recommendationAfterCompletion = recommendations
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Falha ao salvar a avaliação: ${result.exceptionOrNull()?.message ?: "Erro desconhecido"}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Erro ao salvar avaliação: ${e.message ?: "Erro desconhecido"}"
                    )
                }
            }
        }
    }
    
    /**
     * Determina o nível de risco com base na pontuação total
     */
    private fun determineRiskLevel(score: Int): RiskLevel {
        return when {
            score < 30 -> RiskLevel.LOW
            score < 70 -> RiskLevel.MEDIUM
            else -> RiskLevel.HIGH
        }
    }
    
    /**
     * Gera recomendações com base no nível de risco e pontuações por categoria
     */
    private fun generateRecommendations(riskLevel: RiskLevel, categorizedScores: Map<QuestionCategory, Int>): List<String> {
        val recommendations = mutableListOf<String>()
        
        // Recomendações gerais baseadas no nível de risco
        when (riskLevel) {
            RiskLevel.LOW -> {
                recommendations.add("Você está lidando bem com o estresse. Continue com práticas saudáveis.")
                recommendations.add("Mantenha a rotina de autocuidado e bem-estar.")
            }
            RiskLevel.MEDIUM -> {
                recommendations.add("Seu nível de estresse está moderado. Considere implementar práticas de gerenciamento de estresse.")
                recommendations.add("Considere reservar tempo para atividades que aumentem seu bem-estar.")
            }
            RiskLevel.HIGH -> {
                recommendations.add("Seu nível de estresse é alto. Recomendamos fortemente a implementação de práticas de redução de estresse.")
                recommendations.add("Considere buscar apoio profissional para ajudá-lo a gerenciar seu estresse.")
            }
        }
        
        // Recomendações específicas baseadas nas pontuações por categoria
        categorizedScores.forEach { (category, score) ->
            if (score > 7) {
                when (category) {
                    QuestionCategory.WORKLOAD -> {
                        recommendations.add("Sua carga de trabalho parece ser alta. Considere técnicas de gerenciamento de tempo.")
                    }
                    QuestionCategory.AUTONOMY -> {
                        recommendations.add("Busque mais controle sobre suas tarefas diárias e decisões de trabalho.")
                    }
                    QuestionCategory.RELATIONSHIPS -> {
                        recommendations.add("Trabalhe em melhorar seus relacionamentos no ambiente de trabalho.")
                    }
                    QuestionCategory.ROLE_CLARITY -> {
                        recommendations.add("Busque clarificar suas responsabilidades e papel em sua equipe.")
                    }
                    QuestionCategory.CHANGE -> {
                        recommendations.add("Desenvolva técnicas para lidar melhor com mudanças em seu ambiente.")
                    }
                    QuestionCategory.SUPPORT -> {
                        recommendations.add("Busque mais apoio social e profissional.")
                    }
                    QuestionCategory.WORK_LIFE_BALANCE -> {
                        recommendations.add("Trabalhe em melhorar seu equilíbrio entre vida pessoal e profissional.")
                    }
                }
            }
        }
        
        return recommendations
    }
    
    /**
     * Calcula as pontuações totais e por categoria
     */
    private fun calculateScores(assessment: Assessment): Pair<Int, Map<QuestionCategory, Int>> {
        val categorizedScores = mutableMapOf<QuestionCategory, Int>()
        var totalScore = 0
        
        assessment.questions.forEach { question ->
            val answer = question.answer
            if (answer is Int) {
                totalScore += answer
                
                // Adiciona à pontuação da categoria
                val currentCategoryScore = categorizedScores.getOrDefault(question.category, 0)
                categorizedScores[question.category] = currentCategoryScore + answer
            }
        }
        
        return Pair(totalScore, categorizedScores)
    }
    
    /**
     * Reinicia a avaliação
     */
    fun restartAssessment() {
        initializeAssessment()
    }
} 