package com.example.alpvp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
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
import com.example.alpvp.data.dto.FriendFoodLog
import com.example.alpvp.ui.theme.*
import com.example.alpvp.ui.viewModel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    dashboardViewModel: DashboardViewModel
) {
    var showAddFriendDialog by remember { mutableStateOf(false) }
    val uiState by dashboardViewModel.uiState.collectAsState()

    val friendLogs = uiState.dashboardData?.recentFriendFoodLogs?.sortedByDescending { it.timestamp } ?: emptyList()

    // Extract unique friends from friend logs
    val uniqueFriends = friendLogs
        .distinctBy { it.friendId }
        .map { Friend(userId = it.friendId, name = it.friendName) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Friends",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showAddFriendDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add Friend",
                            tint = ElectricBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight,
                    titleContentColor = Gray900
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(padding)
        ) {
            // Subtitle
            item {
                Text(
                    text = "See what your friends are eating",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Gray600,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 16.dp, bottom = 24.dp)
                )
            }

            // Friends Grid
            item {
                Text(
                    text = "Your Friends (${uniqueFriends.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray900,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
            }

            if (uniqueFriends.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        uniqueFriends.take(4).forEach { friend ->
                            FriendCard(
                                friend = friend,
                                friendLogs = friendLogs,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Recent Activity
            item {
                Text(
                    text = "Recent Activity",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray900,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
            }

            items(friendLogs) { log ->
                FriendFoodLogItem(log = log)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (showAddFriendDialog) {
            AddFriendDialog(onDismiss = { showAddFriendDialog = false })
        }
    }
}

// Simple data class for representing a friend
data class Friend(
    val userId: Int,
    val name: String
)

@Composable
fun FriendCard(
    friend: Friend,
    friendLogs: List<FriendFoodLog>,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color(0xFFFF6B6B),
        Color(0xFF2F80ED),
        Color(0xFF51CF66),
        Color(0xFFFFA94D)
    )
    val avatarColor = colors[friend.userId % colors.size]
    val initials = friend.name.split(" ").mapNotNull { it.firstOrNull() }.joinToString("")

    // Calculate logs today for this friend
    val todayStart = System.currentTimeMillis() - (System.currentTimeMillis() % (24 * 60 * 60 * 1000L))
    val logsToday = friendLogs.count { it.friendId == friend.userId && it.timestamp >= todayStart }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = SurfaceWhite,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = SurfaceWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = friend.name.split(" ").first(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Gray900
            )
            Text(
                text = "$logsToday logs today",
                fontSize = 12.sp,
                color = Gray500
            )
        }
    }
}

@Composable
fun FriendFoodLogItem(log: FriendFoodLog) {
    val colors = listOf(
        Color(0xFFFF6B6B),
        Color(0xFF2F80ED),
        Color(0xFF51CF66),
        Color(0xFFFFA94D)
    )
    val avatarColor = colors[log.friendId % colors.size]
    val initials = log.friendName.split(" ").mapNotNull { it.firstOrNull() }.joinToString("")

    val timeAgo = run {
        val minutes = ((System.currentTimeMillis() - log.timestamp) / 60000).toInt()
        when {
            minutes < 60 -> "${minutes}m ago"
            minutes < 1440 -> "${minutes / 60}h ago"
            else -> "${minutes / 1440}d ago"
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceWhite,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = SurfaceWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = log.friendName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Gray900
                    )
                    Text(
                        text = timeAgo,
                        fontSize = 12.sp,
                        color = Gray500
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = log.foodName,
                    fontSize = 14.sp,
                    color = Gray700
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "🔥 ${log.calories} cal",
                        fontSize = 12.sp,
                        color = Gray600
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "📦 ${log.quantity}x",
                        fontSize = 12.sp,
                        color = Gray600
                    )
                }
            }
        }
    }
}

@Composable
fun AddFriendDialog(onDismiss: () -> Unit) {
    var email by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BackgroundLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Add Friend",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray900
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Connect with friends to share your food journey and stay motivated together.",
                    fontSize = 14.sp,
                    color = Gray600
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Enter friend's email...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Gray200
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricBlue
                    )
                ) {
                    Text(text = "Send Friend Request", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "They'll receive a notification to accept your request",
                    fontSize = 12.sp,
                    color = Gray500,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
