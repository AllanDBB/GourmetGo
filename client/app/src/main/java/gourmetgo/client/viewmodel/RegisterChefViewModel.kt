package gourmetgo.client.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.viewmodel.statesUi.RegisterChefUiState
import gourmetgo.client.data.repository.RegisterChefRepository
import kotlinx.coroutines.launch

class RegisterChefViewModel(
    private val repository: RegisterChefRepository
) : ViewModel() {

    var uiState by mutableStateOf(RegisterChefUiState())
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

    private fun isValidPhone(phone: String): Boolean {
        if (phone.length != 8) return false
        return phone.all { it.isDigit() }
    }

    private fun isValidExperience(experience: String): Boolean {
        // Debe ser un número seguido de "años" o solo números
        return experience.matches(Regex("^\\d+\\s*(años?|año)?$")) || experience.all { it.isDigit() }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        var nameError: String? = null
        var contactPersonError: String? = null
        var emailError: String? = null
        var phoneError: String? = null
        var locationError: String? = null
        var cuisinetypeError: String? = null
        var passwordError: String? = null
        var bioError: String? = null
        var experienceError: String? = null
        var photoError: String? = null

        // Validar nombre del restaurante/chef
        if (uiState.name.isBlank()) {
            nameError = "El nombre del restaurante/chef es requerido"
            isValid = false
        }

        // Validar persona de contacto
        if (uiState.contactPerson.isBlank()) {
            contactPersonError = "La persona de contacto es requerida"
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

        // Validar ubicación
        if (uiState.location.isBlank()) {
            locationError = "La ubicación es requerida"
            isValid = false
        }

        // Validar tipo de cocina
        if (uiState.cuisinetype.isBlank()) {
            cuisinetypeError = "El tipo de cocina es requerido"
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

        // Validar biografía
        if (uiState.bio.isBlank()) {
            bioError = "La biografía es requerida"
            isValid = false
        } else if (uiState.bio.length < 50) {
            bioError = "La biografía debe tener al menos 50 caracteres"
            isValid = false
        }

        // Validar experiencia
        if (uiState.experience.isBlank()) {
            experienceError = "Los años de experiencia son requeridos"
            isValid = false
        } else if (!isValidExperience(uiState.experience)) {
            experienceError = "Formato inválido (ej: '15 años' o '15')"
            isValid = false
        }

        // Validar foto
        if (uiState.selectedImageUri == null) {
            photoError = "La foto de perfil es requerida"
            isValid = false
        }

        uiState = uiState.copy(
            nameError = nameError,
            contactPersonError = contactPersonError,
            emailError = emailError,
            phoneError = phoneError,
            locationError = locationError,
            cuisinetypeError = cuisinetypeError,
            passwordError = passwordError,
            bioError = bioError,
            experienceError = experienceError,
            photoError = photoError,
            isFormValid = isValid
        )

        return isValid
    }

    fun register() {
        if (!validateForm()) return

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                repository.registerChef(
                    name = uiState.name,
                    contactPerson = uiState.contactPerson,
                    email = uiState.email,
                    phone = uiState.phone,
                    location = uiState.location,
                    cuisineType = uiState.cuisinetype,
                    password = uiState.password,
                    bio = uiState.bio,
                    experience = uiState.experience,
                    socialLinks = uiState.sociallinks,
                    photoUri = uiState.selectedImageUri 
                )
                    .onSuccess {
                        uiState = uiState.copy(
                            isLoading = false,
                            isRegistered = true,
                            error = null
                        )
                        Log.d("RegisterChefViewModel", "Chef registrado con éxito")
                    }
                    .onFailure { error ->
                        uiState = uiState.copy(
                            isLoading = false,
                            error = error.message ?: "Error desconocido"
                        )
                        Log.e("RegisterChefViewModel", "Error al registrar", error)
                    }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error inesperado: ${e.message}"
                )
                Log.e("RegisterChefViewModel", "Excepción inesperada", e)
            }
        }
    }

    // Funciones de actualización de campos
    fun updateName(name: String) {
        uiState = uiState.copy(name = name, nameError = null)
    }

    fun updateContactPerson(contactPerson: String) {
        uiState = uiState.copy(contactPerson = contactPerson, contactPersonError = null)
    }

    fun updateEmail(email: String) {
        uiState = uiState.copy(email = email, emailError = null)
    }

    fun updatePhone(phone: String) {
        uiState = uiState.copy(phone = phone, phoneError = null)
    }

    fun updateLocation(location: String) {
        uiState = uiState.copy(location = location, locationError = null)
    }

    fun updateCuisineType(cuisinetype: String) {
        uiState = uiState.copy(cuisinetype = cuisinetype, cuisinetypeError = null)
    }

    fun updatePassword(password: String) {
        uiState = uiState.copy(password = password, passwordError = null)
    }

    fun updateBio(bio: String) {
        uiState = uiState.copy(bio = bio, bioError = null)
    }

    fun updateExperience(experience: String) {
        uiState = uiState.copy(experience = experience, experienceError = null)
    }

    fun updateSocialLinks(sociallinks: List<String>) {
        uiState = uiState.copy(sociallinks = sociallinks)
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
            contactPersonError = null,
            emailError = null,
            phoneError = null,
            locationError = null,
            cuisinetypeError = null,
            passwordError = null,
            bioError = null,
            experienceError = null,
            photoError = null
        )
    }

    fun resetState() {
        uiState = RegisterChefUiState()
    }
}