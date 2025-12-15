import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alpvp.data.dto.FoodItem
import com.example.alpvp.ui.viewModel.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodDialog(
    foodViewModel: FoodViewModel,
    onDismiss: () -> Unit
) {
    val uiState by foodViewModel.uiState.collectAsStateWithLifecycle()
    var currentStage by remember { mutableStateOf("FOOD_LIST") }
    var selectedFood by remember { mutableStateOf<FoodItem?>(null) }
    var manualFoodName by remember { mutableStateOf("") }
    var manualCalories by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = {
            currentStage = "FOOD_LIST"
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
                if (currentStage == "ADD_FOOD") {
                    IconButton(onClick = { currentStage = "FOOD_LIST" }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
                Text(
                    text = when (currentStage) {
                        "FOOD_LIST" -> "Add Food Log"
                        else -> "Add Food Item"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (currentStage) {
                    "FOOD_LIST" -> {
                        if (uiState.selectedFoods.isNotEmpty()) {
                            Text("Foods in this log:", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                            Card(
                                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)) {
                                    items(uiState.selectedFoods.size) { index ->
                                        val entry = uiState.selectedFoods[index]
                                        ListItem(
                                            headlineContent = { Text("${entry.name} x${entry.quantity}") },
                                            supportingContent = { Text("${entry.calories * entry.quantity} cal", color = Color.Gray) },
                                            trailingContent = {
                                                IconButton(onClick = { foodViewModel.removeSelectedFood(index) }) {
                                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red)
                                                }
                                            }
                                        )
                                        if (index < uiState.selectedFoods.size - 1) {
                                            HorizontalDivider()
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Button(
                            onClick = { currentStage = "ADD_FOOD" },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F8BFF))
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Another Food", color = Color.White)
                        }
                    }

                    "ADD_FOOD" -> {
                        OutlinedTextField(
                            value = manualFoodName,
                            onValueChange = {
                                manualFoodName = it
                                foodViewModel.searchFood(it)
                            },
                            label = { Text("Food name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (uiState.searchResults.isNotEmpty() && manualFoodName.isNotBlank()) {
                            Card(
                                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 150.dp)) {
                                    items(uiState.searchResults) { food ->
                                        ListItem(
                                            headlineContent = { Text(food.name ?: "") },
                                            supportingContent = { Text("${food.calories ?: 0} cal", color = Color.Gray) },
                                            modifier = Modifier.fillMaxWidth().clickable {
                                                selectedFood = food
                                                manualFoodName = food.name ?: ""
                                                manualCalories = food.calories?.toString() ?: ""
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = manualCalories,
                            onValueChange = { manualCalories = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Calories") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Quantity") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                if (uiState.error != null) {
                    Text(uiState.error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                if (uiState.loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color(0xFF4F8BFF))
                }
            }
        },
        confirmButton = {
            when (currentStage) {
                "FOOD_LIST" -> {
                    Button(
                        onClick = { foodViewModel.submitFoodLog() },
                        enabled = uiState.selectedFoods.isNotEmpty() && !uiState.loading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F8BFF))
                    ) {
                        Text("Submit Log", color = Color.White)
                    }
                }
                "ADD_FOOD" -> {
                    Button(
                        onClick = {
                            foodViewModel.addFoodLog(
                                foodName = manualFoodName,
                                calories = manualCalories.toIntOrNull() ?: 0,
                                quantity = quantity.toIntOrNull() ?: 1,
                                foodId = selectedFood?.id
                            )
                            currentStage = "FOOD_LIST"
                            selectedFood = null
                            manualFoodName = ""
                            manualCalories = ""
                            quantity = "1"
                        },
                        enabled = manualFoodName.isNotBlank() && manualCalories.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F8BFF))
                    ) {
                        Text("Add to Log", color = Color.White)
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                currentStage = "FOOD_LIST"
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
}