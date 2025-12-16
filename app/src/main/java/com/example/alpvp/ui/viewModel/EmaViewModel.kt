package com.example.alpvp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alpvp.data.dto.EmaLog
import com.example.alpvp.data.dto.EmaLogRequest
import com.example.alpvp.data.repository.EmaLogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class EmaUiState(
    val loading: Boolean = false,
    val emaLogs: List<EmaLog> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null,
    val showEmaPrompt: Boolean = false
)

class EmaViewModel(
    private val emaLogRepository: EmaLogRepository,
    private val userId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmaUiState())
    val uiState: StateFlow<EmaUiState> = _uiState

    init {
        loadEmaLogs()
    }

    fun loadEmaLogs() {
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val logs = emaLogRepository.getUserEmaLogs(userId)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    emaLogs = logs
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Failed to load EMA logs"
                )
            }
        }
    }

    fun createEmaLog(
        moodScore: Int,
        context: String?,
        latitude: Double?,
        longitude: Double?
    ) {
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val request = EmaLogRequest(
                    user_id = userId,
                    moodScore = moodScore,
                    context = context,
                    timestamp = System.currentTimeMillis(),
                    latitude = latitude,
                    longitude = longitude,
                    geofenceRadius = 50
                )
                
                emaLogRepository.createEmaLog(request)
                
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    successMessage = "Mood logged successfully!",
                    showEmaPrompt = false
                )
                
                loadEmaLogs()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Failed to create EMA log"
                )
            }
        }
    }

    fun showEmaPrompt() {
        _uiState.value = _uiState.value.copy(showEmaPrompt = true)
    }

    fun hideEmaPrompt() {
        _uiState.value = _uiState.value.copy(showEmaPrompt = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}
