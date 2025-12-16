package com.example.alpvp.ui.viewModel

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alpvp.data.container.AppContainer
import com.example.alpvp.notification.NotificationScheduler
import com.example.alpvp.data.dto.NotificationSettings
import com.example.alpvp.data.enums.VulnerabilityLevel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * NotificationViewModel - central place for notification-related logic.
 * Inlined smart-notification code: persists a FIFO queue of pending notifications
 * to SharedPreferences so pending items survive process death.
 */
class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val appContainer = AppContainer(application)
    private val scheduler = NotificationScheduler(application)

    // UI state for Notification Settings dialog
    data class NotificationUiState(
        val loading: Boolean = false,
        val settings: NotificationSettings? = null,
        val error: String? = null,
        val successMessage: String? = null
    )

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState

    // Persistent queue storage
    private val prefs = application.getSharedPreferences("notifications_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val PREF_KEY = "pending_notifications"

    private val queue: MutableList<PendingNotification> = mutableListOf()
    private var aarWatcherJob: Job? = null

    init {
        createNotificationChannelIfNeeded()
        loadQueueFromPrefs()
        // if there are queued items on startup, attempt to process them
        if (queue.isNotEmpty()) {
            startAarWatcherIfNeeded()
        }
        // Load settings into uiState at startup
        loadSettingsIntoUiState()
    }

    data class PendingNotification(
        val id: Int,
        val title: String,
        val message: String,
        val createdAt: Long = System.currentTimeMillis()
    )

    private fun loadQueueFromPrefs() {
        try {
            val json = prefs.getString(PREF_KEY, null) ?: return
            val type = object : TypeToken<List<PendingNotification>>() {}.type
            val list: List<PendingNotification> = gson.fromJson(json, type) ?: emptyList()
            queue.clear()
            queue.addAll(list)
            Log.d("NotificationVM", "Loaded ${queue.size} pending notifications from prefs")
        } catch (e: Exception) {
            Log.e("NotificationVM", "Failed to load pending notifications", e)
        }
    }

    private fun saveQueueToPrefs() {
        try {
            val json = gson.toJson(queue)
            prefs.edit().putString(PREF_KEY, json).apply()
        } catch (e: Exception) {
            Log.e("NotificationVM", "Failed to save pending notifications", e)
        }
    }

    /**
     * Load settings from backend and schedule notifications if enabled.
     */
    fun loadAndScheduleNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = appContainer.appService.getNotificationSettings()
                if (response.isSuccessful && response.body() != null) {
                    val settings = response.body()!!.data

                    // update UI state
                    _uiState.value = _uiState.value.copy(settings = settings, loading = false, error = null)

                    if (settings.notificationEnabled) {
                        scheduler.scheduleNotifications(
                            breakfastTime = settings.breakfastTime,
                            lunchTime = settings.lunchTime,
                            dinnerTime = settings.dinnerTime,
                            snackTime = settings.snackTime
                        )
                        Log.d("NotificationVM", "✅ Notifications scheduled from ViewModel")
                    } else {
                        scheduler.cancelAll()
                        clearQueue()
                        Log.d("NotificationVM", "Notifications disabled - cancelled")
                    }
                }
            } catch (e: Exception) {
                Log.e("NotificationVM", "Failed to load/schedule notifications", e)
                _uiState.value = _uiState.value.copy(error = "Failed to load settings: ${e.message}", loading = false)
            }
        }
    }

    private fun loadSettingsIntoUiState() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = _uiState.value.copy(loading = true, error = null)
                val response = appContainer.appService.getNotificationSettings()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(settings = response.body()!!.data, loading = false)
                } else {
                    _uiState.value = _uiState.value.copy(error = "Failed to load settings", loading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "${e.message}", loading = false)
            }
        }
    }

    /**
     * Public API: try to send a smart notification now; if user is in unsafe state,
     * enqueue the notification (persisted) and watch AAR until it's safe.
     */
    fun trySmartNotify(title: String = "Meal Time!", message: String = "Time to log your meal!") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Check runtime notification permission (Android 13+)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ActivityCompat.checkSelfPermission(
                            getApplication(),
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.w("NotificationVM", "Cannot send notification - POST_NOTIFICATIONS not granted")
                        return@launch
                    }
                }

                val aarState = appContainer.aarService.aarState
                val currentLevel = aarState.value.vulnerabilityLevel

                // Decide: only send if safe/moderate; block for high/critical
                if (currentLevel == VulnerabilityLevel.CRITICAL || currentLevel == VulnerabilityLevel.HIGH) {
                    Log.d("NotificationVM", "Blocked send - currentLevel=$currentLevel, enqueueing notification")
                    enqueueNotification(title, message)
                } else {
                    // Safe to send now
                    sendNotification(title, message)
                }
            } catch (e: Exception) {
                Log.e("NotificationVM", "Failed to attempt smart notification", e)
            }
        }
    }

    private fun enqueueNotification(title: String, message: String) {
        val id = Random.nextInt(1000, 9999)
        val pn = PendingNotification(id = id, title = title, message = message)
        synchronized(queue) {
            queue.add(pn)
            saveQueueToPrefs()
        }
        startAarWatcherIfNeeded()
    }

    private fun startAarWatcherIfNeeded() {
        if (aarWatcherJob != null) return
        aarWatcherJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val maxWatchMillis = 30 * 60 * 1000L
                val pollInterval = 5_000L
                var elapsed = 0L

                appContainer.aarService.aarState.collectLatest { state ->
                    // If no queued items, cancel watcher
                    val next = synchronized(queue) { queue.firstOrNull() }
                    if (next == null) {
                        cancelWatcherInternal()
                        return@collectLatest
                    }

                    val level = state.vulnerabilityLevel
                    if (level != VulnerabilityLevel.CRITICAL && level != VulnerabilityLevel.HIGH) {
                        // send the next queued item
                        processNextInQueue()
                        // reset elapsed after a successful send
                        elapsed = 0L
                    } else {
                        // still unsafe
                        elapsed += pollInterval
                        if (elapsed >= maxWatchMillis) {
                            Log.w("NotificationVM", "AAR watch timeout - will retry queued items later")
                            // schedule a retry for the remaining queue and cancel watcher
                            scheduleRetryForAll(delayMinutes = 5)
                            cancelWatcherInternal()
                            return@collectLatest
                        }
                        // small delay to avoid tight-looping
                        delay(pollInterval)
                    }
                }
            } catch (e: Exception) {
                Log.e("NotificationVM", "AAR watcher failed", e)
            }
        }
    }

    private fun cancelWatcherInternal() {
        aarWatcherJob?.cancel()
        aarWatcherJob = null
    }

    private fun processNextInQueue() {
        val pn: PendingNotification? = synchronized(queue) {
            if (queue.isEmpty()) return
            queue.removeAt(0)
        }
        if (pn != null) {
            saveQueueToPrefs()
            sendNotification(pn.title, pn.message)
        }
    }

    private fun scheduleRetryForAll(delayMinutes: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(delayMinutes * 60_000L)
            processQueueNowIfSafe()
        }
    }

    private fun processQueueNowIfSafe() {
        viewModelScope.launch(Dispatchers.IO) {
            val aarState = appContainer.aarService.aarState
            val currentLevel = aarState.value.vulnerabilityLevel
            if (currentLevel != VulnerabilityLevel.CRITICAL && currentLevel != VulnerabilityLevel.HIGH) {
                // keep sending until either queue empty or becomes unsafe
                while (true) {
                    val next = synchronized(queue) { queue.firstOrNull() } ?: break
                    processNextInQueue()
                    // small yield
                    delay(200)
                    val level = appContainer.aarService.aarState.value.vulnerabilityLevel
                    if (level == VulnerabilityLevel.CRITICAL || level == VulnerabilityLevel.HIGH) break
                }
            } else {
                // If still unsafe, schedule another retry
                scheduleRetryForAll(delayMinutes = 5)
            }
        }
    }

    private fun clearQueue() {
        synchronized(queue) {
            queue.clear()
            saveQueueToPrefs()
        }
    }

    private fun sendNotification(title: String, message: String) {
        try {
            val ctx = getApplication<Application>().applicationContext

            // Ensure channel exists
            createNotificationChannelIfNeeded()

            // Build notification
            val id = Random.nextInt(1000, 9999)
            val notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            if (ActivityCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            ) {
                NotificationManagerCompat.from(ctx).notify(id, notification)
                Log.d("NotificationVM", "Notification sent: $title (id=$id)")
            } else {
                Log.w("NotificationVM", "Notification permission missing - not sent")
            }
        } catch (e: Exception) {
            Log.e("NotificationVM", "Failed to send notification", e)
        }
    }

    private fun createNotificationChannelIfNeeded() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val ctx = getApplication<Application>().applicationContext
                val manager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val existing = manager.getNotificationChannel(CHANNEL_ID)
                if (existing == null) {
                    val channel = NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        description = "Reminders to log your meals"
                    }
                    manager.createNotificationChannel(channel)
                }
            }
        } catch (e: Exception) {
            Log.e("NotificationVM", "Failed to create notification channel", e)
        }
    }

    companion object {
        private const val CHANNEL_ID = "food_reminders_vm"
        private const val CHANNEL_NAME = "Food Logging Reminders (VM)"
    }

    // ------------------------------------------------------------------
    // UI-related helper APIs
    // ------------------------------------------------------------------
    fun updateSettings(settings: NotificationSettings) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            try {
                val response = appContainer.appService.updateNotificationSettings(settings)
                if (response.isSuccessful && response.body() != null) {
                    val returned = response.body()!!.data
                    _uiState.value = _uiState.value.copy(settings = returned, loading = false, successMessage = "Saved")

                    if (returned.notificationEnabled) {
                        scheduler.scheduleNotifications(
                            breakfastTime = returned.breakfastTime,
                            lunchTime = returned.lunchTime,
                            dinnerTime = returned.dinnerTime,
                            snackTime = returned.snackTime
                        )
                    } else {
                        scheduler.cancelAll()
                        clearQueue()
                    }
                } else {
                    _uiState.value = _uiState.value.copy(error = "Failed to save settings", loading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Error", loading = false)
            }
        }
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}
