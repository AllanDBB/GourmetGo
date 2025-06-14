package gourmetgo.client.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.AppConfig
import gourmetgo.client.data.repository.CreateExperienceRepository
import gourmetgo.client.data.models.dtos.Menu
import gourmetgo.client.data.models.dtos.CreateExperienceRequest
import gourmetgo.client.utils.EditProfileUtils
import gourmetgo.client.viewmodel.statesUi.CreateExperienceUiState
import kotlinx.coroutines.launch
import gourmetgo.client.enums.Preferences
import gourmetgo.client.utils.ImageUploadUtils
import gourmetgo.client.data.remote.CloudinaryService

class CreateExperienceViewModel(
    private val repository: CreateExperienceRepository,
    private val cloudinaryService: CloudinaryService
) : ViewModel() {

    var uiState by mutableStateOf(CreateExperienceUiState())
        private set

    private fun isValidUrl(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://")
    }

    private fun isDateInFutureOrToday(dateString: String): Boolean {
        // dateString expected format: yyyy-MM-ddTHH:mm:ssZ or yyyy-MM-dd
        return try {
            val datePart = dateString.split("T")[0]
            val parts = datePart.split("-")
            if (parts.size != 3) return false
            val year = parts[0].toInt()
            val month = parts[1].toInt() - 1 // Calendar months are 0-based
            val day = parts[2].toInt()
            val calendar = java.util.Calendar.getInstance()
            val today = java.util.Calendar.getInstance()
            calendar.set(year, month, day, 0, 0, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            today.set(java.util.Calendar.HOUR_OF_DAY, 0)
            today.set(java.util.Calendar.MINUTE, 0)
            today.set(java.util.Calendar.SECOND, 0)
            today.set(java.util.Calendar.MILLISECOND, 0)
            !calendar.before(today)
        } catch (e: Exception) {
            false
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (uiState.title.isBlank()) {
            uiState = uiState.copy(error = "El nombre no puede estar vacío")
            isValid = false
        }
        if (uiState.description.isBlank()) {
            uiState = uiState.copy(error = "La descripción no puede estar vacía")
            isValid = false
        } else if (uiState.description.length < 20) {
            uiState = uiState.copy(error = "La descripción debe tener al menos 20 caracteres")
            isValid = false
        }
        if (uiState.date.isBlank()) {
            uiState = uiState.copy(error = "La fecha no puede estar vacía")
            isValid = false
        } else if (!isDateInFutureOrToday(uiState.date)) {
            uiState = uiState.copy(error = "La fecha no puede ser anterior al día actual")
            isValid = false
        }
        if (uiState.duration <= 0) {
            uiState = uiState.copy(error = "La duración debe ser mayor que cero")
            isValid = false
        }
        if (uiState.price <= 0) {
            uiState = uiState.copy(error = "El precio debe ser mayor que cero")
            isValid = false
        }
        if (uiState.capacity <= 0) {
            uiState = uiState.copy(error = "La capacidad debe ser mayor que cero")
            isValid = false
        }
        if (uiState.location.isBlank()) {
            uiState = uiState.copy(error = "La ubicación no puede estar vacía")
            isValid = false
        } else if (!isValidUrl(uiState.location)) {
            uiState = uiState.copy(error = "La ubicación debe ser un enlace válido (http o https)")
            isValid = false
        }
        if ((uiState.image == null) && uiState.text.isBlank()) {
            uiState = uiState.copy(error = "El menú debe estar en texto o imagen o ambos")
            isValid = false
        } else if (uiState.text.isNotBlank() && uiState.text.length < 10) {
            uiState = uiState.copy(error = "El menú en texto debe tener al menos 10 caracteres")
            isValid = false
        }
        if (uiState.status != "Activa" && uiState.status != "Próximamente") {
            uiState = uiState.copy(error = "El estado debe ser 'Activa' o 'Próximamente'")
            isValid = false
        }
        return isValid
    }


    fun createExperience() {
        if (!validateForm()) return

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // Subir imágenes a Cloudinary y obtener URLs
                val imageUrls = mutableListOf<String>()
                for (imgUri in uiState.images) {
                    try {
                        val url = cloudinaryService.uploadExperienceImage(imgUri).getOrThrow()
                        imageUrls.add(url)
                    } catch (e: Exception) {
                        uiState = uiState.copy(isLoading = false, error = "Error al subir imagen: ${e.message}")
                        return@launch
                    }
                }
                // Subir imagen de menú si existe
                val menuUri = uiState.image
                val menuImageUrl = if (menuUri != null) {
                    try {
                        cloudinaryService.uploadExperienceImage(menuUri).getOrThrow()
                    } catch (e: Exception) {
                        uiState = uiState.copy(isLoading = false, error = "Error al subir imagen de menú: ${e.message}")
                        return@launch
                    }
                } else ""

                repository.createExperience(
                    uiState.title,
                    uiState.description,
                    uiState.date,
                    uiState.location,
                    uiState.capacity,
                    uiState.price,
                    uiState.duration,
                    uiState.category,
                    imageUrls,
                    uiState.requirements,
                    uiState.status,
                    menuImageUrl,
                    uiState.text
                )
                uiState = uiState.copy(isLoading = false, createSuccess = true)
            } catch (e: Exception) {
                Log.e("CreateExperienceViewModel", "Error creating experience", e)
                uiState = uiState.copy(isLoading = false, error = e.message ?: "Unknown error")
            }
        }
    }

    fun updateTitle(title: String) {
        uiState = uiState.copy(title = title)
    }
    fun updateDescription(description: String) {
        uiState = uiState.copy(description = description)
    }
    fun updateDate(date: String) {
        uiState = uiState.copy(date = date)
    }
    fun updateTime(time: String) {
        uiState = uiState.copy(date = time)
    }
    fun updateLocation(location: String) {
        uiState = uiState.copy(location = location)
    }
    fun updateCapacity(capacity: Int) {
        uiState = uiState.copy(capacity = capacity)
    }
    fun updatePrice(price: Double) {
        uiState = uiState.copy(price = price)
    }
    fun updateDuration(duration: Int) {
        uiState = uiState.copy(duration = duration)
    }
    fun updateCategory(category: String) {
        uiState = uiState.copy(category = category)
    }
    fun updateImages(images: List<Uri>) {
        uiState = uiState.copy(images = images)
    }
    fun updateRequirements(requirements: String) {
        uiState = uiState.copy(requirements = requirements)
    }
    fun updateStatus(status: String) {
        uiState = uiState.copy(status = status)
    }
    fun updateMenuImage(image: Uri?) {
        uiState = uiState.copy(image = image)
    }
    fun updateMenuText(text: String) {
        uiState = uiState.copy(text = text)
    }
    fun updateError(error: String?) {
        uiState = uiState.copy(error = error)
    }
}