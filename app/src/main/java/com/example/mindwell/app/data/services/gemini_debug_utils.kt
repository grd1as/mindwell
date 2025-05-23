package com.example.mindwell.app.data.services

import android.util.Log

/**
 * Utilidades para debug e monitoramento da integração com Gemini
 */
object GeminiDebugUtils {
    private const val TAG = "GeminiDebug"
    
    /**
     * Monitora e reporta a qualidade das respostas do Gemini
     */
    fun analyzeResponse(content: String, type: String): GeminiResponseQuality {
        Log.d(TAG, "🔍 Analisando resposta do Gemini ($type)")
        
        val quality = GeminiResponseQuality(
            type = type,
            contentLength = content.length,
            hasJson = content.contains("{") && content.contains("}"),
            hasExtraText = hasExtraTextBeforeJson(content),
            isValidJson = isValidJsonStructure(content),
            estimatedTokens = estimateTokenCount(content)
        )
        
        logQualityReport(quality)
        return quality
    }
    
    /**
     * Verifica se há texto explicativo antes do JSON
     */
    private fun hasExtraTextBeforeJson(content: String): Boolean {
        val firstBrace = content.indexOf("{")
        if (firstBrace <= 0) return false
        
        val textBefore = content.substring(0, firstBrace).trim()
        return textBefore.isNotEmpty()
    }
    
    /**
     * Validação básica da estrutura JSON
     */
    private fun isValidJsonStructure(content: String): Boolean {
        return try {
            val cleaned = extractJsonPart(content)
            val openBraces = cleaned.count { it == '{' }
            val closeBraces = cleaned.count { it == '}' }
            val openBrackets = cleaned.count { it == '[' }
            val closeBrackets = cleaned.count { it == ']' }
            
            openBraces == closeBraces && openBrackets == closeBrackets
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Extrai apenas a parte JSON da resposta
     */
    private fun extractJsonPart(content: String): String {
        val startIndex = content.indexOf("{")
        val endIndex = content.lastIndexOf("}") + 1
        
        return if (startIndex != -1 && endIndex > startIndex) {
            content.substring(startIndex, endIndex)
        } else {
            content
        }
    }
    
    /**
     * Estima quantidade de tokens usados (aproximação)
     */
    private fun estimateTokenCount(content: String): Int {
        // Aproximação: 1 token ≈ 4 caracteres em português
        return (content.length / 4.0).toInt()
    }
    
    /**
     * Gera relatório detalhado da qualidade
     */
    private fun logQualityReport(quality: GeminiResponseQuality) {
        val status = when {
            quality.isValidJson && !quality.hasExtraText -> "✅ EXCELENTE"
            quality.isValidJson -> "⚠️ BOA (com texto extra)"
            quality.hasJson -> "❌ PROBLEMÁTICA (JSON malformado)"
            else -> "💥 FALHA CRÍTICA (sem JSON)"
        }
        
        Log.d(TAG, """
            📊 RELATÓRIO DE QUALIDADE - ${quality.type}
            Status: $status
            Tamanho: ${quality.contentLength} caracteres
            Tokens estimados: ${quality.estimatedTokens}
            Tem JSON: ${quality.hasJson}
            JSON válido: ${quality.isValidJson}
            Texto extra: ${quality.hasExtraText}
            
        """.trimIndent())
    }
    
    /**
     * Cria prompt de exemplo para testes
     */
    fun createTestPrompt(): String {
        return """
            Teste rápido: gere um recurso simples no formato JSON:
            {
              "resources": [
                {
                  "title": "Respiração de 3 minutos",
                  "description": "Técnica simples de respiração profunda",
                  "category": "breathing",
                  "duration_minutes": 3,
                  "difficulty": "beginner",
                  "icon": "breathing",
                  "action_text": "Começar"
                }
              ],
              "personalized_message": "Teste de resposta JSON"
            }
        """.trimIndent()
    }
}

/**
 * Classe para análise de qualidade das respostas
 */
data class GeminiResponseQuality(
    val type: String,
    val contentLength: Int,
    val hasJson: Boolean,
    val hasExtraText: Boolean,
    val isValidJson: Boolean,
    val estimatedTokens: Int
) 