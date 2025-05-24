package com.example.mindwell.app.data.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Serviço de lembretes com novas regras de timing:
 * 1. 20 segundos após app iniciar (se reminder ativado)
 * 2. A cada 5 minutos até formulário ser preenchido
 * 3. Bloquear por 24 horas após preenchimento
 */
@Singleton
class ReminderService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val reminder_dialog_manager: ReminderDialogManager,
    private val checkin_manager: CheckinManager
) {
    companion object {
        private const val TAG = "ReminderService"
        private const val PREFS_NAME = "reminder_service_prefs"
        private const val KEY_REMINDER_ENABLED = "reminder_enabled"
        private const val KEY_APP_START_TIME = "app_start_time"
        
        // Timings em millisegundos
        private const val INITIAL_DELAY_MS = 20 * 1000L // 20 segundos
        private const val REPEAT_INTERVAL_MS = 5 * 60 * 1000L // 5 minutos
        
        // Request codes para alarmes
        private const val REQUEST_CODE_INITIAL = 2001
        private const val REQUEST_CODE_REPEAT = 2002
    }
    
    private val alarm_manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val shared_prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Inicia o sistema de lembretes quando app abre
     * Agenda primeiro lembrete para 20 segundos
     */
    fun start_reminder_system() {
        if (!is_reminder_enabled()) {
            Log.d(TAG, "⏸️ Lembretes desabilitados pelo usuário")
            return
        }
        
        // Verificar se usuário está logado antes de iniciar lembretes
        if (!is_user_logged_in()) {
            Log.d(TAG, "👤 Usuário não logado - lembretes não iniciados")
            return
        }
        
        // Registra horário de início do app
        val app_start_time = System.currentTimeMillis()
        shared_prefs.edit()
            .putLong(KEY_APP_START_TIME, app_start_time)
            .apply()
        
        Log.d(TAG, "🚀 Iniciando sistema de lembretes - primeiro em 20s")
        
        // Verificar se já fez check-in hoje
        if (checkin_manager.has_checked_in_today()) {
            Log.d(TAG, "✅ Já fez check-in hoje - sistema pausado")
            return
        }
        
        // Agendar primeiro lembrete em 20 segundos
        schedule_initial_reminder()
    }
    
    /**
     * Para todo o sistema de lembretes
     */
    fun stop_reminder_system() {
        Log.d(TAG, "🛑 Parando sistema de lembretes")
        
        cancel_all_reminders()
    }
    
    /**
     * Ativa/desativa lembretes nas configurações
     */
    fun set_reminder_enabled(enabled: Boolean) {
        shared_prefs.edit()
            .putBoolean(KEY_REMINDER_ENABLED, enabled)
            .apply()
        
        Log.d(TAG, "⚙️ Lembretes ${if (enabled) "ativados" else "desativados"}")
        
        if (enabled) {
            start_reminder_system()
        } else {
            stop_reminder_system()
        }
    }
    
    /**
     * Verifica se lembretes estão habilitados
     */
    fun is_reminder_enabled(): Boolean {
        return shared_prefs.getBoolean(KEY_REMINDER_ENABLED, false)
    }
    
    /**
     * Verifica se o usuário está logado checando SharedPreferences de auth
     */
    private fun is_user_logged_in(): Boolean {
        val auth_prefs = context.getSharedPreferences("mindwell_auth_prefs", Context.MODE_PRIVATE)
        val access_token = auth_prefs.getString("access_token", null)
        val is_logged_in = !access_token.isNullOrBlank()
        
        Log.d(TAG, "🔐 Status de login: ${if (is_logged_in) "logado" else "não logado"}")
        return is_logged_in
    }
    
    /**
     * Agenda o primeiro lembrete (20 segundos após app iniciar)
     */
    private fun schedule_initial_reminder() {
        val intent = Intent(context, InitialReminderReceiver::class.java)
        val pending_intent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_INITIAL,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val trigger_time = System.currentTimeMillis() + INITIAL_DELAY_MS
        
        try {
            alarm_manager.setExact(
                AlarmManager.RTC_WAKEUP,
                trigger_time,
                pending_intent
            )
            
            Log.d(TAG, "⏰ Primeiro lembrete agendado para ${INITIAL_DELAY_MS}ms")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao agendar primeiro lembrete: ${e.message}", e)
        }
    }
    
    /**
     * Agenda lembretes repetidos a cada 5 minutos
     */
    fun schedule_repeat_reminders() {
        val intent = Intent(context, RepeatReminderReceiver::class.java)
        val pending_intent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_REPEAT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val trigger_time = System.currentTimeMillis() + REPEAT_INTERVAL_MS
        
        try {
            alarm_manager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                trigger_time,
                REPEAT_INTERVAL_MS,
                pending_intent
            )
            
            Log.d(TAG, "🔁 Lembretes repetidos agendados a cada ${REPEAT_INTERVAL_MS}ms")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao agendar lembretes repetidos: ${e.message}", e)
        }
    }
    
    /**
     * Cancela todos os alarmes de lembrete
     */
    private fun cancel_all_reminders() {
        // Cancelar primeiro lembrete
        val initial_intent = Intent(context, InitialReminderReceiver::class.java)
        val initial_pending = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_INITIAL,
            initial_intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarm_manager.cancel(initial_pending)
        
        // Cancelar lembretes repetidos
        val repeat_intent = Intent(context, RepeatReminderReceiver::class.java)
        val repeat_pending = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_REPEAT,
            repeat_intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarm_manager.cancel(repeat_pending)
        
        Log.d(TAG, "🚫 Todos os lembretes cancelados")
    }
    
    /**
     * Chamado quando usuário faz check-in
     * Para lembretes e agenda retorno para próximo dia
     */
    fun on_checkin_completed() {
        Log.d(TAG, "✅ Check-in completado - pausando lembretes por 24h")
        
        cancel_all_reminders()
        
        // Agendar retorno dos lembretes para amanhã
        schedule_next_day_restart()
    }
    
    /**
     * Agenda retorno dos lembretes para próximo dia permitido
     */
    private fun schedule_next_day_restart() {
        val next_checkin_time = checkin_manager.get_time_until_next_checkin()
        
        if (next_checkin_time > 0) {
            val intent = Intent(context, RestartReminderReceiver::class.java)
            val pending_intent = PendingIntent.getBroadcast(
                context,
                2003,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val restart_time = System.currentTimeMillis() + next_checkin_time
            
            try {
                alarm_manager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    restart_time,
                    pending_intent
                )
                
                Log.d(TAG, "📅 Lembretes retornarão em ${next_checkin_time}ms")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao agendar retorno: ${e.message}", e)
            }
        }
    }
}

