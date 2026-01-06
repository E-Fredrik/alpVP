import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alpvp.ui.model.FoodLogModel
import com.example.alpvp.ui.theme.BackgroundLight
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FoodLogCard(log: FoodLogModel) {
    val totalCals = log.foodInLogs.sumOf { it.calories }
    var isMapExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF4F8BFF),
                                Color(0xFF6B9FFF)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = getMealEmoji(log.timestamp),
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatTimestamp(log.timestamp),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Calorie badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$totalCals",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "calories",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Food items list
            Column(modifier = Modifier.padding(16.dp)) {
                log.foodInLogs.forEachIndexed { index, food ->
                    if (index > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = BackgroundLight,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Food emoji/icon
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = Color(0xFF4F8BFF).copy(alpha = 0.1f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🍽️",
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = food.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1A1A)
                            )
                            if (food.quantity > 1) {
                                Text(
                                    text = "Qty: ${food.quantity}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF666666)
                                )
                            }
                        }

                        // Calorie chip
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFFFE5E5),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${food.calories} cal",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFFF6B6B),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Location info if available
                if (log.latitude != 0.0 && log.longitude != 0.0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isMapExpanded = !isMapExpanded }
                            .background(
                                color = BackgroundLight,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color(0xFF4F8BFF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isMapExpanded) "Hide map" else "Tap to view map",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF666666),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (isMapExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isMapExpanded) "Collapse" else "Expand",
                            tint = Color(0xFF4F8BFF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    // Embedded Map
                    if (isMapExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .background(
                                    color = BackgroundLight,
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            val position = LatLng(log.latitude, log.longitude)
                            val cameraPositionState = rememberCameraPositionState {
                                this.position = CameraPosition.fromLatLngZoom(position, 15f)
                            }
                            
                            GoogleMap(
                                modifier = Modifier.fillMaxWidth().height(250.dp),
                                cameraPositionState = cameraPositionState
                            ) {
                                Marker(
                                    state = MarkerState(position = position),
                                    title = "Food logged here",
                                    snippet = String.format(Locale.getDefault(), "%.4f, %.4f", log.latitude, log.longitude)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getMealEmoji(timestamp: Long): String {
    val hour = SimpleDateFormat("HH", Locale.getDefault()).format(Date(timestamp)).toInt()
    return when (hour) {
        in 5..10 -> "☀️" // Breakfast
        in 11..14 -> "🌤️" // Lunch
        in 15..17 -> "🍎" // Snack
        in 18..22 -> "🌙" // Dinner
        else -> "🌃" // Late night
    }
}


private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}