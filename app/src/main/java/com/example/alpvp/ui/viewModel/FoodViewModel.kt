package com.example.alpvp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alpvp.data.Repository.FoodRepository
import com.example.alpvp.data.dto.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log

data class FoodUiState(
    val loading: Boolean = false,
    val logs: List<FoodLogItem> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<FoodItem> = emptyList(),
    val showAddDialog: Boolean = false,
    val showManualEntry: Boolean = false,
    val selectedFood: FoodItem? = null,
    val error: String? = null
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
                _uiState.update { it.copy(searchResults = emptyList(), error = t.message) }
            }
        }
    }

    fun selectFood(food: FoodItem) {
        _uiState.update {
            it.copy(
                selectedFood = food,
                searchQuery = food.name ?: "",
                searchResults = emptyList()
            )
        }
    }

    fun showManualEntry() {
        _uiState.update { it.copy(showManualEntry = true, selectedFood = null) }
    }

    fun toggleAddDialog(show: Boolean) {
        _uiState.update {
            it.copy(
                showAddDialog = show,
                searchQuery = "",
                searchResults = emptyList(),
                selectedFood = null,
                showManualEntry = false,
                error = null
            )
        }
        if (!show) {
            loadFoodLogs()
        }
    }

    fun addFoodLog(foodName: String, calories: Int, quantity: Int) {
        _uiState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                val food = Food(
                    calories = calories,
                    food_id = 0,
                    name = foodName
                )

                val request = FoodLogRequest(
                    foods = listOf(food),
                    latitude = 0.0,
                    longitude = 0.0,
                    timestamp = System.currentTimeMillis(),
                    user_id = userId
                )

                foodRepository.createFoodLog(token, request)

                _uiState.update { it.copy(loading = false, showAddDialog = false) }
                loadFoodLogs()
            } catch (t: Throwable) {
                t.printStackTrace()
                Log.e("FoodViewModel", "addFoodLog error", t)
                _uiState.update { it.copy(loading = false, error = t.message ?: "Add failed") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
