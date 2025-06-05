package gourmetgo.client.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.data.repository.AuthRepository
import gourmetgo.client.utils.PhoneUtils
import gourmetgo.client.viewmodel.statesUi.ProfileUiState
import kotlinx.coroutines.launch
import  gourmetgo.client.enums.Preferences

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
            uiState = uiState.copy(error = "Error al cargar perfil de usuario")
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

        if (!isValidEmail(email)) {
            uiState = uiState.copy(error = "Formato de correo electrónico inválido")
            return
        }

        if (phone.isNotBlank() && !isValidPhone(phone)) {
            uiState = uiState.copy(error = "El número telefónico debe tener 8 dígitos")
            return
        }

        if (identification.isNotBlank() && !isValidIdentification(identification)) {
            uiState = uiState.copy(error = "La identificación debe tener 9 dígitos")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // TODO: Implement actual API call for profile update
                // For now, simulate API call
                kotlinx.coroutines.delay(1000)

                // Update local user data
                val updatedUser = uiState.user?.copy(
                    name = name,
                    email = email,
                    phone = phone,
                    preferences = preferences
                )

                // Save updated user locally
                updatedUser?.let { user ->
                    authRepository.updateUserLocally(user)
                    uiState = uiState.copy(
                        isLoading = false,
                        user = user,
                        updateSuccess = true,
                        error = null
                    )
                    Log.d("ProfileViewModel", "Profile updated successfully for: ${user.name}")
                }

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error al actualizar perfil: ${e.message}"
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
        val prefs = Preferences.entries
        val str = mutableListOf<String>()

        for (pref in prefs) {
            str.add(pref.toString())
        }

        return str
    }



    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPhone(phone: String): Boolean {
        return PhoneUtils.isValidPhone(phone)
    }

    private fun isValidIdentification(identification: String): Boolean {
        val cleanId = identification.replace("[-\\s]".toRegex(), "")
        return cleanId.length == 9 && cleanId.all { it.isDigit() }
    }



}