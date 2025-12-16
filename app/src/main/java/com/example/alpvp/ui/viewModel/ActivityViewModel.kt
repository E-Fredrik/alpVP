package com.example.alpvp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alpvp.data.dto.ActivityLog
import com.example.alpvp.data.dto.ActivityLogRequest
import com.example.alpvp.data.repository.ActivityLogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ActivityUiState(
    val loading: Boolean = false,
    val activities: List<ActivityLog> = emptyList(),
    val currentActivity: ActivityLog? = null,
    val error: String? = null,
    val successMessage: String? = null
)

class ActivityViewModel(
    private val activityLogRepository: ActivityLogRepository,
    private val userId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityUiState())
    val uiState: StateFlow<ActivityUiState> = _uiState

    init {
        loadActivities()
    }

    fun loadActivities() {
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val activities = activityLogRepository.getUserActivityLogs(userId)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    activities = activities
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Failed to load activities"
                )
            }
        }
    }

    fun loadCurrentActivity() {
        viewModelScope.launch {
            try {
                val current = activityLogRepository.getCurrentActivity(userId)
                _uiState.value = _uiState.value.copy(currentActivity = current)
            } catch (e: Exception) {
                // Silent fail for current activity
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}
