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

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(error = "Correo y contraseña son requeridos")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                repository.login(email, password)
                    .onSuccess { user ->
                        uiState = uiState.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            client = repository.getCurrentUser(),
                            error = null
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
                val user =  repository.getCurrentUser()
                uiState = uiState.copy(
                    isLoggedIn = true,
                    client = user
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

}