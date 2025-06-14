package gourmetgo.client.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import gourmetgo.client.viewmodel.statesUi.AuthUiState
import gourmetgo.client.data.repository.AuthRepository
import gourmetgo.client.data.models.User
import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.Chef
import java.util.regex.Pattern

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    init {
        checkLoginStatus()
    }

    //VALIDACIONES

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
        )
        return emailPattern.matcher(email).matches()
    }

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
                    .onSuccess { user ->  
                        // Después del login exitoso, obtener datos específicos
                        when (user.role) {
                            "user" -> {
                                val client = repository.getCurrentClient()
                                if (client != null) {
                                    uiState = uiState.copy(
                                        isLoading = false,
                                        isLoggedIn = true,
                                        currentUser = client,
                                        userType = "user",
                                        error = null,
                                        emailError = null,
                                        passwordError = null
                                    )
                                } else {
                                    uiState = uiState.copy(
                                        isLoading = false,
                                        error = "Error obteniendo datos del usuario"
                                    )
                                }
                            }
                            "chef" -> {
                                val chef = repository.getCurrentChef()
                                if (chef != null) {
                                    uiState = uiState.copy(
                                        isLoading = false,
                                        isLoggedIn = true,
                                        currentUser = chef,
                                        userType = "chef",
                                        error = null,
                                        emailError = null,
                                        passwordError = null
                                    )
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
                                    error = "Tipo de usuario no válido"
                                )
                            }
                        }
                    }
                    .onFailure { throwable ->
                        uiState = uiState.copy(
                            isLoading = false,
                            error = throwable.message ?: "Error desconocido en el login"
                        )
                    }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "Error de conexión"
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repository.logout()
                uiState = AuthUiState()
            } catch (e: Exception) {
                // Error silencioso en logout
            }
        }
    }

    fun checkLoginStatus() {
        try {
            if (repository.hasActiveSession()) {
                val client = repository.getCurrentClient()
                if (client != null) {
                    uiState = uiState.copy(
                        isLoggedIn = true,
                        currentUser = client,
                        userType = "user"
                    )
                    return
                }

                val chef = repository.getCurrentChef()
                if (chef != null) {
                    uiState = uiState.copy(
                        isLoggedIn = true,
                        currentUser = chef,
                        userType = "chef"
                    )
                    return
                }

                // No hay datos de usuario, logout
                repository.logout()
                uiState = AuthUiState()
            } else {
                uiState = AuthUiState()
            }
        } catch (e: Exception) {
            // Error silencioso, mantener estado por defecto
            uiState = AuthUiState()
        }
    }

    fun getCurrentClient(): Client? {
        return uiState.currentUser as? Client
    }

    fun getCurrentChef(): Chef? {
        return uiState.currentUser as? Chef
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    fun clearEmailError() {
        uiState = uiState.copy(emailError = null)
    }

    fun clearPasswordError() {
        uiState = uiState.copy(passwordError = null)
    }

    fun clearFieldErrors() {
        uiState = uiState.copy(
            emailError = null,
            passwordError = null
        )
    }

    fun refreshUserData() {
        viewModelScope.launch {
            try {
                when (uiState.userType) {
                    "chef" -> {
                        val chef = repository.getCurrentChef()
                        if (chef != null) {
                            uiState = uiState.copy(currentUser = chef)
                        }
                    }
                    "user" -> {
                        val client = repository.getCurrentClient()
                        if (client != null) {
                            uiState = uiState.copy(currentUser = client)
                        }
                    }
                }
            } catch (e: Exception) {
                // Error silencioso
            }
        }
    }
}