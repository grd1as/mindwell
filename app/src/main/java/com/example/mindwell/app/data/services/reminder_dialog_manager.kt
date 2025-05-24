package com.example.mindwell.app.data.services

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador global para controlar quando mostrar o dialog de lembrete
 * Este manager é usado por todas as telas para mostrar o dialog quando necessário
 */
@Singleton
class ReminderDialogManager @Inject constructor() {
    
    companion object {
        private const val TAG = "ReminderDialogManager"
    }
    
    private val _should_show_reminder = MutableStateFlow(false)
    val should_show_reminder: StateFlow<Boolean> = _should_show_reminder.asStateFlow()
    
    /**
     * Mostra o dialog de lembrete
     */
    fun show_reminder() {
        Log.d(TAG, "🎯 Solicitando exibição do dialog de lembrete")
        _should_show_reminder.value = true
    }
    
    /**
     * Esconde o dialog de lembrete
     */
    fun hide_reminder() {
        Log.d(TAG, "❌ Escondendo dialog de lembrete")
        _should_show_reminder.value = false
    }
    
    /**
     * Verifica se o dialog deve ser mostrado
     */
    fun is_showing(): Boolean {
        return _should_show_reminder.value
    }
    
    /**
     * Agenda lembrete para mais tarde (5 minutos)
     */
    fun snooze_reminder() {
        Log.d(TAG, "⏰ Lembrete adiado para 5 minutos")
        hide_reminder()
        // Aqui poderíamos agendar um novo alarme para 5 minutos, 
        // mas por simplicidade vamos deixar o sistema normal continuar
    }
} 