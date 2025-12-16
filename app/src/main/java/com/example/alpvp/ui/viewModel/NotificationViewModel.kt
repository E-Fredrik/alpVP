package com.example.alpvp.ui.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alpvp.data.services.AppService
import com.example.alpvp.data.dto.NotificationSettings
import com.example.alpvp.notification.NotificationScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotificationUiState(
    val loading: Boolean = false,
    val settings: NotificationSettings? = null,
    val error: String? = null,
    val successMessage: String? = null
)

/**
 * NotificationViewModel
 * Manages notification settings and scheduling logic
 * Follows MVVM pattern - UI interacts with this ViewModel, not directly with scheduler
 */
class NotificationViewModel(
    private val appService: AppService,
    private val context: Context
) : ViewModel() {

    private val scheduler = NotificationScheduler(context)

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    /**
     * Load current notification settings from the server
     */
    fun loadSettings() {
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val response = appService.getNotificationSettings()
                if (response.isSuccessful && response.body()?.success == true) {
                    val settings = response.body()?.data
                    if (settings != null) {
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            settings = settings,
                            error = null
                        )
                        
                        // Sync scheduler with loaded settings
                        if (settings.notificationEnabled) {
                            scheduler.scheduleNotifications(
                                breakfastTime = settings.breakfastTime,
                                lunchTime = settings.lunchTime,
                                dinnerTime = settings.dinnerTime,
                                snackTime = settings.snackTime
                            )
                        } else {
                            scheduler.cancelAll()
                        }
                        
                        Log.d("NotificationViewModel", "Settings loaded: enabled=${settings.notificationEnabled}")
                    } else {
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            error = "Failed to load settings: empty response"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = "Failed to load settings: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Failed to load settings", e)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Failed to load notification settings"
                )
            }
        }
    }

    /**
     * Update notification settings on the server and reschedule notifications
     */
    fun updateSettings(settings: NotificationSettings) {
        _uiState.value = _uiState.value.copy(loading = true, error = null, successMessage = null)
        viewModelScope.launch {
            try {
                val response = appService.updateNotificationSettings(settings)
                if (response.isSuccessful && response.body()?.success == true) {
                    val updated = response.body()?.data ?: settings
                    
                    // Schedule or cancel notifications based on settings
                    if (updated.notificationEnabled) {
                        scheduler.scheduleNotifications(
                            breakfastTime = updated.breakfastTime,
                            lunchTime = updated.lunchTime,
                            dinnerTime = updated.dinnerTime,
                            snackTime = updated.snackTime
                        )
                        Log.d("NotificationViewModel", "Notifications scheduled")
                    } else {
                        scheduler.cancelAll()
                        Log.d("NotificationViewModel", "Notifications cancelled")
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        settings = updated,
                        successMessage = "Settings updated successfully",
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = "Failed to save settings: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Failed to update settings", e)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Failed to update notification settings"
                )
            }
        }
    }

    /**
     * Toggle notifications on/off
     */
    fun toggleNotifications(enabled: Boolean) {
        val currentSettings = _uiState.value.settings ?: return
        updateSettings(currentSettings.copy(notificationEnabled = enabled))
    }

    /**
     * Update meal time for a specific meal type
     */
    fun updateMealTime(mealType: String, time: String) {
        val currentSettings = _uiState.value.settings ?: return
        
        val updatedSettings = when (mealType) {
            "breakfast" -> currentSettings.copy(breakfastTime = time)
            "lunch" -> currentSettings.copy(lunchTime = time)
            "dinner" -> currentSettings.copy(dinnerTime = time)
            "snack" -> currentSettings.copy(snackTime = time)
            else -> currentSettings
        }
        
        updateSettings(updatedSettings)
    }

    /**
     * Clear success message (e.g., after showing to user)
     */
    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    /**
     * Clear error message (e.g., after showing to user)
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
