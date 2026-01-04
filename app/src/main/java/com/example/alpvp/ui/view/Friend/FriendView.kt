package com.example.alpvp.ui.view.Friend

import android.hardware.lights.Light
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alpvp.R
import com.example.alpvp.data.dto.FriendFoodLog
import com.example.alpvp.ui.model.Friend
import com.example.alpvp.ui.viewModel.FriendViewModel

@Composable
fun yourFriends(data: List<FriendFoodLog>){
    LazyColumn {
        items(data){friendFoodlog -> FriendCardView(friendFoodlog = friendFoodlog)}
    }
}

@Composable
fun recentActivity(data: List<FriendFoodLog>){
    LazyColumn {
        items(data){friendFoodlog -> RecentActivityCardView(friendFoodlog = friendFoodlog)}
    }
}

@Composable
fun FriendView(friend: List<Friend>,
               friendViewModel: FriendViewModel
               ) {
    var addFriend by rememberSaveable { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    val friendCount = friend.size

    if (!addFriend) {
        Column(
            modifier = Modifier
                .background(Color(0xFFDAE7FF))
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Friends",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = Color(0xFF000000)
                )
                TextButton(
                    onClick = {addFriend = true},
                    enabled = true,
                    modifier = Modifier
                        .background(Color(0xFF005AFF))
                ) {
                    Image(
                        painter = painterResource(R.drawable.addicon),
                        "search"
                    )
                }
            }
            Text("See what your friends are eating",
                color = Color(0x80000000)
            )
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
            ) {
                Text("Your Friends ($friendCount)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF000000)
                )
            }
            yourFriends()
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
            ) {
                Text("Recent Activity",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF000000)
                )
            }
            recentActivity()
        }
    }else{
        Column(
            modifier = Modifier
                .background(Color(0xFFDAE7FF))
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 16.dp)
        ) {
            Row (
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextButton(
                    onClick = {addFriend = false},
                    enabled = true,
                ) {
                    Image(
                        painter = painterResource(R.drawable.close),
                        "close"
                    )
                }
            }
            Column(
                modifier = Modifier
                    .background(Color(0xFF0057FF))
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.addicon),
                    "icon", modifier = Modifier
                        .size(50.dp)
                )
            }
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text("Add Friend",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = Color(0xFF000000)
                )
                Text("Connect with friends to share your food journey and stay motivated together.",
                    color = Color(0x99000000)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Enter your friend's email") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFFFFFFF),
                        unfocusedIndicatorColor = Color(0x00000000),
                        focusedContainerColor = Color(0xFFFFFFFF),
                        focusedTextColor = Color(0xFF000000),
                        unfocusedTextColor = Color(0xFF000000),
                        focusedIndicatorColor = Color(0x00000000),
                        focusedLabelColor = Color(0x80000000),
                        unfocusedLabelColor = Color(0x80000000)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
                TextButton(
                    onClick = {
                        addFriend = false
                        friendViewModel.searchFriends(search)
                              },
                    enabled = true,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .background(Color(0xFF005AFF))
                        .fillMaxWidth()
                ) {
                    Text("Send Friend Request",
                        color = Color(0xFFFFFFFF)
                    )
                }
                Text("They'll receive a notification to accept your request",
                    color = Color(0x99000000)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun Preview(){
    FriendView()
}