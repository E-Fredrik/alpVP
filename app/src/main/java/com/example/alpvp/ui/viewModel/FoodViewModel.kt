package com.example.alpvp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alpvp.data.repository.FoodRepository
import com.example.alpvp.data.dto.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FoodUiState(
    val loading: Boolean = false,
    val logs: List<FoodLogItem> = emptyList(),
    val searchQuery: String = "",
    val selectedMeal: String = "Lunch",
    val showAddDialog: Boolean = false,
    val error: String? = null,
    val locationText: String = "Fetching location..."
)

class FoodViewModel(
    private val foodRepository: FoodRepository,
    private val token: String
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
                // Sample data for testing - replace with actual API call when ready
                val now = System.currentTimeMillis()
                val pizza = Food(name = "Margherita Pizza", calories = 480, food_id = 1)
                val pepper = Food(name = "Pepperoni Pizza", calories = 540, food_id = 2)
                val salad = Food(name = "Caesar Salad", calories = 200, food_id = 3)

                val l1 = FoodInLog(calories = 480, food = pizza, food_id = 1, id = 1, log_id = 101, quantity = 2)
                val l2 = FoodInLog(calories = 540, food = pepper, food_id = 2, id = 2, log_id = 102, quantity = 2)
                val l3 = FoodInLog(calories = 200, food = salad, food_id = 3, id = 3, log_id = 103, quantity = 1)

                val logs = listOf(
                    FoodLogItem(foodInLogs = listOf(l1), latitude = 0.0, log_id = 101, longitude = 0.0, timestamp = now, user_id = 1),
                    FoodLogItem(foodInLogs = listOf(l2), latitude = 0.0, log_id = 102, longitude = 0.0, timestamp = now - 86400000L, user_id = 2),
                    FoodLogItem(foodInLogs = listOf(l3), latitude = 0.0, log_id = 103, longitude = 0.0, timestamp = now - 2 * 86400000L, user_id = 3),
                )

                _uiState.update { it.copy(loading = false, logs = logs, locationText = "You're at Joe's Pizza") }
            } catch (t: Throwable) {
                _uiState.update { it.copy(loading = false, error = t.message ?: "Failed loading logs") }
            }
        }
    }

    fun setSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectMeal(meal: String) {
        _uiState.update { it.copy(selectedMeal = meal) }
    }

    fun toggleAddDialog(show: Boolean) {
        _uiState.update { it.copy(showAddDialog = show) }
    }

    fun addManualLog(foodName: String, calories: Int, quantity: Int) {
        _uiState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                // Step 1: Create the food item
                val newFood = FoodItem(
                    name = foodName,
                    calories = calories,
                    id = 0 // server assigns ID
                )
                val createdFood = foodRepository.createFood(newFood)

                // Step 2: Create the food log
                val logRequest = FoodLogRequest(
                    foods = listOf(Food(name = createdFood.name, calories = createdFood.calories, food_id = createdFood.id)),
                    latitude = 0.0,
                    longitude = 0.0,
                    timestamp = System.currentTimeMillis(),
                    user_id = 0 // backend should extract from token
                )
                val createdLog = foodRepository.createFoodLog(token, logRequest)

                // Step 3: Create food-in-log entry
                val foodInLogRequest = FoodInLogRequest(
                    calories = calories,
                    food_id = createdFood.id,
                    log_id = createdLog.log_id,
                    quantity = quantity
                )
                foodRepository.createFoodInLog(token, foodInLogRequest)

                // Refresh logs from server or prepend locally
                _uiState.update { state ->
                    state.copy(
                        loading = false,
                        logs = listOf(createdLog) + state.logs,
                        showAddDialog = false
                    )
                }
            } catch (t: Throwable) {
                _uiState.update { it.copy(loading = false, error = t.message ?: "Failed to add log") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
