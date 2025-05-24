package com.example.mindwell.app.data.services

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador de check-ins diários
 * Controla quando o usuário pode fazer check-in e gerencia o timer de 24 horas
 */
@Singleton
class CheckinManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private const val PREF_NAME = "mindwell_checkin_prefs"
        private const val KEY_LAST_CHECKIN_DATE = "last_checkin_date"
        private const val KEY_LAST_CHECKIN_HOUR = "last_checkin_hour"
        private const val KEY_LAST_CHECKIN_MINUTE = "last_checkin_minute"
        private const val KEY_CAN_CHECKIN = "can_checkin"
        
        // Para teste: 1 minuto (60000ms), para produção: 24 horas (86400000ms)
        private const val CHECKIN_COOLDOWN_MS = 60 * 1000L // 1 minuto para teste
        
        private const val TAG = "CheckinManager"
    }
    
    private val shared_prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val date_format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    /**
     * Verifica se o usuário já fez check-in hoje
     */
    fun has_checked_in_today(): Boolean {
        val today = LocalDate.now().toString()
        val last_checkin_date = shared_prefs.getString(KEY_LAST_CHECKIN_DATE, "")
        
        Log.d(TAG, "🔍 Verificando check-in: hoje=$today, último=$last_checkin_date")
        
        return today == last_checkin_date
    }
    
    /**
     * Verifica se o usuário pode fazer check-in agora
     * Considera apenas a data do último check-in
     */
    fun can_checkin_now(): Boolean {
        // Se já fez check-in hoje, não pode fazer novamente
        if (has_checked_in_today()) {
            Log.d(TAG, "❌ Não pode fazer check-in: já fez hoje")
            return false
        }
        
        // Se não fez hoje, pode fazer
        Log.d(TAG, "✅ Pode fazer check-in: ainda não fez hoje")
        return true
    }
    
    /**
     * Registra que o usuário fez check-in agora
     */
    fun register_checkin() {
        val now = LocalTime.now()
        val today = LocalDate.now().toString()
        
        shared_prefs.edit()
            .putString(KEY_LAST_CHECKIN_DATE, today)
            .putInt(KEY_LAST_CHECKIN_HOUR, now.hour)
            .putInt(KEY_LAST_CHECKIN_MINUTE, now.minute)
            .apply()
        
        Log.d(TAG, "✅ Check-in registrado para $today às ${String.format("%02d:%02d", now.hour, now.minute)}")
        
        stop_all_reminders()
    }
    
    /**
     * Para todos os tipos de lembretes após check-in
     */
    private fun stop_all_reminders() {
        try {
            // Parar sistema antigo de notificações
            val notification_service = NotificationService(context)
            notification_service.stop_daily_reminders()
            
            // Parar novo sistema de lembretes
            stopNewReminderSystem()
            
            Log.d(TAG, "🔕 Todos os lembretes pausados após check-in")
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Erro ao parar lembretes: ${e.message}")
        }
    }
    
    /**
     * Para o novo sistema de lembretes usando AlarmManager diretamente
     */
    private fun stopNewReminderSystem() {
        val alarm_manager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        
        // Cancelar primeiro lembrete (20s)
        val initial_intent = android.content.Intent(context, InitialReminderReceiver::class.java)
        val initial_pending = android.app.PendingIntent.getBroadcast(
            context,
            2001,
            initial_intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        alarm_manager.cancel(initial_pending)
        
        // Cancelar lembretes repetidos (5min)
        val repeat_intent = android.content.Intent(context, RepeatReminderReceiver::class.java)
        val repeat_pending = android.app.PendingIntent.getBroadcast(
            context,
            2002,
            repeat_intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        alarm_manager.cancel(repeat_pending)
        
        Log.d(TAG, "🚫 Novo sistema de lembretes parado")
    }
    
    /**
     * Obtém o tempo restante até poder fazer novo check-in
     */
    fun get_time_until_next_checkin(): Long {
        if (!has_checked_in_today()) {
            return 0L
        }
        
        // Pegar hora do último check-in
        val lastCheckInHour = shared_prefs.getInt(KEY_LAST_CHECKIN_HOUR, 0)
        val lastCheckInMinute = shared_prefs.getInt(KEY_LAST_CHECKIN_MINUTE, 0)
        
        // Calcular tempo até completar 24h desde o último check-in
        val hoursUntilNext = 24 - (LocalTime.now().hour - lastCheckInHour)
        val minutesUntilNext = if (LocalTime.now().minute >= lastCheckInMinute) {
            60 - (LocalTime.now().minute - lastCheckInMinute)
        } else {
            lastCheckInMinute - LocalTime.now().minute
        }
        
        return ((hoursUntilNext * 60L + minutesUntilNext) * 60L * 1000L)
    }
    
    /**
     * Formata o tempo restante em texto legível
     */
    fun get_formatted_time_until_next_checkin(): String {
        if (!has_checked_in_today()) {
            return "Disponível agora"
        }
        
        // Pegar hora do último check-in
        val lastCheckInHour = shared_prefs.getInt(KEY_LAST_CHECKIN_HOUR, 0)
        val lastCheckInMinute = shared_prefs.getInt(KEY_LAST_CHECKIN_MINUTE, 0)
        
        // Hora atual
        val now = LocalTime.now()
        
        // Calcular tempo até completar 24h desde o último check-in
        val hoursUntilNext = 24 - (now.hour - lastCheckInHour)
        val minutesUntilNext = if (now.minute >= lastCheckInMinute) {
            60 - (now.minute - lastCheckInMinute)
        } else {
            lastCheckInMinute - now.minute
        }
        
        Log.d(TAG, "⏰ Último check-in: $lastCheckInHour:$lastCheckInMinute, Agora: ${now.hour}:${now.minute}, Faltam: ${hoursUntilNext}h ${minutesUntilNext}m")
        
        return when {
            hoursUntilNext > 0 -> "${hoursUntilNext}h ${minutesUntilNext}m"
            minutesUntilNext > 0 -> "${minutesUntilNext}m"
            else -> "Menos de 1m"
        }
    }
    
    /**
     * Força reset do check-in (para desenvolvimento/teste)
     */
    fun reset_checkin_for_testing() {
        shared_prefs.edit()
            .remove(KEY_LAST_CHECKIN_DATE)
            .remove(KEY_LAST_CHECKIN_HOUR)
            .remove(KEY_LAST_CHECKIN_MINUTE)
            .remove(KEY_CAN_CHECKIN)
            .apply()
        
        Log.d(TAG, "🔄 Check-in resetado para teste")
    }
    
    /**
     * Agenda o retorno dos lembretes após o período de cooldown
     */
    private fun schedule_reminder_restart() {
        val alarm_manager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = android.content.Intent(context, CheckinCooldownReceiver::class.java)
        val pending_intent = android.app.PendingIntent.getBroadcast(
            context,
            1003,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        
        val restart_time = System.currentTimeMillis() + CHECKIN_COOLDOWN_MS
        
        try {
            alarm_manager.setExact(
                android.app.AlarmManager.RTC_WAKEUP,
                restart_time,
                pending_intent
            )
            
            Log.d(TAG, "⏰ Lembretes voltarão em ${CHECKIN_COOLDOWN_MS}ms")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao agendar retorno dos lembretes: ${e.message}", e)
        }
    }
}

/**
 * BroadcastReceiver que reativa os lembretes após o período de cooldown
 * Agora integrado com o novo ReminderService
 */
class CheckinCooldownReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: android.content.Intent) {
        Log.d("CheckinCooldownReceiver", "🔔 Reativando lembretes após cooldown")
        
        // Compatibilidade com sistema antigo
        val notification_service = NotificationService(context)
        notification_service.start_daily_reminders()
        
        // TODO: Integrar com ReminderService quando DI estiver disponível
        // val reminderService = // obter via DI
        // reminderService.start_reminder_system()
    }
} 