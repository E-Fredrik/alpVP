package com.example.alpvp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun EmaPromptDialog(
    onDismiss: () -> Unit,
    onSubmit: (moodScore: Int, context: String) -> Unit
) {
    var moodScore by remember { mutableStateOf(5) }
    var context by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "How are you feeling?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Rate your mood from 1 (bad) to 10 (great)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Mood Score Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    (1..10).forEach { score ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    if (score == moodScore) Color(0xFF4F8BFF)
                                    else Color(0xFFE5E7EB)
                                )
                                .clickable { moodScore = score },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = score.toString(),
                                color = if (score == moodScore) Color.White else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Mood Emoji Display
                Text(
                    text = getMoodEmoji(moodScore),
                    fontSize = 48.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Context Input
                OutlinedTextField(
                    value = context,
                    onValueChange = { context = it },
                    label = { Text("What's happening? (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { onSubmit(moodScore, context) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4F8BFF)
                        )
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}

private fun getMoodEmoji(score: Int): String {
    return when (score) {
        in 1..2 -> "😢"
        in 3..4 -> "😕"
        in 5..6 -> "😐"
        in 7..8 -> "🙂"
        else -> "😄"
    }
}
