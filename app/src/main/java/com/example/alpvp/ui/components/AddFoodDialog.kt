import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alpvp.data.dto.FoodItem
import com.example.alpvp.ui.theme.*
import com.example.alpvp.ui.viewModel.FoodViewModel
import com.example.alpvp.ui.model.FoodModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodDialog(
    foodViewModel: FoodViewModel,
    onDismiss: () -> Unit
) {
    val uiState by foodViewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFood by remember { mutableStateOf<FoodModel?>(null) }
    var customCalories by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var showSearchResults by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {
            searchQuery = ""
            selectedFood = null
            customCalories = ""
            quantity = "1"
            onDismiss()
        },
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(28.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Add Food",
                        fontWeight = FontWeight.Bold,
                        color = Gray900,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Gray600)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        if (it.isNotBlank()) {
                            foodViewModel.searchFood(it)
                            showSearchResults = true
                        } else {
                            showSearchResults = false
                        }
                    },
                    label = { Text("Search food or enter custom", color = Gray600) },
                    placeholder = { Text("e.g., Apple, Pizza, Salad...", color = Gray400) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = ElectricBlue)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                showSearchResults = false
                                selectedFood = null
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Gray500)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Gray300,
                        focusedLabelColor = ElectricBlue,
                        unfocusedLabelColor = Gray600,
                        cursorColor = ElectricBlue
                    ),
                    singleLine = true
                )

                // Search Results Dropdown
                if (showSearchResults && uiState.searchResults.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(uiState.searchResults) { food ->
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            food.name,
                                            fontWeight = FontWeight.Medium,
                                            color = Gray900
                                        )
                                    },
                                    supportingContent = {
                                        Text(
                                            "${food.calories} cal per serving",
                                            color = Gray600,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    leadingContent = {
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = ElectricBlue.copy(alpha = 0.1f)
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Fastfood,
                                                contentDescription = null,
                                                tint = ElectricBlue,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    },
                                    colors = androidx.compose.material3.ListItemDefaults.colors(
                                        containerColor = SurfaceWhite,
                                        headlineColor = Gray900,
                                        supportingColor = Gray600
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedFood = food
                                            searchQuery = food.name
                                            customCalories = food.calories.toString()
                                            showSearchResults = false
                                        }
                                )
                                if (uiState.searchResults.last() != food) {
                                    HorizontalDivider(color = Gray200, thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }

                // Selected Food or Manual Entry Card
                if (searchQuery.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedFood != null) ElectricBlue.copy(alpha = 0.08f) else BackgroundLight
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Food Name Display
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Fastfood,
                                    contentDescription = null,
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = searchQuery,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Gray900
                                    )
                                    Text(
                                        text = if (selectedFood != null) "From database" else "Custom food",
                                        fontSize = 12.sp,
                                        color = Gray600
                                    )
                                }
                            }

                            HorizontalDivider(color = Gray300)

                            // Calories and Quantity Input
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Calories Field
                                OutlinedTextField(
                                    value = customCalories,
                                    onValueChange = { 
                                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                            customCalories = it
                                        }
                                    },
                                    label = { Text("Calories", fontSize = 14.sp) },
                                    placeholder = { Text("0", color = Gray400) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ElectricBlue,
                                        unfocusedBorderColor = Gray300,
                                        focusedLabelColor = ElectricBlue,
                                        cursorColor = ElectricBlue
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )

                                // Quantity Field
                                OutlinedTextField(
                                    value = quantity,
                                    onValueChange = { 
                                        if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.toIntOrNull() != 0)) {
                                            quantity = it
                                        }
                                    },
                                    label = { Text("Quantity", fontSize = 14.sp) },
                                    placeholder = { Text("1", color = Gray400) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ElectricBlue,
                                        unfocusedBorderColor = Gray300,
                                        focusedLabelColor = ElectricBlue,
                                        cursorColor = ElectricBlue
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                            }

                            // Total Calories Display
                            val totalCals = (customCalories.toIntOrNull() ?: 0) * (quantity.toIntOrNull() ?: 1)
                            if (totalCals > 0) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = ElectricBlue
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Total Calories",
                                            color = SurfaceWhite,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            "$totalCals cal",
                                            color = SurfaceWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Current Food List
                if (uiState.selectedFoods.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Foods in this log (${uiState.selectedFoods.size})",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Gray900
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp),
                            colors = CardDefaults.cardColors(containerColor = BackgroundLight),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(uiState.selectedFoods.size) { index ->
                                    val entry = uiState.selectedFoods[index]
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1f),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Fastfood,
                                                    contentDescription = null,
                                                    tint = ElectricBlue,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Column {
                                                    Text(
                                                        entry.name,
                                                        fontWeight = FontWeight.Medium,
                                                        color = Gray900,
                                                        fontSize = 14.sp
                                                    )
                                                    Text(
                                                        "${entry.calories * entry.quantity} cal (${entry.quantity}x)",
                                                        color = Gray600,
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }
                                            IconButton(
                                                onClick = { foodViewModel.removeSelectedFood(index) }
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Remove",
                                                    tint = ErrorRed,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Grand Total
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = ElectricBlue.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Total Calories",
                                    fontWeight = FontWeight.Bold,
                                    color = Gray900,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${uiState.selectedFoods.sumOf { it.calories * it.quantity }} cal",
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricBlue,
                                    fontSize = 22.sp
                                )
                            }
                        }
                    }
                }

                // Error Message
                if (uiState.error != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ErrorRedLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚠️", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                uiState.error ?: "",
                                color = ErrorRed,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Loading Indicator
                if (uiState.loading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = ElectricBlue,
                        trackColor = ElectricBlue.copy(alpha = 0.2f)
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Add to Log Button (if there's a valid food entry)
                if (searchQuery.isNotEmpty() && customCalories.isNotEmpty() && quantity.isNotEmpty()) {
                    Button(
                        onClick = {
                            val name = searchQuery.trim()
                            val cals = customCalories.toIntOrNull() ?: 0
                            val qty = quantity.toIntOrNull() ?: 1
                            if (name.isNotEmpty() && cals > 0 && qty > 0) {
                                foodViewModel.addFoodLog(name, cals, qty, selectedFood?.id)
                                // Reset fields
                                searchQuery = ""
                                selectedFood = null
                                customCalories = ""
                                quantity = "1"
                                showSearchResults = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBlue,
                            contentColor = SurfaceWhite
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add", fontWeight = FontWeight.SemiBold)
                    }
                }

                // Submit Log Button (if there are foods in the list)
                if (uiState.selectedFoods.isNotEmpty()) {
                    Button(
                        onClick = { 
                            foodViewModel.submitFoodLog()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreen,
                            contentColor = SurfaceWhite
                        ),
                        enabled = !uiState.loading
                    ) {
                        if (uiState.loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = SurfaceWhite,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Submit Log", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    searchQuery = ""
                    selectedFood = null
                    customCalories = ""
                    quantity = "1"
                    onDismiss()
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel", color = Gray600, fontWeight = FontWeight.Medium)
            }
        }
    )
}
