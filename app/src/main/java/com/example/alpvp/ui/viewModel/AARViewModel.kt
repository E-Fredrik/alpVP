package com.example.alpvp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alpvp.data.services.AARService
import com.example.alpvp.ui.model.AARState
import com.example.alpvp.ui.model.VulnerabilityLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * AAR ViewModel - Manages Activity and Attention Recognition state
 */
class AARViewModel(private val aarService: AARService) : ViewModel() {

    private val _aarState = MutableStateFlow(AARState())
    val aarState: StateFlow<AARState> = _aarState

    init {
        // Start monitoring when ViewModel is created
        startMonitoring()

        // Observe AAR service state
        viewModelScope.launch {
            aarService.aarState.collect { state ->
                _aarState.value = state
            }
        }
    }

    fun startMonitoring() {
        aarService.startMonitoring()
    }

    fun stopMonitoring() {
        aarService.stopMonitoring()
    }

    fun isVulnerable(): Boolean {
        return _aarState.value.vulnerabilityLevel in listOf(
            VulnerabilityLevel.HIGH,
            VulnerabilityLevel.CRITICAL
        )
    }

    fun shouldShowWarning(): Boolean {
        return _aarState.value.vulnerabilityLevel == VulnerabilityLevel.CRITICAL
    }

    override fun onCleared() {
        super.onCleared()
        stopMonitoring()
    }
}

