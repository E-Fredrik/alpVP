package com.example.alpvp.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alpvp.ui.theme.*
import com.example.alpvp.ui.viewModel.AuthViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit = {}
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

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Create account",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Gray900,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Text(
                text = "Join us to start tracking your calories",
                style = MaterialTheme.typography.bodyMedium,
                color = Gray600,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Registration Form Card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SurfaceWhite,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Full Name Field
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full name") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = ElectricBlue
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = Gray200
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email address") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = ElectricBlue
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = Gray200
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = ElectricBlue
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showPassword) "Hide password" else "Show password",
                                    tint = Gray500
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = Gray200
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Section Title
                    Text(
                        text = "Physical Information",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Gray700,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Height and Weight Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = heightCm,
                            onValueChange = { heightCm = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Height (cm)") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Height,
                                    contentDescription = null,
                                    tint = ElectricBlue
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = Gray200
                            )
                        )

                        OutlinedTextField(
                            value = weightLbs,
                            onValueChange = { weightLbs = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Weight (kg)") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.MonitorWeight,
                                    contentDescription = null,
                                    tint = ElectricBlue
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = Gray200
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // BMI Goal Field
                    OutlinedTextField(
                        value = goal,
                        onValueChange = { goal = it.filter { ch -> ch.isDigit() } },
                        label = { Text("BMI goal") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = ElectricBlue
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = Gray200
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Error Message
                    if (!error.isNullOrEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Error.copy(alpha = 0.1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = error,
                                color = Error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Register Button
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
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBlue,
                            disabledContainerColor = Gray200
                        ),
                        enabled = !loading
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Creating account...",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                "Create account",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign In Section
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = ElectricBlue.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Already have an account?",
                        color = Gray700,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(onClick = onNavigateToLogin) {
                        Text(
                            "Sign in",
                            color = ElectricBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
