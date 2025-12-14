package com.example.alpvp.ui.view.Friend

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alpvp.R

@Composable
fun CardView() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Row(
            modifier = Modifier
                .background(Color(0xFFFFFFFF))
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                painter = painterResource(R.drawable.screenshot__1177_),
                "Friend pfp"
            )
            Column {
                Text("John Doe",
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF000000)
                )
                Text("180cm | 65kg | BMI Goal: 22.5",
                    fontWeight = FontWeight.Light,
                    color = Color(0xFF000000)
                )
            }
            Image(
                painter = painterResource(R.drawable.unfriend),
                "unfriend"
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun Preview(){
    CardView()
}