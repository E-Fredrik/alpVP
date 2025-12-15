package com.example.alpvp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alpvp.ui.viewModel.AuthViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var heightCm by remember { mutableStateOf("") }
    var weightLbs by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val loading = uiState.loading
    val error = uiState.error

    val bg = Brush.verticalGradient(listOf(Color(0xFFF3F7FB), Color(0xFFEFF4FB)))

    Surface(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .background(bg)
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Create account",
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 28.sp),
                    color = Color(0xFF0F1724),
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full name") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (showPassword) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            trailingIcon = {
                                TextButton(onClick = { showPassword = !showPassword }) {
                                    Text(if (showPassword) "Hide" else "Show")
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = heightCm,
                                onValueChange = { heightCm = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Height (cm)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = weightLbs,
                                onValueChange = { weightLbs = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Weight (lbs)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = goal,
                            onValueChange = { goal = it.filter { ch -> ch.isDigit() } },
                            label = { Text("BMI goal") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (!error.isNullOrEmpty()) {
                            Text(text = error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
                        }

                        Button(
                            onClick = {
                                val h = heightCm.toIntOrNull() ?: 0
                                val w = weightLbs.toIntOrNull() ?: 0
                                val g = goal.toIntOrNull() ?: 0
                                scope.launch {
                                    authViewModel.register(
                                        name = name.trim(),
                                        email = email.trim(),
                                        password = password,
                                        height = h,
                                        weight = w,
                                        bmiGoal = g
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F8BFF))
                        ) {
                            if (loading) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text("Create account", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row {
                    Text("Already have an account?", color = Color.Gray)
                    Spacer(modifier = Modifier.width(6.dp))
                    TextButton(onClick = { /* navigation handled by AppRouting */ }) {
                        Text("Sign in", color = Color(0xFF4F8BFF))
                    }
                }
            }
        }
    }
}
