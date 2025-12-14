package com.example.alpvp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alpvp.data.dto.DashboardData
import com.example.alpvp.data.dto.UserProfileData
import com.example.alpvp.data.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val loading: Boolean = false,
    val userProfile: UserProfileData? = null,
    val dashboardData: DashboardData? = null,
    val error: String? = null
)

class DashboardViewModel(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun loadDashboardData(token: String) {
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val profileResult = dashboardRepository.getUserProfile(token)
                val dashboardResult = dashboardRepository.getDashboardData(token)
                
                val profile = profileResult.getOrNull()
                val dashboard = dashboardResult.getOrNull()
                
                if (profile != null && dashboard != null) {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        userProfile = profile,
                        dashboardData = dashboard,
                        error = null
                    )
                } else {
                    val errorMessage = profileResult.exceptionOrNull()?.message 
                        ?: dashboardResult.exceptionOrNull()?.message 
                        ?: "Failed to load data"
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = errorMessage
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
