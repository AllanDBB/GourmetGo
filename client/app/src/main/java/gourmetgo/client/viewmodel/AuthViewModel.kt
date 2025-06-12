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

import gourmetgo.client.data.models.User
import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.Chef

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    init {
        checkLoginStatus()
    }

    //VALIDACIONES


    private fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isValidPassword(password: String): Boolean {
        if (password.length != 11) return false

        var letterCount = 0
        var digitCount = 0
        var dotCount = 0

        for (char in password) {
            when {
                char.isLetter() -> letterCount++
                char.isDigit() -> digitCount++
                char == '.' -> dotCount++
                else -> return false
            }
        }

        return letterCount == 6 && digitCount == 4 && dotCount == 1
    }

    fun validateCredentials(email: String, password: String): Boolean {
        var isValid = true
        var emailError: String? = null
        var passwordError: String? = null

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

        uiState = uiState.copy(
            emailError = emailError,
            passwordError = passwordError
        )

        return isValid
    }



    fun login(email: String, password: String) {
        if (!validateCredentials(email, password)) {
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                repository.login(email, password)
                    .onSuccess { user ->  // ← CAMBIAR: recibe User
                        // Después del login exitoso, obtener datos específicos
                        when (user.role) {
                            "user" -> {
                                // Obtener datos completos del cliente
                                val client = repository.getCurrentClient()
                                if (client != null) {
                                    uiState = uiState.copy(
                                        isLoading = false,
                                        isLoggedIn = true,
                                        currentUser = client,  // ← Usar Client específico
                                        userType = "user",
                                        error = null,
                                        emailError = null,
                                        passwordError = null
                                    )
                                    Log.d("AuthViewModel", "Login successful for user: ${client.name}")
                                } else {
                                    uiState = uiState.copy(
                                        isLoading = false,
                                        error = "Error obteniendo datos del usuario"
                                    )
                                }
                            }
                            "chef" -> {
                                // Obtener datos completos del chef
                                val chef = repository.getCurrentChef()
                                if (chef != null) {
                                    uiState = uiState.copy(
                                        isLoading = false,
                                        isLoggedIn = true,
                                        currentUser = chef,  // ← Usar Chef específico
                                        userType = "chef",
                                        error = null,
                                        emailError = null,
                                        passwordError = null
                                    )
                                    Log.d("AuthViewModel", "Login successful for chef: ${chef.name}")
                                } else {
                                    uiState = uiState.copy(
                                        isLoading = false,
                                        error = "Error obteniendo datos del chef"
                                    )
                                }
                            }
                            else -> {
                                uiState = uiState.copy(
                                    isLoading = false,
                                    error = "Tipo de usuario no reconocido: ${user.role}"
                                )
                            }
                        }
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
                // Intentar obtener Client primero
                val client = repository.getCurrentClient()
                if (client != null) {
                    uiState = uiState.copy(
                        isLoggedIn = true,
                        currentUser = client,
                        userType = "user"
                    )
                    Log.d("AuthViewModel", "User session restored: ${client.name}")
                    return
                }

                // Si no hay Client, intentar Chef
                val chef = repository.getCurrentChef()
                if (chef != null) {
                    uiState = uiState.copy(
                        isLoggedIn = true,
                        currentUser = chef,
                        userType = "chef"
                    )
                    Log.d("AuthViewModel", "Chef session restored: ${chef.name}")
                    return
                }

                // Si no hay ninguno, logout
                repository.logout()
                Log.d("AuthViewModel", "No user data found, logging out")
            } else {
                Log.d("AuthViewModel", "No active session found")
            }
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error checking login status", e)
            uiState = uiState.copy(error = "Error verificando estado de sesión")
        }
    }

    fun getCurrentClient(): Client? {
        return uiState.currentUser as? Client
    }

    fun getCurrentChef(): Chef? {
        return uiState.currentUser as? Chef
    }


    fun isUserClient(): Boolean {
        return uiState.userType == "user"
    }


    fun isUserChef(): Boolean {
        return uiState.userType == "chef"
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    fun clearFieldErrors() {
        uiState = uiState.copy(emailError = null, passwordError = null)
    }
}