/**
 * Receiver para o primeiro lembrete (20 segundos após app iniciar)
 */
class InitialReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("InitialReminderReceiver", "⏰ Primeiro lembrete (20s) ativado")
        
        val checkin_manager = CheckinManager(context)
        
        // Verificar se ainda não fez check-in
        if (!checkin_manager.has_checked_in_today()) {
            // Mostrar dialog usando uma abordagem que funciona sem DI
            ReminderDialogHelper.show_reminder_via_broadcast(context)
            Log.d("InitialReminderReceiver", "🎯 Dialog de lembrete solicitado")
            
            // Agendar lembretes repetidos usando AlarmManager diretamente
            scheduleRepeatReminders(context)
        } else {
            Log.d("InitialReminderReceiver", "✅ Já fez check-in - ignorando lembrete")
        }
    }
    
    private fun scheduleRepeatReminders(context: Context) {
        val alarm_manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, RepeatReminderReceiver::class.java)
        val pending_intent = PendingIntent.getBroadcast(
            context,
            2002,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val trigger_time = System.currentTimeMillis() + (5 * 60 * 1000L) // 5 minutos
        
        try {
            alarm_manager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                trigger_time,
                5 * 60 * 1000L, // 5 minutos
                pending_intent
            )
            Log.d("InitialReminderReceiver", "🔁 Lembretes repetidos agendados")
        } catch (e: Exception) {
            Log.e("InitialReminderReceiver", "❌ Erro ao agendar repetidos: ${e.message}")
        }
    }
}

/**
 * Receiver para lembretes repetidos (a cada 5 minutos)
 */
class RepeatReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("RepeatReminderReceiver", "🔁 Lembrete repetido (5min) ativado")
        
        val checkin_manager = CheckinManager(context)
        
        // Verificar se ainda não fez check-in
        if (!checkin_manager.has_checked_in_today()) {
            // Mostrar dialog
            ReminderDialogHelper.show_reminder_via_broadcast(context)
            Log.d("RepeatReminderReceiver", "🎯 Dialog de lembrete repetido solicitado")
        } else {
            Log.d("RepeatReminderReceiver", "✅ Check-in feito - parando lembretes repetidos")
            
            // Parar lembretes repetidos
            val alarm_manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val repeat_intent = Intent(context, RepeatReminderReceiver::class.java)
            val pending_intent = PendingIntent.getBroadcast(
                context,
                2002,
                repeat_intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarm_manager.cancel(pending_intent)
        }
    }
}

/**
 * Receiver para reiniciar lembretes após período de cooldown
 */
class RestartReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("RestartReminderReceiver", "📅 Reiniciando lembretes após cooldown")
        
        // Verificar se lembretes estão habilitados
        val shared_prefs = context.getSharedPreferences("reminder_service_prefs", Context.MODE_PRIVATE)
        val reminder_enabled = shared_prefs.getBoolean("reminder_enabled", false)
        
        if (reminder_enabled) {
            // Criar uma instância simples do ReminderService para reiniciar
            val checkin_manager = CheckinManager(context)
            if (!checkin_manager.has_checked_in_today()) {
                // Agendar primeiro lembrete novamente
                val alarm_manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val restart_intent = Intent(context, InitialReminderReceiver::class.java)
                val pending_intent = PendingIntent.getBroadcast(
                    context,
                    2001,
                    restart_intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                
                val trigger_time = System.currentTimeMillis() + (20 * 1000L) // 20 segundos
                
                try {
                    alarm_manager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        trigger_time,
                        pending_intent
                    )
                    Log.d("RestartReminderReceiver", "⏰ Sistema reiniciado")
                } catch (e: Exception) {
                    Log.e("RestartReminderReceiver", "❌ Erro ao reiniciar: ${e.message}")
                }
            }
        }
    }
}

/**
 * Helper para mostrar dialog sem depender de DI
 */
object ReminderDialogHelper {
    fun show_reminder_via_broadcast(context: Context) {
        // Enviar broadcast para notificar que deve mostrar dialog
        val broadcast_intent = Intent("com.example.mindwell.SHOW_REMINDER_DIALOG")
        context.sendBroadcast(broadcast_intent)
    }
} 