package gourmetgo.client.viewmodel.statesUi

import android.net.Uri

data class RegisterChefUiState(
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val error: String? = null,
    val isFormValid: Boolean = false,
    val name: String = "",                    // Nombre del restaurante/chef
    val contactPerson: String = "",           // Persona de contacto
    val email: String = "",                   // Email
    val phone: String = "",                   // Teléfono
    val location: String = "",                // Ubicación
    val cuisinetype: String = "",             // Tipo de cocina
    val password: String = "",                // Contraseña
    val bio: String = "",                     // Biografía/descripción
    val experience: String = "",              // Años de experiencia
    val sociallinks: List<String> = emptyList(), // Enlaces sociales
    val selectedImageUri: Uri? = null,        // Foto de perfil
    val showPassword: Boolean = false,
    val nameError: String? = null,
    val contactPersonError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val locationError: String? = null,
    val cuisinetypeError: String? = null,
    val passwordError: String? = null,
    val bioError: String? = null,
    val experienceError: String? = null,
    val photoError: String? = null
)