package com.example.alpvp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alpvp.data.dto.FoodItem
import com.example.alpvp.data.dto.FoodLogItem
import com.example.alpvp.ui.viewModel.FoodViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(
    foodViewModel: FoodViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by foodViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Logs", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4F8BFF),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { foodViewModel.toggleAddDialog(true) },
                containerColor = Color(0xFF4F8BFF)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add food log", tint = Color.White)
            }
        }
    ) { padding ->
        Box(modifier = modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.loading && uiState.logs.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF4F8BFF)
                    )
                }
                uiState.logs.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Fastfood,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No food logs yet", color = Color.Gray, fontSize = 18.sp)
                        Text("Tap + to add your first meal", color = Color.Gray)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.logs) { log ->
                            FoodLogCard(log)
                        }
                    }
                }
            }

            if (uiState.showAddDialog) {
                AddFoodDialog(
                    foodViewModel = foodViewModel,
                    onDismiss = { foodViewModel.toggleAddDialog(false) }
                )
            }
        }
    }
}

@Composable
fun FoodLogCard(log: FoodLogItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTimestamp(log.timestamp ?: 0L),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                val totalCals = log.foodInLogs?.sumOf { it.calories ?: 0 } ?: 0
                Text(
                    text = "$totalCals cal",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF4F8BFF),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            log.foodInLogs?.forEach { food ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = food.food.name ?: "Unknown",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${food.calories ?: 0} cal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF0F1724)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodDialog(
    foodViewModel: FoodViewModel,
    onDismiss: () -> Unit
) {
    val uiState by foodViewModel.uiState.collectAsStateWithLifecycle()
    var currentStage by remember { mutableStateOf("FOOD_NAME") }
    var selectedFood by remember { mutableStateOf<FoodItem?>(null) }
    var manualFoodName by remember { mutableStateOf("") }
    var manualCalories by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = {
            currentStage = "FOOD_NAME"
            selectedFood = null
            manualFoodName = ""
            manualCalories = ""
            quantity = "1"
            onDismiss()
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (currentStage == "CALORIE_QUANTITY") {
                    IconButton(onClick = {
                        currentStage = "FOOD_NAME"
                        selectedFood = null
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
                Text(
                    text = when (currentStage) {
                        "FOOD_NAME" -> "Search Food"
                        else -> "Add Details"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (currentStage) {
                    "FOOD_NAME" -> {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { foodViewModel.searchFood(it) },
                            label = { Text("Food name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )

                        if (uiState.searchResults.isNotEmpty()) {
                            Text(
                                "Suggestions from database:",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray
                            )
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(2.dp, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 200.dp)
                                ) {
                                    items(uiState.searchResults) { food ->
                                        ListItem(
                                            headlineContent = { Text(food.name ?: "") },
                                            supportingContent = { Text("${food.calories ?: 0} cal", color = Color.Gray) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedFood = food
                                                    manualFoodName = food.name ?: ""
                                                    manualCalories = food.calories?.toString() ?: ""
                                                    currentStage = "CALORIE_QUANTITY"
                                                }
                                        )
                                        if (food != uiState.searchResults.last()) {
                                            HorizontalDivider()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        OutlinedTextField(
                            value = manualFoodName,
                            onValueChange = {},
                            label = { Text("Food name") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            enabled = false,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = manualCalories,
                            onValueChange = { manualCalories = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Calories") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Quantity") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                if (uiState.error != null) {
                    Text(
                        text = uiState.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (uiState.loading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF4F8BFF)
                    )
                }
            }
        },
        confirmButton = {
            when (currentStage) {
                "FOOD_NAME" -> {
                    Button(
                        onClick = {
                            if (uiState.searchQuery.isNotBlank()) {
                                manualFoodName = uiState.searchQuery.trim()
                                manualCalories = ""
                                currentStage = "CALORIE_QUANTITY"
                            }
                        },
                        enabled = uiState.searchQuery.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F8BFF))
                    ) {
                        Text("Next", color = Color.White)
                    }
                }
                else -> {
                    Button(
                        onClick = {
                            foodViewModel.addFoodLog(
                                foodName = manualFoodName,
                                calories = manualCalories.toIntOrNull() ?: 0,
                                quantity = quantity.toIntOrNull() ?: 1,
                                foodId = selectedFood?.id
                            )
                            currentStage = "FOOD_NAME"
                            selectedFood = null
                            manualFoodName = ""
                            manualCalories = ""
                            quantity = "1"
                        },
                        enabled = !uiState.loading && manualCalories.isNotBlank() && manualCalories.toIntOrNull() != null,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F8BFF))
                    ) {
                        Text("Add", color = Color.White)
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                currentStage = "FOOD_NAME"
                selectedFood = null
                manualFoodName = ""
                manualCalories = ""
                quantity = "1"
                onDismiss()
            }) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            kotlinx.coroutines.delay(3000)
            foodViewModel.clearError()
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
