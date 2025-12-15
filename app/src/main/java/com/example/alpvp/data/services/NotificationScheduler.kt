package com.example.alpvp.data.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

class NotificationScheduler(private val context: Context) {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    companion object {
        const val BREAKFAST_CODE = 1001
        const val LUNCH_CODE = 1002
        const val DINNER_CODE = 1003
        const val SNACK_CODE = 1004
    }
    
    fun scheduleNotifications(
        breakfastTime: String?,
        lunchTime: String?,
        dinnerTime: String?,
        snackTime: String?
    ) {
        cancelAll()
        
        breakfastTime?.let { schedule(it, "Breakfast Time! 🌅", BREAKFAST_CODE) }
        lunchTime?.let { schedule(it, "Lunch Time! 🍱", LUNCH_CODE) }
        dinnerTime?.let { schedule(it, "Dinner Time! 🍽️", DINNER_CODE) }
        snackTime?.let { schedule(it, "Snack Time! 🍎", SNACK_CODE) }
    }
    
    private fun schedule(time: String, title: String, code: Int) {
        val (hour, minute) = time.split(":").map { it.toInt() }
        
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            
            // If time passed, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("code", code)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context, code, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Use setRepeating - simple daily repeat
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        
        Log.d("Scheduler", "✅ Set $title for $time daily")
    }
    
    fun cancelAll() {
        listOf(BREAKFAST_CODE, LUNCH_CODE, DINNER_CODE, SNACK_CODE).forEach { code ->
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, code, intent, PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}
