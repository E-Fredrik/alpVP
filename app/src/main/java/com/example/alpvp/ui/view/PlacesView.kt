package com.example.alpvp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alpvp.data.dto.Place
import com.example.alpvp.ui.viewModel.PlaceViewModel
import com.example.alpvp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesView(
    placeViewModel: PlaceViewModel,
    onBack: () -> Unit
) {
    val uiState by placeViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        placeViewModel.loadAllPlaces()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Places",
                        color = SurfaceWhite,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            "Back",
                            tint = SurfaceWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ElectricBlue,
                    titleContentColor = SurfaceWhite,
                    navigationIconContentColor = SurfaceWhite
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
        ) {
            // Category Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedCategory == null,
                        onClick = { placeViewModel.filterByCategory(null) },
                        label = { Text("All") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = SurfaceWhite,
                            containerColor = Gray100,
                            labelColor = Gray700
                        )
                    )
                }
                items(listOf("RESTAURANT", "PARK", "GYM", "STORE")) { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { placeViewModel.filterByCategory(category) },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = SurfaceWhite,
                            containerColor = Gray100,
                            labelColor = Gray700
                        )
                    )
                }
            }

            // Places List
            if (uiState.loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ElectricBlue)
                }
            } else if (uiState.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Error loading places",
                            style = MaterialTheme.typography.titleMedium,
                            color = CoralRed
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            uiState.error ?: "Unknown error",
                            style = MaterialTheme.typography.bodySmall,
                            color = Gray600
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { placeViewModel.loadAllPlaces() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricBlue,
                                contentColor = SurfaceWhite
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                val filteredPlaces = if (uiState.selectedCategory == null) {
                    uiState.places
                } else {
                    uiState.places.filter { it.category == uiState.selectedCategory }
                }

                if (filteredPlaces.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No places found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Gray600
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredPlaces) { place ->
                            PlaceCard(place)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceCard(place: Place) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(getCategoryColor(place.category), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    getCategoryEmoji(place.category),
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Place Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    place.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Gray900
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    place.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray600
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Gray500
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "%.4f, %.4f".format(place.latitude, place.longitude),
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray500,
                        fontSize = 11.sp
                    )
                }
                if (place.geofenceRadius > 0) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Radius: ${place.geofenceRadius}m",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray500,
                        fontSize = 11.sp
                    )
                }
            }

            // Distance badge (placeholder - could calculate from user location)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = BackgroundLight
            ) {
                Text(
                    "${place.place_id}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray700,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

fun getCategoryEmoji(category: String): String = when(category) {
    "RESTAURANT" -> "🍽️"
    "PARK" -> "🌳"
    "GYM" -> "💪"
    "STORE" -> "🛒"
    "OTHER" -> "📍"
    else -> "📍"
}

fun getCategoryColor(category: String): Color = when(category) {
    "RESTAURANT" -> CategoryRestaurant
    "PARK" -> CategoryPark
    "GYM" -> CategoryGym
    "STORE" -> CategoryStore
    "OTHER" -> CategoryOther
    else -> CategoryOther
}

