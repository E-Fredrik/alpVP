package com.example.alpvp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alpvp.data.repository.UserRepository
import com.example.alpvp.data.repository.AuthRepository
import com.example.alpvp.ui.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.alpvp.data.dto.RegisterUserRequest

data class AuthUiState(
    val loading: Boolean = false,
    val token: String? = null,
    val user: UserModel? = null,
    val error: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository? = null // optional: used to fetch full profile after login
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                // call authRepository.loginUser which expects a UserLoginRequest; map response to token
                val request = com.example.alpvp.data.dto.UserLoginRequest(email = email.trim(), password = password)
                val response = authRepository.loginUser(request)
                val token = response.body()?.data?.token
                // optionally fetch full user profile if userRepository is provided
                val user = if (userRepository != null) {
                    try {
                        // if backend requires an Int userId, we can't fetch here without it; keep minimal user
                        UserModel(email = email)
                    } catch (_: Exception) {
                        UserModel(email = email)
                    }
                } else {
                    UserModel(email = email)
                }

                _uiState.value = _uiState.value.copy(
                    loading = false,
                    token = token,
                    user = user
                )
            } catch (t: Throwable) {
                _uiState.value = _uiState.value.copy(loading = false, error = t.message ?: "Login failed")
            }
        }
    }

    fun register(name: String, email: String, password: String, height: Int, weight: Int, bmiGoal: Int) {
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val req = RegisterUserRequest(
                    bmiGoal = bmiGoal,
                    email = email.trim(),
                    height = height,
                    password = password,
                    username = name.trim(),
                    weight = weight
                )
                val response = authRepository.registerUser(req)
                val token = response.body()?.data?.token
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    token = token,
                    user = UserModel(name = name, email = email)
                )
            } catch (t: Throwable) {
                _uiState.value = _uiState.value.copy(loading = false, error = t.message ?: "Registration failed")
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
