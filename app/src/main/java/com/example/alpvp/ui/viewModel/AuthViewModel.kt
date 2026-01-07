package com.example.alpvp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alpvp.data.Repository.AuthRepository
import com.example.alpvp.data.Repository.UserRepository
import com.example.alpvp.data.Repository.UserPreferencesRepository
import com.example.alpvp.ui.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import android.util.Log

data class AuthUiState(
    val loading: Boolean = false,
    val token: String? = null,
    val userId: Int? = null,
    val user: UserModel? = null,
    val error: String? = null,
    val initialized: Boolean = false // true once preferences have emitted at least once
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository? = null // optional: used to fetch user profile
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        // Combine token and userId flows so we detect when initial values are loaded
        viewModelScope.launch {
            combine(
                userPreferencesRepository.authTokenFlow,
                userPreferencesRepository.userIdFlow
            ) { token, userId ->
                Pair(token, userId)
            }.collect { (token, userId) ->
                _uiState.value = _uiState.value.copy(
                    token = token,
                    userId = userId,
                    user = userId?.let { UserModel(id = it) },
                    initialized = true
                )
            }
        }
    }

    fun login(email: String, password: String) {
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val data = authRepository.loginUser(email.trim(), password)

                userPreferencesRepository.saveAuth(data.token, data.userId)

                _uiState.value = _uiState.value.copy(
                    loading = false,
                    token = data.token,
                    userId = data.userId,
                    user = UserModel(id = data.userId)
                )
            } catch (t: Throwable) {
                t.printStackTrace()
                Log.e("AuthViewModel", "login error", t)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = t.message ?: "Login failed"
                )
            }
        }
    }

    fun register(name: String, email: String, password: String, height: Int, weight: Int, bmiGoal: Int) {
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val data = authRepository.registerUser(
                    name.trim(),
                    email.trim(),
                    password,
                    height = height,
                    weight = weight,
                    bmiGoal = bmiGoal
                )

                userPreferencesRepository.saveAuth(data.token, data.userId)

                _uiState.value = _uiState.value.copy(
                    loading = false,
                    token = data.token,
                    userId = data.userId,
                    user = UserModel(name = name, email = email)
                )
            } catch (t: Throwable) {
                t.printStackTrace()
                Log.e("AuthViewModel", "register error", t)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = t.message ?: "Registration failed"
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.clearAuth()
            _uiState.value = AuthUiState()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
