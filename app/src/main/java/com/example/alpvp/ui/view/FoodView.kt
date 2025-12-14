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
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.*
import kotlin.toString

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

//            if (!uiState.error.isNullOrEmpty()) {
//                Snackbar(
//                    modifier = Modifier
//                        .align(Alignment.BottomCenter)
//                        .padding(16.dp),
//                    action = {
//                        TextButton(onClick = { foodViewModel.() }) {
//                            Text("Dismiss")
//                        }
//                    }
//                ) {
//                    Text(uiState.error ?: "")
//                }
//            }
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
                    text = formatTimestamp(log.timestamp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                val totalCals = log.foodInLogs.sumOf { it.calories }
                Text(
                    text = "$totalCals cal",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF4F8BFF),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            log.foodInLogs.forEach { foodInLog ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = foodInLog.food.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Qty: ${foodInLog.quantity}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Text(
                        text = "${foodInLog.calories} cal",
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
    var searchQuery by remember { mutableStateOf(uiState.searchQuery) }
    var quantity by remember { mutableStateOf("1") }
    var manualCalories by remember { mutableStateOf("") }
    val selectedFood = uiState.selectedFood

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Add food", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        // forward search to VM so repository is queried
                        foodViewModel.searchFood(it)
                    },
                    label = { Text("Food name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Show suggestions when available and no selectedFood
                val suggestions = uiState.searchResults
                if (suggestions.isNotEmpty() && selectedFood == null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column {
                            suggestions.forEach { food ->
                                // use safe calls and Material3 ListItem signature
                                ListItem(
                                    headlineContent = {
                                        Text(text = food?.name ?: "")
                                    },
                                    supportingContent = {
                                        Text(text = "${food?.calories ?: 0} cal", color = Color.Gray)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            // select suggestion in VM (will set searchQuery there too)
                                            food?.let {
                                                foodViewModel.selectFood(it)
                                                searchQuery = it.name ?: ""
                                            }
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                                Divider()
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // show calories for selected suggestion, otherwise manual entry
                if (selectedFood != null) {
                    Text("Calories: ${selectedFood.calories}", color = Color.Gray)
                } else {
                    OutlinedTextField(
                        value = manualCalories,
                        onValueChange = { manualCalories = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Calories") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Quantity") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val qty = quantity.toIntOrNull() ?: 1
                val nameToUse = selectedFood?.name ?: searchQuery.trim()
                val caloriesToUse = selectedFood?.calories ?: (manualCalories.toIntOrNull() ?: 0)

                if (nameToUse.isBlank() || caloriesToUse <= 0) {
                    return@Button
                }

                foodViewModel.addFoodLog(nameToUse, caloriesToUse, qty)
            },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F8BFF))
            ) {
                Text("Add", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
        }
    )
}




private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
