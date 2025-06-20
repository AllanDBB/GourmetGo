package gourmetgo.client.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.AppConfig
import gourmetgo.client.data.repository.AuthRepository
import gourmetgo.client.utils.EditProfileUtils
import gourmetgo.client.viewmodel.statesUi.ProfileUiState
import kotlinx.coroutines.launch
import gourmetgo.client.enums.Preferences
import gourmetgo.client.utils.ImageUploadUtils

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val imageUploadUseCase: ImageUploadUtils
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        try {
            if (AppConfig.ENABLE_LOGGING) {
                Log.w("ProfileViewModel", "Loading current user data")
            }

            val currentClient = authRepository.getCurrentClient()
            val currentChef = authRepository.getCurrentChef()

            when {
                currentClient != null -> {
                    uiState = uiState.copy(
                        client = currentClient,
                        chef = null,
                        isLoading = false,
                        error = null
                    )
                    if (AppConfig.ENABLE_LOGGING) {
                        Log.d("ProfileViewModel", "Current client loaded: ${currentClient.name}")
                    }
                }
                currentChef != null -> {
                    uiState = uiState.copy(
                        chef = currentChef,
                        client = null,
                        isLoading = false,
                        error = null
                    )
                    if (AppConfig.ENABLE_LOGGING) {
                        Log.d("ProfileViewModel", "Current chef loaded: ${currentChef.name}")
                    }
                }
                else -> {
                    if (AppConfig.ENABLE_LOGGING) {
                        Log.e("ProfileViewModel", "No user data found")
                    }
                    uiState = uiState.copy(
                        error = "No se encontraron datos del usuario",
                        isLoading = false
                    )
                }
            }
        } catch (e: Exception) {
            if (AppConfig.ENABLE_LOGGING) {
                Log.e("ProfileViewModel", "Error loading current user", e)
            }
            uiState = uiState.copy(
                error = "Error cargando perfil de usuario",
                isLoading = false
            )
        }
    }

    fun uploadProfileImage(imageUri: Uri) {
        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val uploadResult = if (isChef()) {
                    imageUploadUseCase.uploadChefImage(imageUri)
                } else {
                    imageUploadUseCase.uploadUserImage(imageUri)
                }

                uploadResult
                    .onSuccess { imageUrl ->
                        if (isClient()) {
                            val currentClient = uiState.client?.copy(avatar = imageUrl)
                            uiState = uiState.copy(
                                client = currentClient,
                                isLoading = false
                            )
                        } else if (isChef()) {
                            val currentChef = uiState.chef?.copy(avatar = imageUrl)
                            uiState = uiState.copy(
                                chef = currentChef,
                                isLoading = false
                            )
                        }

                        if (AppConfig.ENABLE_LOGGING) {
                            Log.d("ProfileViewModel", "Image uploaded successfully: $imageUrl")
                        }
                    }
                    .onFailure { error ->
                        uiState = uiState.copy(
                            isLoading = false,
                            error = error.message ?: "Error subiendo imagen"
                        )
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.e("ProfileViewModel", "Error uploading image", error)
                        }
                    }
            } catch (e: Exception) {
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("ProfileViewModel", "Error in uploadProfileImage", e)
                }
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error subiendo imagen: ${e.message}"
                )
            }
        }
    }

    fun updateClientProfile(
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

        val currentClient = uiState.client
        if (currentClient == null) {
            uiState = uiState.copy(error = "No se encontraron datos del cliente")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val updatedClient = currentClient.copy(
                    name = name,
                    email = email,
                    phone = phone,
                    identification = identification,
                    preferences = preferences
                )

                authRepository.updateClientProfile(updatedClient)
                    .onSuccess { client ->
                        uiState = uiState.copy(
                            isLoading = false,
                            client = client,
                            updateSuccess = true,
                            error = null
                        )
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.d("ProfileViewModel", "Client profile updated successfully: ${client.name}")
                        }
                    }
                    .onFailure { error ->
                        uiState = uiState.copy(
                            isLoading = false,
                            error = error.message ?: "Error actualizando perfil de cliente"
                        )
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.e("ProfileViewModel", "Error updating client profile", error)
                        }
                    }
            } catch (e: Exception) {
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("ProfileViewModel", "Error in updateClientProfile", e)
                }
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error actualizando perfil: ${e.message}"
                )
            }
        }
    }

    fun updateChefProfile(
        name: String,
        email: String,
        phone: String,
        contactPerson: String,
        location: String,
        cuisineTypes: List<String>
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

        if (cuisineTypes.isEmpty()) {
            uiState = uiState.copy(error = "Debe seleccionar al menos un tipo de cocina")
            return
        }

        val currentChef = uiState.chef
        if (currentChef == null) {
            uiState = uiState.copy(error = "No se encontraron datos del chef")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val updatedChef = currentChef.copy(
                    name = name,
                    email = email,
                    phone = phone,
                    contactPerson = contactPerson,
                    location = location,
                    preferences = cuisineTypes
                )

                authRepository.updateChefProfile(updatedChef)
                    .onSuccess { chef ->
                        uiState = uiState.copy(
                            isLoading = false,
                            chef = chef,
                            updateSuccess = true,
                            error = null
                        )
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.d("ProfileViewModel", "Chef profile updated successfully: ${chef.name}")
                        }
                    }
                    .onFailure { error ->
                        uiState = uiState.copy(
                            isLoading = false,
                            error = "Error actualizando perfil de chef"
                        )
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.e("ProfileViewModel", "Error updating chef profile", error)
                        }
                    }
            } catch (e: Exception) {
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("ProfileViewModel", "Error in updateChefProfile", e)
                }
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error actualizando perfil: ${e.message}"
                )
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

    fun isChef(): Boolean {
        return uiState.chef != null
    }

    fun isClient(): Boolean {
        return uiState.client != null
    }

    fun getCurrentImageUrl(): String? {
        return when {
            isClient() -> uiState.client?.avatar
            isChef() -> uiState.chef?.avatar
            else -> null
        }
    }
}