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
        // Don't reload if already loading
        if (_uiState.value.loading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)

            android.util.Log.d("DashboardViewModel", "Loading dashboard data with token: ${token.take(10)}...")

            try {
                val profileResult = dashboardRepository.getUserProfile(token)
                val dashboardResult = dashboardRepository.getDashboardData(token)
                
                val profile = profileResult.getOrNull()
                val dashboard = dashboardResult.getOrNull()

                android.util.Log.d("DashboardViewModel", "Profile loaded: ${profile != null}")
                android.util.Log.d("DashboardViewModel", "Dashboard loaded: ${dashboard != null}")

                if (profile != null && dashboard != null) {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        userProfile = profile,
                        dashboardData = dashboard,
                        error = null
                    )
                    android.util.Log.d("DashboardViewModel", "Data loaded successfully")
                } else {
                    val errorMessage = profileResult.exceptionOrNull()?.message
                        ?: dashboardResult.exceptionOrNull()?.message 
                        ?: "Failed to load data"
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = errorMessage
                    )
                    android.util.Log.e("DashboardViewModel", "Error: $errorMessage")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Unknown error occurred"
                )
                android.util.Log.e("DashboardViewModel", "Exception loading data", e)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
