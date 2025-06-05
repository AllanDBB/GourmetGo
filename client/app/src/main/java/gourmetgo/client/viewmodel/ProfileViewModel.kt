package gourmetgo.client.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.data.repository.AuthRepository
import gourmetgo.client.utils.EditProfileUtils
import gourmetgo.client.viewmodel.statesUi.ProfileUiState
import kotlinx.coroutines.launch
import gourmetgo.client.enums.Preferences

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        try {
            val currentUser = authRepository.getCurrentUser()
            uiState = uiState.copy(client = currentUser)
            Log.d("ProfileViewModel", "Current user loaded: ${currentUser?.name}")
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Error loading current user", e)
            uiState = uiState.copy(error = "Error cargando perfil de usuario")
        }
    }

    fun updateProfile(
        name: String,
        email: String,
        phone: String,
        identification: String,
        preferences: List<String>
    ) {
        if (name.isBlank() || email.isBlank()) {
            uiState = uiState.copy(error = "Nombre y correo son obligatorios")
            return
        }

        if (!EditProfileUtils.isValidName(name)) {
            uiState = uiState.copy(error = "El nombre solo puede contener letras y espacios")
            return
        }

        if (!EditProfileUtils.isValidEmail(email)) {
            uiState = uiState.copy(error = "Formato de correo electrónico inválido")
            return
        }

        if (phone.isNotBlank() && !EditProfileUtils.isValidPhone(phone)) {
            uiState = uiState.copy(error = "El teléfono debe tener 8 dígitos")
            return
        }

        if (identification.isNotBlank() && !EditProfileUtils.isValidDNI(identification)) {
            uiState = uiState.copy(error = "La cédula debe tener 9 dígitos")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {

                val updatedUser = uiState.client?.copy(
                    name = name,
                    email = email,
                    phone = phone,
                    dni = identification,
                    preferences = preferences
                )

                updatedUser?.let { user ->
                    authRepository.updateUser(user)
                    uiState = uiState.copy(
                        isLoading = false,
                        client = user,
                        updateSuccess = true,
                        error = null
                    )
                    Log.d("ProfileViewModel", "Profile updated successfully for: ${user.name}")
                }

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error actualizando perfil: ${e.message}"
                )
                Log.e("ProfileViewModel", "Error updating profile", e)
            }
        }
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    fun clearUpdateSuccess() {
        uiState = uiState.copy(updateSuccess = false)
    }

    fun getPreferences(): List<String> {
        return Preferences.entries.map { it.toString() }
    }
}