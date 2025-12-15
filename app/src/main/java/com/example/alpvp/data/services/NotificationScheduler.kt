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
        cancelAllNotifications()
        
        breakfastTime?.let { 
            scheduleNotification(it, "Breakfast Time! 🌅", "Log your breakfast", BREAKFAST_CODE) 
        }
        lunchTime?.let { 
            scheduleNotification(it, "Lunch Time! 🍱", "Log your lunch", LUNCH_CODE) 
        }
        dinnerTime?.let { 
            scheduleNotification(it, "Dinner Time! 🍽️", "Log your dinner", DINNER_CODE) 
        }
        snackTime?.let { 
            scheduleNotification(it, "Snack Time! 🍎", "Log your snack", SNACK_CODE) 
        }
    }
    
    private fun scheduleNotification(time: String, title: String, message: String, requestCode: Int) {
        try {
            val parts = time.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()
            
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }
            
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("title", title)
                putExtra("message", message)
                putExtra("requestCode", requestCode)
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
            
            Log.d("NotificationScheduler", "Scheduled $title for $time (${calendar.time})")
        } catch (e: Exception) {
            Log.e("NotificationScheduler", "Error scheduling notification", e)
        }
    }
    
    fun cancelAllNotifications() {
        listOf(BREAKFAST_CODE, LUNCH_CODE, DINNER_CODE, SNACK_CODE).forEach { code ->
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                code,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
        Log.d("NotificationScheduler", "Cancelled all notifications")
    }
}
