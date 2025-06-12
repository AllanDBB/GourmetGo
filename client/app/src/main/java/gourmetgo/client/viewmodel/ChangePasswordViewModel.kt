package gourmetgo.client.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.data.repository.ChangePasswordRepository
import gourmetgo.client.viewmodel.statesUi.ChangePasswordUiState
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val repository: ChangePasswordRepository
) : ViewModel() {

    var uiState by mutableStateOf(ChangePasswordUiState())
        private set

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

    private fun validateForm(): Boolean {
        var isValid = true
        var currentPasswordError: String? = null
        var newPasswordError: String? = null
        var confirmPasswordError: String? = null

        // Validar contraseña actual
        if (uiState.currentPassword.isBlank()) {
            currentPasswordError = "La contraseña actual es requerida"
            isValid = false
        }

        // Validar nueva contraseña
        if (uiState.newPassword.isBlank()) {
            newPasswordError = "La nueva contraseña es requerida"
            isValid = false
        } else if (!isValidPassword(uiState.newPassword)) {
            newPasswordError = "Debe tener 6 letras, 4 números y un punto"
            isValid = false
        }

        // Validar confirmación de contraseña
        if (uiState.confirmPassword.isBlank()) {
            confirmPasswordError = "Confirmar la nueva contraseña es requerido"
            isValid = false
        } else if (uiState.newPassword != uiState.confirmPassword) {
            confirmPasswordError = "Las contraseñas no coinciden"
            isValid = false
        }

        // Validar que la nueva contraseña sea diferente a la actual
        if (uiState.currentPassword == uiState.newPassword && uiState.currentPassword.isNotBlank()) {
            newPasswordError = "La nueva contraseña debe ser diferente a la actual"
            isValid = false
        }

        uiState = uiState.copy(
            currentPasswordError = currentPasswordError,
            newPasswordError = newPasswordError,
            confirmPasswordError = confirmPasswordError,
            isFormValid = isValid
        )

        return isValid
    }

    fun changePassword() {
        if (!validateForm()) return

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                repository.changePassword(
                    currentPassword = uiState.currentPassword,
                    newPassword = uiState.newPassword
                ).onSuccess { message ->
                    uiState = uiState.copy(
                        isLoading = false,
                        isPasswordChanged = true,
                        error = null
                    )
                }.onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        error = error.message ?: "Error desconocido"
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error inesperado: ${e.message}"
                )
            }
        }
    }

    fun updateCurrentPassword(password: String) {
        uiState = uiState.copy(currentPassword = password, currentPasswordError = null)
    }

    fun updateNewPassword(password: String) {
        uiState = uiState.copy(newPassword = password, newPasswordError = null)
    }

    fun updateConfirmPassword(password: String) {
        uiState = uiState.copy(confirmPassword = password, confirmPasswordError = null)
    }

    fun toggleCurrentPasswordVisibility() {
        uiState = uiState.copy(showCurrentPassword = !uiState.showCurrentPassword)
    }

    fun toggleNewPasswordVisibility() {
        uiState = uiState.copy(showNewPassword = !uiState.showNewPassword)
    }

    fun toggleConfirmPasswordVisibility() {
        uiState = uiState.copy(showConfirmPassword = !uiState.showConfirmPassword)
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    fun clearFieldErrors() {
        uiState = uiState.copy(
            currentPasswordError = null,
            newPasswordError = null,
            confirmPasswordError = null
        )
    }

    fun resetState() {
        uiState = ChangePasswordUiState()
    }
}