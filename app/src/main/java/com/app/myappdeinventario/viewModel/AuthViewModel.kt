package com.app.myappdeinventario.viewModel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.myappdeinventario.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository = AuthRepository()): ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> get() = _uiState


    fun login(email: String, password: String) {

        // validaciones

        val validation = validateLoginFields(email, password)
        if (validation != null) {
            _uiState.value = AuthUiState.Error(validation)
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val result = repository.login(email, password)
                _uiState.value = if (result.isSuccess) {
                    AuthUiState.Success("Inicio de sesión exitoso ")
                } else {
                    val message = mapFirebaseError(result.exceptionOrNull()?.message)
                    AuthUiState.Error(message)
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(mapFirebaseError(e.message))
            }
        }
    }

    fun registro(nombre: String, apellido: String, email: String, password: String) {

        // validaciones

        val validation = validateRegisterFields(nombre, apellido, email, password)
        if (validation != null) {
            _uiState.value = AuthUiState.Error(validation)
            return
        }

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val result = repository.registro(nombre, apellido, email, password)
                _uiState.value = if (result.isSuccess) {
                    AuthUiState.Success("Cuenta creada correctamente ✅")
                } else {
                    val message = mapFirebaseError(result.exceptionOrNull()?.message)
                    AuthUiState.Error(message)
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(mapFirebaseError(e.message))
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    // validaciones

    private fun validateLoginFields(email: String, password: String): String? {
        return when {
            email.isBlank() -> "El correo no puede estar vacío"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "El correo no es válido"
            password.isBlank() -> "La contraseña no puede estar vacía"
            else -> null
        }
    }

    private fun validateRegisterFields(nombre: String, apellido: String, email: String, password: String): String? {
        return when {
            nombre.isBlank() -> "El nombre es obligatorio"
            apellido.isBlank() -> "El apellido es obligatorio"
            email.isBlank() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "El correo no es válido"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    //mensajes de error mas comues de firebase
    private fun mapFirebaseError(rawMessage: String?): String {
        val msg = rawMessage ?: return "Error desconocido"

        return when {
            msg.contains("password is invalid", true) -> "Contraseña incorrecta"
            msg.contains("no user record", true) -> "Usuario no encontrado"
            msg.contains("already in use", true) -> "Este correo ya está registrado"
            msg.contains("network error", true) -> "Error de conexión. Verifica tu internet"
            msg.contains("WEAK_PASSWORD", true) -> "La contraseña es demasiado débil"
            else -> msg
        }
    }
}

// estados ui
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val message: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
