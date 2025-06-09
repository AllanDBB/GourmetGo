package gourmetgo.client.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.AppConfig
import gourmetgo.client.data.repository.UpdateExperienceRepository
import gourmetgo.client.utils.EditProfileUtils
import gourmetgo.client.viewmodel.statesUi.UpdateExperienceUiState
import kotlinx.coroutines.launch
import gourmetgo.client.enums.Preferences
import gourmetgo.client.utils.ImageUploadUtils

class UpdateExperienceViewModel(
    private val updateExperienceRepository: UpdateExperienceRepository
    private val idExperience: String,
) : ViewModel() {

    var uiState by mutableStateOf(UpdateExperienceUiState())
        private set

    init {
        loadCurrentData()
    }

    fun loadCurrentData() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, error = null)

                val currentExperience = updateExperienceRepository.getCurrentExperience(idExperience)
                uiState = uiState.copy(
                    isLoading = false,
                    experience = currentExperience,
                    error = null
                )

                if (AppConfig.ENABLE_LOGGING) {
                    Log.d("UpdateExperienceViewModel", "Current experience loaded: ${currentExperience.title}")
                }
            } catch (e: Exception) {
                val userMessage = EditProfileUtils.mapErrorToUserMessage(e)
                uiState = uiState.copy(
                    isLoading = false,
                    error = userMessage
                )
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("UpdateExperienceViewModel", "Error loading current experience", e)
                }
            }
        }
    }

}