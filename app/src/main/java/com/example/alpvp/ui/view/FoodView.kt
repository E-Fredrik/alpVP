package com.example.alpvp.ui.view

import AddFoodDialog
import FoodLogCard
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
import com.example.alpvp.ui.model.FoodLogModel
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alpvp.data.dto.FoodItem
import com.example.alpvp.data.dto.FoodLogItem
import com.example.alpvp.ui.theme.*
import com.example.alpvp.ui.viewModel.FoodViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.compareTo
import kotlin.text.get
import kotlin.times
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
                title = {
                    Text(
                        "Food Logs",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight,
                    titleContentColor = Gray900
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { foodViewModel.toggleAddDialog(true) },
                containerColor = ElectricBlue,
                contentColor = SurfaceWhite
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add food log", tint = SurfaceWhite)
            }
        },
        containerColor = BackgroundLight
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(padding)
        ) {
            when {
                uiState.loading && uiState.logs.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = ElectricBlue)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading food logs...", color = Gray600)
                    }
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
                            tint = Gray400
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No food logs yet", 
                            color = Gray600, 
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Tap + to add your first meal", 
                            color = Gray500,
                            fontSize = 14.sp
                        )
                    }
                }
                else -> {
                    // Group logs by day
                    val groupedLogs = groupLogsByDay(uiState.logs)
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        groupedLogs.forEach { (dateLabel, logs) ->
                            // Day header with total calories
                            item {
                                DayHeader(
                                    dateLabel = dateLabel,
                                    logs = logs
                                )
                            }
                            
                            // Logs for this day
                            items(logs) { log ->
                                FoodLogCard(log)
                            }
                            
                            // Spacer between days
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            if (uiState.showAddDialog) {
                AddFoodDialog (
                    foodViewModel = foodViewModel,
                    onDismiss = { foodViewModel.toggleAddDialog(false) }
                )
            }
        }
    }
}

/**
 * Group food logs by day (Today, Yesterday, specific date)
 */
private fun groupLogsByDay(logs: List<FoodLogModel>): LinkedHashMap<String, List<FoodLogModel>> {
    val calendar = Calendar.getInstance()
    val today = calendar.clone() as Calendar
    today.set(Calendar.HOUR_OF_DAY, 0)
    today.set(Calendar.MINUTE, 0)
    today.set(Calendar.SECOND, 0)
    today.set(Calendar.MILLISECOND, 0)
    
    val yesterday = today.clone() as Calendar
    yesterday.add(Calendar.DAY_OF_YEAR, -1)
    
    val grouped = LinkedHashMap<String, MutableList<FoodLogModel>>()
    
    logs.sortedByDescending { it.timestamp }.forEach { log ->
        val logCalendar = Calendar.getInstance()
        logCalendar.timeInMillis = log.timestamp
        logCalendar.set(Calendar.HOUR_OF_DAY, 0)
        logCalendar.set(Calendar.MINUTE, 0)
        logCalendar.set(Calendar.SECOND, 0)
        logCalendar.set(Calendar.MILLISECOND, 0)
        
        val dateLabel = when {
            logCalendar.timeInMillis == today.timeInMillis -> "Today"
            logCalendar.timeInMillis == yesterday.timeInMillis -> "Yesterday"
            else -> {
                val sdf = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
                sdf.format(Date(log.timestamp))
            }
        }
        
        grouped.getOrPut(dateLabel) { mutableListOf() }.add(log)
    }
    
    // Convert to immutable lists
    return LinkedHashMap(grouped.mapValues { it.value.toList() })
}

/**
 * Day header showing date and total calories
 */
@Composable
private fun DayHeader(
    dateLabel: String,
    logs: List<FoodLogModel>
) {
    val totalCalories = logs.sumOf { log ->
        log.foodInLogs.sumOf { it.calories }
    }
    
    val mealsCount = logs.size
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Gray900
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$mealsCount meal${if (mealsCount != 1) "s" else ""} logged",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray600
                )
            }
            
            // Total calories badge
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = ElectricBlue.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$totalCalories",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue
                    )
                    Text(
                        text = "calories",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricBlue,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
