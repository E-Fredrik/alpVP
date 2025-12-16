package com.example.alpvp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alpvp.data.Service.AARService
import com.example.alpvp.ui.model.AARState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AARViewModel(private val aarService: AARService) : ViewModel() {

    private val _aarState = MutableStateFlow(AARState())
    val aarState: StateFlow<AARState> = _aarState

    init {
        // Observe AAR service state
        viewModelScope.launch {
            aarService.aarState.collect { state ->
                _aarState.value = state
            }
        }
    }

    fun startMonitoring(userId: Int) {
        aarService.startMonitoring(userId)
    }

    fun stopMonitoring() {
        aarService.stopMonitoring()
    }

    override fun onCleared() {
        super.onCleared()
        stopMonitoring()
    }
}

