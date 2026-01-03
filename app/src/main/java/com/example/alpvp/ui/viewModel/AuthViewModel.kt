package com.example.alpvp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alpvp.data.Repository.UserRepository
import com.example.alpvp.ui.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val loading: Boolean = false,
    val token: String? = null,
    val user: UserModel? = null,
    val error: String? = null
)

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val data = userRepository.loginUser(email.trim(), password)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    token = data.token,
                    user = UserModel(email = email) // minimal; replace when user endpoint available
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
                val data = userRepository.registerUser(
                    name.trim(), email.trim(), password,
                    height = height,
                    weight = weight,
                    bmiGoal = bmiGoal
                )
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    token = data.token,
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
