package com.example.alpvp.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alpvp.data.Repository.FoodRepository
import com.example.alpvp.data.Service.LocationService
import com.example.alpvp.data.dto.*
import com.example.alpvp.ui.model.FoodLogModel
import com.example.alpvp.ui.model.FoodInLogItemModel
import com.example.alpvp.ui.model.FoodModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FoodUiState(
    val loading: Boolean = false,
    val logs: List<FoodLogModel> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<FoodModel> = emptyList(),
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
    private val locationService: LocationService,
    private val token: String,
    private val userId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodUiState())
    val uiState: StateFlow<FoodUiState> = _uiState

    init {
        loadFoodLogs()
    }

    private fun mapToFoodLogModel(dto: FoodLogItem): FoodLogModel {
        Log.d("FoodViewModel", "Mapping FoodLogItem: log_id=${dto.log_id}, foodInLogs count=${dto.foodInLogs.size}")
        
        return FoodLogModel(
            logId = dto.log_id,
            userId = dto.user_id,
            timestamp = dto.timestamp,
            latitude = dto.latitude,
            longitude = dto.longitude,
            foodInLogs = dto.foodInLogs.map { foodInLog ->
                Log.d("FoodViewModel", "Mapping FoodInLog: id=${foodInLog.id}, name=${foodInLog.food.name}, calories=${foodInLog.calories}, quantity=${foodInLog.quantity}")
                
                FoodInLogItemModel(
                    id = foodInLog.id,
                    foodId = foodInLog.food_id,
                    name = foodInLog.food.name,
                    calories = foodInLog.calories,
                    quantity = foodInLog.quantity
                )
            }
        )
    }

    private fun mapToFoodModel(dto: FoodItem): FoodModel {
        return FoodModel(
            id = dto.id,
            name = dto.name ?: "",
            calories = dto.calories ?: 0
        )
    }

    private fun loadFoodLogs() {
        Log.d("FoodViewModel", "Loading food logs for user $userId")
        _uiState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                val logs = foodRepository.getFoodLogByUser(token, userId)
                Log.d("FoodViewModel", "Received ${logs.size} food logs from API")
                
                val mappedLogs = logs.map { 
                    Log.d("FoodViewModel", "Processing log: ${it.log_id}")
                    mapToFoodLogModel(it) 
                }
                
                Log.d("FoodViewModel", "Mapped ${mappedLogs.size} logs successfully")
                _uiState.update { it.copy(loading = false, logs = mappedLogs) }
            } catch (t: Throwable) {
                t.printStackTrace()
                Log.e("FoodViewModel", "loadFoodLogs error: ${t.message}", t)
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

        Log.d("FoodViewModel", "Searching for: $trimmed")
        viewModelScope.launch {
            try {
                val rawResults = foodRepository.getFoodByName(trimmed)
                Log.d("FoodViewModel", "Raw results from API: ${rawResults.size} items")

                val results = rawResults.mapNotNull { dto ->
                    Log.d("FoodViewModel", "Processing food: id=${dto.id}, name=${dto.name}, calories=${dto.calories}")
                    if (dto.name != null && dto.calories != null) {
                        mapToFoodModel(dto)
                    } else {
                        Log.w("FoodViewModel", "Skipping food with null name or calories: $dto")
                        null
                    }
                }

                Log.d("FoodViewModel", "Filtered results: ${results.size} items")
                _uiState.update { it.copy(searchResults = results) }
            } catch (t: Throwable) {
                t.printStackTrace()
                Log.e("FoodViewModel", "searchFood error: ${t.message}", t)
                _uiState.update { it.copy(searchResults = emptyList(), error = "Search failed: ${t.message}") }
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
                    var foodIdToUse = entry.foodId

                    // If foodId is null, create the custom food first
                    if (foodIdToUse == null) {
                        try {
                            Log.d("FoodViewModel", "Creating custom food: ${entry.name}")
                            val newFoodItem = FoodItem(
                                id = null,
                                name = entry.name,
                                calories = entry.calories
                            )
                            val createdFood = foodRepository.createFood(token, newFoodItem)
                            foodIdToUse = createdFood.id
                            Log.d("FoodViewModel", "Created custom food with ID: $foodIdToUse")
                        } catch (e: Exception) {
                            Log.e("FoodViewModel", "Failed to create custom food: ${entry.name}", e)
                            throw Exception("Failed to create custom food '${entry.name}': ${e.message}")
                        }
                    }


                    if (foodIdToUse == null) {
                        throw Exception("Failed to get food ID for '${entry.name}'")
                    }

                    foodRequests.add(
                        FoodInLogRequest(
                            food_id = foodIdToUse,  // Now guaranteed to be non-null
                            quantity = entry.quantity,
                            calories = entry.calories * entry.quantity
                        )
                    )
                }

                // Get real GPS coordinates
                val (latitude, longitude) = locationService.getLocationCoordinates()
                Log.d("FoodViewModel", "Using location: lat=$latitude, lon=$longitude")

                val logRequest = FoodLogRequest(
                    foods = foodRequests,
                    latitude = latitude,
                    longitude = longitude,
                    timestamp = System.currentTimeMillis(),
                    user_id = userId
                )

                Log.d("FoodViewModel", "Submitting food log to backend...")
                foodRepository.createFoodLog(token, logRequest)
                Log.d("FoodViewModel", "Food log submitted successfully!")

                _uiState.update {
                    it.copy(
                        loading = false,
                        showAddDialog = false,
                        selectedFoods = emptyList(),
                        searchQuery = "",
                        searchResults = emptyList()
                    )
                }
                
                // Reload the food logs to show the new entry
                Log.d("FoodViewModel", "Reloading food logs after successful submit")
                loadFoodLogs()
            } catch (t: Throwable) {
                t.printStackTrace()
                Log.e("FoodViewModel", "submitFoodLog error: ${t.message}", t)

                val errorMsg = when {
                    t.message?.contains("Cannot POST", ignoreCase = true) == true ->
                        "Backend endpoint error. Check if the endpoint is correct."
                    t.message?.contains("404") == true ->
                        "Endpoint not found (404). Backend may be down."
                    t.message?.contains("custom food", ignoreCase = true) == true ->
                        t.message!! // Show the specific custom food error
                    else -> "Failed to submit: ${t.message ?: "Unknown error"}"
                }

                _uiState.update {
                    it.copy(
                        loading = false,
                        error = errorMsg
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
                searchQuery = "",
                searchResults = emptyList(),
                error = null
            )
        }
    }
}
