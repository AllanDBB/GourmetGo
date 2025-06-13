package gourmetgo.client.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.AppConfig
import gourmetgo.client.data.repository.DeleteExperienceRepository
import gourmetgo.client.utils.EditProfileUtils
import gourmetgo.client.viewmodel.statesUi.DeleteExperienceUiState
import kotlinx.coroutines.launch
import gourmetgo.client.enums.Preferences
import gourmetgo.client.utils.ImageUploadUtils

class DeleteExperienceViewModel(
    private val deleteExperienceRepository: DeleteExperienceRepository,
    private val idExperience: String,
) : ViewModel() {

    var uiState by mutableStateOf(DeleteExperienceUiState())
        private set

    fun RequestDeleteExperience(mail: String) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, error = null, mail = mail)

                val result = deleteExperienceRepository.requestDelete(idExperience, mail)
                if (result.isSuccess) {
                    uiState = uiState.copy(isLoading = false, error = null)
                    if (AppConfig.ENABLE_LOGGING) {
                        Log.d("DeleteExperienceViewModel", "Experience deleted successfully")
                    }
                } else {
                    uiState = uiState.copy(isLoading = false, error = result.exceptionOrNull()?.message)
                    if (AppConfig.ENABLE_LOGGING) {
                        Log.e("DeleteExperienceViewModel", "Error deleting experience", result.exceptionOrNull())
                    }
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.message)
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("DeleteExperienceViewModel", "Error in deleteExperience", e)
                }
            }
        }
    }

    fun DeleteExperience(code: String) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, error = null)
                val mail = uiState.mail ?: ""
                val result = deleteExperienceRepository.deleteExperience(idExperience, mail, code)
                if (result.isSuccess) {
                    uiState = uiState.copy(isLoading = false, error = null)
                    if (AppConfig.ENABLE_LOGGING) {
                        Log.d("DeleteExperienceViewModel", "Experience deleted successfully")
                    }
                } else {
                    uiState = uiState.copy(isLoading = false, error = result.exceptionOrNull()?.message)
                    if (AppConfig.ENABLE_LOGGING) {
                        Log.e("DeleteExperienceViewModel", "Error deleting experience", result.exceptionOrNull())
                    }
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.message)
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("DeleteExperienceViewModel", "Error in deleteExperience", e)
                }
            }
        }
    }
}