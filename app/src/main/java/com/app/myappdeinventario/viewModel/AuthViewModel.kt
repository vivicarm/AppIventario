package com.app.myappdeinventario.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.myappdeinventario.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository = AuthRepository()): ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> get() = _uiState

    // Login
    fun login(email: String, password: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val result = repository.login(email, password)
                _uiState.value = if (result.isSuccess) {
                    AuthUiState.Success
                } else {
                    AuthUiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Error inesperado")
            }
        }
    }

    // Registro
    fun registro(email: String, password: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val result = repository.registro(email, password)
                _uiState.value = if (result.isSuccess) {
                    AuthUiState.Success
                } else {
                    AuthUiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Error inesperado")
            }
        }
    }

    // resetea estado después de un error o éxito
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

//  Estados de la UI
sealed class AuthUiState {
    object Idle : AuthUiState()       // Esperando acción
    object Loading : AuthUiState()    // Procesando
    object Success : AuthUiState()    // Operación correcta
    data class Error(val message: String) : AuthUiState() // Falló algo
}

