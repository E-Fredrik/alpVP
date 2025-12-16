package com.example.alpvp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alpvp.data.dto.Place
import com.example.alpvp.data.repository.PlaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PlaceUiState(
    val loading: Boolean = false,
    val places: List<Place> = emptyList(),
    val nearbyPlaces: List<Place> = emptyList(),
    val selectedCategory: String? = null,
    val error: String? = null
)

class PlaceViewModel(
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaceUiState())
    val uiState: StateFlow<PlaceUiState> = _uiState

    init {
        loadAllPlaces()
    }

    fun loadAllPlaces() {
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val places = placeRepository.getAllPlaces()
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    places = places
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Failed to load places"
                )
            }
        }
    }

    fun loadNearbyPlaces(latitude: Double, longitude: Double, radiusKm: Double = 1.0) {
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val places = placeRepository.getNearbyPlaces(latitude, longitude, radiusKm)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    nearbyPlaces = places
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Failed to load nearby places"
                )
            }
        }
    }

    fun filterByCategory(category: String?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
