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
                AddFoodDialog (
                    foodViewModel = foodViewModel,
                    onDismiss = { foodViewModel.toggleAddDialog(false) }
                )
            }
        }
    }
}








