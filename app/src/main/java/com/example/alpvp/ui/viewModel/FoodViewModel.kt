package com.example.alpvp.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alpvp.data.Repository.FoodRepository
import com.example.alpvp.data.dto.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.times

data class FoodUiState(
    val loading: Boolean = false,
    val logs: List<FoodLogItem> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<FoodItem> = emptyList(),
    val showAddDialog: Boolean = false,
    val selectedFoods: List<SelectedFoodEntry> = emptyList(),
    val error: String? = null
)

data class SelectedFoodEntry(
    val foodId: Int?,
    val name: String,
    val calories: Int,
    val quantity: Int
)

class FoodViewModel(
    private val foodRepository: FoodRepository,
    private val token: String,
    private val userId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodUiState())
    val uiState: StateFlow<FoodUiState> = _uiState

    init {
        loadFoodLogs()
    }

    private fun loadFoodLogs() {
        _uiState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                val logs = foodRepository.getFoodLogByUser(token, userId)
                _uiState.update { it.copy(loading = false, logs = logs) }
            } catch (t: Throwable) {
                t.printStackTrace()
                Log.e("FoodViewModel", "loadFoodLogs error", t)
                _uiState.update { it.copy(loading = false, error = t.message ?: "Load failed") }
            }
        }
    }

    fun searchFood(query: String) {
        _uiState.update { it.copy(searchQuery = query, error = null) }
        val trimmed = query.trim()
        if (trimmed.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList()) }
            return
        }

        viewModelScope.launch {
            try {
                val results = foodRepository.getFoodByName(trimmed)
                _uiState.update { it.copy(searchResults = results) }
            } catch (t: Throwable) {
                t.printStackTrace()
                Log.e("FoodViewModel", "searchFood error", t)
                _uiState.update { it.copy(searchResults = emptyList()) }
            }
        }
    }

    fun addFoodLog(foodName: String, calories: Int, quantity: Int, foodId: Int?) {
        val current = _uiState.value.selectedFoods.toMutableList()
        current.add(SelectedFoodEntry(foodId, foodName, calories, quantity))
        _uiState.update { it.copy(selectedFoods = current) }
    }

    fun removeSelectedFood(index: Int) {
        val current = _uiState.value.selectedFoods.toMutableList()
        current.removeAt(index)
        _uiState.update { it.copy(selectedFoods = current) }
    }

    fun submitFoodLog() {
        val selected = _uiState.value.selectedFoods
        if (selected.isEmpty()) {
            _uiState.update { it.copy(error = "Please add at least one food") }
            return
        }

        _uiState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                val foodRequests = mutableListOf<FoodInLogRequest>()

                for (entry in selected) {
                    val foodId = if (entry.foodId == null) {
                        // Create new food first
                        val foodItem = FoodItem(
                            id = null,
                            name = entry.name,
                            calories = entry.calories
                        )
                        val createdFood = foodRepository.createFood(foodItem)

                        // Add null-safety check
                        if (createdFood.id == null) {
                            throw IllegalStateException("Created food has no ID for: ${entry.name}")
                        }
                        createdFood.id
                    } else {
                        entry.foodId
                    }

                    foodRequests.add(
                        FoodInLogRequest(
                            food_id = foodId,
                            quantity = entry.quantity,
                            calories = entry.calories * entry.quantity
                        )
                    )
                }

                val logRequest = FoodLogRequest(
                    foods = foodRequests,
                    latitude = 12.34,
                    longitude = 56.78,
                    timestamp = System.currentTimeMillis(),
                    user_id = userId
                )

                foodRepository.createFoodLog(token, logRequest)

                _uiState.update {
                    it.copy(
                        loading = false,
                        showAddDialog = false,
                        selectedFoods = emptyList(),
                        searchQuery = "",
                        searchResults = emptyList()
                    )
                }
                loadFoodLogs()
            } catch (t: Throwable) {
                t.printStackTrace()
                Log.e("FoodViewModel", "submitFoodLog error", t)
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = t.message ?: "Failed to submit log"
                    )
                }
            }
        }
    }







    fun toggleAddDialog(show: Boolean) {
        _uiState.update {
            it.copy(
                showAddDialog = show,
                selectedFoods = if (!show) emptyList() else it.selectedFoods,
                searchQuery = if (!show) "" else it.searchQuery,
                searchResults = if (!show) emptyList() else it.searchResults,
                error = null
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
