package gourmetgo.client.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.viewmodel.statesUi.AuthUiState
import gourmetgo.client.data.repository.AuthRepository
import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.Chef
import kotlinx.coroutines.launch

/**
 * AuthViewModel - ViewModel de autenticación unificado
 * 
 * Maneja login tanto para usuarios normales como chefs.
 * Detecta automáticamente el tipo de usuario según la respuesta de la API.
 */
class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    init {
        checkLoginStatus()
    }

    // ========== VALIDACIONES ==========

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

    // ========== LOGIN UNIFICADO ==========

    /**
     * Login unificado - maneja usuarios y chefs automáticamente
     * 
     * El repository detecta el tipo de usuario según el campo "role"
     * de la respuesta de la API y devuelve Client o Chef correspondiente.
     */
    fun login(email: String, password: String) {
        if (!validateCredentials(email, password)) {
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                repository.login(email, password)
                    .onSuccess { result ->
                        // El repository devuelve Client o Chef según el role
                        when (result) {
                            is Client -> {
                                uiState = uiState.copy(
                                    isLoading = false,
                                    isLoggedIn = true,
                                    currentUser = result,
                                    userType = "user",
                                    error = null,
                                    emailError = null,
                                    passwordError = null
                                )
                                Log.d("AuthViewModel", "Login successful for user: ${result.name}")
                            }
                            is Chef -> {
                                uiState = uiState.copy(
                                    isLoading = false,
                                    isLoggedIn = true,
                                    currentUser = result,
                                    userType = "chef",
                                    error = null,
                                    emailError = null,
                                    passwordError = null
                                )
                                Log.d("AuthViewModel", "Login successful for chef: ${result.name}")
                            }
                            else -> {
                                uiState = uiState.copy(
                                    isLoading = false,
                                    error = "Tipo de usuario no reconocido"
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

    // ========== GESTIÓN DE SESIÓN ==========

    fun logout() {
        try {
            repository.logout()
            uiState = AuthUiState() // Reset completo
            Log.d("AuthViewModel", "Logout successful")
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error in logout", e)
            uiState = uiState.copy(error = "Error al cerrar sesión")
        }
    }

    /**
     * Verifica estado de login usando la nueva función del repository
     * 
     * Usa repository.checkLoginStatus() que devuelve Result<Any?>
     */
    fun checkLoginStatus() {
        viewModelScope.launch {
            try {
                repository.checkLoginStatus()
                    .onSuccess { result ->
                        if (result != null) {
                            // Hay sesión activa
                            when (result) {
                                is Client -> {
                                    uiState = uiState.copy(
                                        isLoggedIn = true,
                                        currentUser = result,
                                        userType = "user"
                                    )
                                    Log.d("AuthViewModel", "User session restored: ${result.name}")
                                }
                                is Chef -> {
                                    uiState = uiState.copy(
                                        isLoggedIn = true,
                                        currentUser = result,
                                        userType = "chef"
                                    )
                                    Log.d("AuthViewModel", "Chef session restored: ${result.name}")
                                }
                            }
                        } else {
                            // No hay sesión activa
                            Log.d("AuthViewModel", "No active session found")
                        }
                    }
                    .onFailure { error ->
                        Log.e("AuthViewModel", "Error checking login status", error)
                        uiState = uiState.copy(error = "Error verificando estado de sesión")
                    }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Unexpected error checking login status", e)
                uiState = uiState.copy(error = "Error verificando estado de sesión")
            }
        }
    }

    // ========== HELPERS ==========

    /**
     * Obtiene el usuario actual como Client (solo si es usuario normal)
     */
    fun getCurrentClient(): Client? {
        return uiState.currentUser as? Client
    }

    /**
     * Obtiene el usuario actual como Chef (solo si es chef)
     */
    fun getCurrentChef(): Chef? {
        return uiState.currentUser as? Chef
    }

    /**
     * Verifica si el usuario actual es un cliente normal
     */
    fun isUserClient(): Boolean {
        return uiState.userType == "user"
    }

    /**
     * Verifica si el usuario actual es un chef
     */
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