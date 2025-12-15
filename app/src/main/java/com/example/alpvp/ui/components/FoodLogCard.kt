import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.alpvp.data.dto.FoodLogItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}