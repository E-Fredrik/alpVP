package com.example.alpvp.ui.view.Friend

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alpvp.R

@Composable
fun View() {
    var search by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .background(Color(0xFFDAE7FF))
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 16.dp)
    ) {
        Text("Friends List",
            fontWeight = FontWeight.Bold,
            fontSize = 50.sp,
            color = Color(0xFF000000)
        )
        Column(
            modifier = Modifier
                .padding(top = 16.dp)
        ) {
            TextField(
                value = search,
                onValueChange = {search = it},
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFFFFFFF),
                    unfocusedIndicatorColor = Color(0x00FFFFFF),
                    focusedContainerColor = Color(0xFFFFFFFF),
                    focusedIndicatorColor = Color(0x00FFFFFF)
                ),
                shape = RoundedCornerShape(30.dp),
                maxLines = 1,
                leadingIcon = {
                    TextButton(
                        onClick = {}
                    ) {
                        Image(
                            painter = painterResource(R.drawable.search),
                            "search"
                        )
                    }
                }
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun Preview(){
    View()
}