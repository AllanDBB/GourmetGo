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
            uiState = uiState.copy(user = currentUser)
            Log.d("ProfileViewModel", "Current user loaded: ${currentUser?.name}")
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Error loading current user", e)
            uiState = uiState.copy(error = "Error al cargar usuario")
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
            uiState = uiState.copy(error = "Nombre y mail requerido")
            return
        }

        if (!EditProfileUtils.isValidName(name)) {
            uiState = uiState.copy(error = "Solo letras y espacios")
            return
        }

        if (!EditProfileUtils.isValidEmail(email)) {
            uiState = uiState.copy(error = "Mail no valido")
            return
        }

        if (phone.isNotBlank() && !EditProfileUtils.isValidPhone(phone)) {
            uiState = uiState.copy(error = "El telefono deve tener 8 digitos")
            return
        }

        if (identification.isNotBlank() && !EditProfileUtils.isValidDNI(identification)) {
            uiState = uiState.copy(error = "La identificacion debe tener 9 digitos")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(1000)

                val updatedUser = uiState.user?.copy(
                    name = name,
                    email = email,
                    phone = phone,
                    dni = identification,
                    preferences = preferences
                )

                updatedUser?.let { user ->
                    authRepository.updateUserLocally(user)
                    uiState = uiState.copy(
                        isLoading = false,
                        user = user,
                        updateSuccess = true,
                        error = null
                    )
                    Log.d("ProfileViewModel", "Profile updated successfully for: ${user.phone}")
                }

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error updating profile: ${e.message}"
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