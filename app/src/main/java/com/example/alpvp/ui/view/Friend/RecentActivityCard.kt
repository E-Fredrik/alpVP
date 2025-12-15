package com.example.alpvp.ui.view.Friend

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alpvp.R
import com.example.alpvp.R

@Composable
fun RecentActivityCardView() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Row(
            modifier = Modifier
                .background(Color(0xFFFFFFFF))
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Row {
                Image(
                    painter = painterResource(R.drawable.screenshot__1177_),
                    "Friend pfp"
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                ) {
                    Text("John Doe")
                    Text("Hamburger",
                        color = Color(0xBF000000)
                    )
                    Row {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.bolt),
                                "cal",
                                modifier = Modifier
                                    .size(18.dp)
                            )
                            Text("# cal",
                                color = Color(0xBF000000)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.anchor),
                                "protein",
                                modifier = Modifier
                                    .size(18.dp)
                            )
                            Text("#g protein",
                                color = Color(0xBF000000)
                            )
                        }
                    }
                }
            }
            Row {
                Image(
                    painter = painterResource(R.drawable.time),
                    "Friend pfp",
                    modifier = Modifier
                        .size(18.dp)
                )
                Text("1m ago",
                    color = Color(0x80000000)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun Preview(){
    RecentActivityCardView()
}