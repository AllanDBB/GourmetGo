package gourmetgo.client.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.viewmodel.statesUi.RegisterUserUiState
import gourmetgo.client.data.repository.RegisterUserRepository
import kotlinx.coroutines.launch

class RegisterUserViewModel(
    private val repository: RegisterUserRepository
) : ViewModel() {

    var uiState by mutableStateOf(RegisterUserUiState())
        private set

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

    private fun isValidPhone(phone: String): Boolean{
        if (phone.length != 8) return false

        return phone.all { it.isDigit() }
    }

    private fun isValidIddentification(identification: String): Boolean{
        if (identification.length !=9) return false

        return identification.all { it.isDigit() }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        var nameError: String? = null
        var emailError: String? = null
        var phoneError: String? = null
        var identificationError: String? = null
        var passwordError: String? = null
        var photoError: String? = null

        // Validar nombre
        if (uiState.name.isBlank()) {
            nameError = "El nombre es requerido"
            isValid = false
        }

        // Validar email
        if (uiState.email.isBlank()) {
            emailError = "El correo es requerido"
            isValid = false
        } else if (!isValidEmail(uiState.email)) {
            emailError = "Correo inválido"
            isValid = false
        }

        // Validar teléfono
        if (uiState.phone.isBlank()) {
            phoneError = "El teléfono es requerido"
            isValid = false
        } else if (!isValidPhone(uiState.phone)) {
            phoneError = "Debe tener 8 números"
            isValid = false
        }

        // Validar identificación
        if (uiState.identification.isBlank()) {
            identificationError = "La identificación es requerida"
            isValid = false
        } else if (!isValidIddentification(uiState.identification)) {
            identificationError = "Debe tener 9 números"
            isValid = false
        }

        // Validar contraseña
        if (uiState.password.isBlank()) {
            passwordError = "La contraseña es requerida"
            isValid = false
        } else if (!isValidPassword(uiState.password)) {
            passwordError = "Debe tener 6 letras, 4 números y un punto"
            isValid = false
        }

        // Validar foto
        if (uiState.selectedImageUri == null) {
            photoError = "La foto es requerida"
            isValid = false
        }


        uiState = uiState.copy(
            nameError = nameError,
            emailError = emailError,
            phoneError = phoneError,
            identificationError = identificationError,
            passwordError = passwordError,
            photoError = photoError,
            isFormValid = isValid
        )

        return isValid
    }

// En RegisterUserViewModel.kt - función register()

    fun register() {
        if (!validateForm()) return

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                repository.registerUser(
                    name = uiState.name,
                    email = uiState.email,
                    phone = uiState.phone,
                    identification = uiState.identification,
                    password = uiState.password,
                    preferences = uiState.selectedPreferences.toList(),
                    photoUri = uiState.selectedImageUri
                )
                    .onSuccess {
                        uiState = uiState.copy(
                            isLoading = false,
                            isRegistered = true,
                            error = null
                        )
                        Log.d("RegisterUserViewModel", "Usuario registrado con éxito")
                    }
                    .onFailure { error ->
                        uiState = uiState.copy(
                            isLoading = false,
                            error = error.message ?: "Error desconocido"
                        )
                        Log.e("RegisterUserViewModel", "Error al registrar", error)
                    }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error inesperado: ${e.message}"
                )
                Log.e("RegisterUserViewModel", "Excepción inesperada", e)
            }
        }
    }

    fun updateName(name: String) {
        uiState = uiState.copy(name = name, nameError = null)
    }

    fun updateEmail(email: String) {
        uiState = uiState.copy(email = email, emailError = null)
    }

    fun updatePhone(phone: String) {
        uiState = uiState.copy(phone = phone, phoneError = null)
    }

    fun updateIdentification(identification: String) {
        uiState = uiState.copy(identification = identification, identificationError = null)
    }

    fun updatePassword(password: String) {
        uiState = uiState.copy(password = password, passwordError = null)
    }

    fun updateSelectedImage(uri: Uri?) {
        uiState = uiState.copy(selectedImageUri = uri)
    }

    fun updatePreferences(preferences: Set<String>) {
        uiState = uiState.copy(selectedPreferences = preferences)
    }

    fun updateSelectedImageUri(uri: Uri?) {
        uiState = uiState.copy(selectedImageUri = uri, photoError = null)
    }

    fun togglePasswordVisibility() {
        uiState = uiState.copy(showPassword = !uiState.showPassword)
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    fun clearFieldErrors() {
        uiState = uiState.copy(
            nameError = null,
            emailError = null,
            phoneError = null,
            identificationError = null,
            passwordError = null,
            photoError = null,
        )
    }

    fun resetState() {
        uiState = RegisterUserUiState()
    }
}