package com.example.alpvp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.alpvp.ui.theme.*

@Composable
fun ContextDetectionDialog(
    restaurantName: String,
    duration: String = "20 minutes",
    onLogMeal: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceWhite
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Gray600
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = ElectricBlue.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🍽️",
                        fontSize = 40.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = "Context Detected",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Gray900
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Message
                Text(
                    text = "You've been at ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Gray600,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = restaurantName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = ElectricBlue,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = " for $duration. Are you having a meal?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Gray600,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Why we're asking card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = BackgroundLight
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "📊",
                            fontSize = 20.sp
                        )
                        Column {
                            Text(
                                text = "Why we're asking",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Gray900
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Nudge learns your eating patterns to help you reach your BMI goal. Quick logging now helps us give better suggestions later.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Gray600,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Context info
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Gray600,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${getCurrentTime()} • Typical meal time",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gray600
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Gray600,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = restaurantName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gray600
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Button(
                    onClick = onLogMeal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricBlue,
                        contentColor = SurfaceWhite
                    )
                ) {
                    Text(
                        text = "Yes, Log A Meal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BackgroundLight,
                        contentColor = Gray700
                    )
                ) {
                    Text(
                        text = "No, Just Visiting",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Footer text
                Text(
                    text = "Your response helps Nudge understand your behavior patterns",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun getCurrentTime(): String {
    val calendar = java.util.Calendar.getInstance()
    val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
    val minute = calendar.get(java.util.Calendar.MINUTE)
    val amPm = if (hour < 12) "AM" else "PM"
    val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    return String.format("%d:%02d %s", displayHour, minute, amPm)
}
