package gourmetgo.client.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.viewmodel.statesUi.ExperienceDetailsUiState
import gourmetgo.client.data.models.Experience
import gourmetgo.client.data.repository.ExperienceDetailsRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job

class ExperienceDetailsViewModel(
    private val repository: ExperienceDetailsRepository,
    private val experienceId: String 
) : ViewModel() {

    var uiState by mutableStateOf(ExperienceDetailsUiState())
        private set

    private var searchJob: Job? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData(){

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, error = null)
                
                val experienceResult = repository.getExperienceDetails(experienceId)

                experienceResult
                    .onSuccess { experience ->
                        uiState = uiState.copy(
                            experience = experience,
                            isLoading = false
                        )
                        Log.d("ExperiencesViewModel", "Loaded experience: ${experience.title}")
                    }
                    .onFailure { error ->
                        uiState = uiState.copy(
                            isLoading = false,
                            error = error.message ?: "Error desconocido"
                        )
                        Log.e("ExperiencesViewModel", "Error loading experience", error)
                    }

            } catch (e: Exception) {
                Log.e("ExperiencesViewModel", "Error in loadInitialData", e)
                uiState = uiState.copy(isLoading = false, error = e.message ?: "Error desconocido")
            }
        }
    }
}