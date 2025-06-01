package gourmetgo.client.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.viewmodel.statesUi.AuthUiState
import gourmetgo.client.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    init {
        checkLoginStatus()
    }

    // Función de validación de email
    private fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // Función de validación de contraseña con formato específico
    private fun isValidPassword(password: String): Boolean {
        // Debe tener exactamente 11 caracteres (6 letras + 4 números + 1 punto)
        if (password.length != 11) return false

        // Contar letras, números y puntos
        var letterCount = 0
        var digitCount = 0
        var dotCount = 0

        for (char in password) {
            when {
                char.isLetter() -> letterCount++
                char.isDigit() -> digitCount++
                char == '.' -> dotCount++
                else -> return false // Caracteres no permitidos
            }
        }

        // Verificar que tenga exactamente 6 letras, 4 números y 1 punto
        return letterCount == 6 && digitCount == 4 && dotCount == 1
    }

    // Función para validar las credenciales
    fun validateCredentials(email: String, password: String): Boolean {
        var isValid = true
        var emailError: String? = null
        var passwordError: String? = null

        // Validar email
        when {
            email.isBlank() -> {
                emailError = "El correo electrónico es requerido"
                isValid = false
            }
            !isValidEmail(email) -> {
                emailError = "Formato de correo electrónico inválido"
                isValid = false
            }
        }

        // Validar contraseña con formato específico
        when {
            password.isBlank() -> {
                passwordError = "La contraseña es requerida"
                isValid = false
            }
            password.length != 11 -> {
                passwordError = "La contraseña debe tener exactamente 11 caracteres"
                isValid = false
            }
            !isValidPassword(password) -> {
                passwordError = "La contraseña debe tener 6 letras, 4 números y un punto"
                isValid = false
            }
        }

        // Actualizar el estado con los errores
        uiState = uiState.copy(
            emailError = emailError,
            passwordError = passwordError
        )

        return isValid
    }

    fun login(email: String, password: String) {
        // Primero validar las credenciales
        if (!validateCredentials(email, password)) {
            return // Si no es válido, salir sin hacer la petición
        }

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                repository.login(email, password)
                    .onSuccess { user ->
                        uiState = uiState.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            user = user,
                            error = null,
                            emailError = null,
                            passwordError = null
                        )
                        Log.d("AuthViewModel", "Login successful for user: ${user.name}")
                    }
                    .onFailure { error ->
                        uiState = uiState.copy(
                            isLoading = false,
                            error = error.message ?: "Error desconocido"
                        )
                        Log.e("AuthViewModel", "Login failed", error)
                    }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error inesperado: ${e.message}"
                )
                Log.e("AuthViewModel", "Unexpected error in login", e)
            }
        }
    }

    fun logout() {
        try {
            repository.logout()
            uiState = AuthUiState() // Reset state
            Log.d("AuthViewModel", "Logout successful")
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error in logout", e)
            uiState = uiState.copy(error = "Error al cerrar sesión")
        }
    }

    fun checkLoginStatus() {
        try {
            if (repository.isLoggedIn()) {
                val user = repository.getCurrentUser()
                uiState = uiState.copy(
                    isLoggedIn = true,
                    user = user
                )
                Log.d("AuthViewModel", "User is already logged in: ${user?.name}")
            } else {
                Log.d("AuthViewModel", "User is not logged in")
            }
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error checking login status", e)
            uiState = uiState.copy(error = "Error verificando estado de sesión")
        }
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    fun clearFieldErrors() {
        uiState = uiState.copy(emailError = null, passwordError = null)
    }
}