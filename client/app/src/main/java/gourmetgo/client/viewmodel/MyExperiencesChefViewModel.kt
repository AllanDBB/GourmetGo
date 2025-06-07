package gourmetgo.client.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.viewmodel.statesUi.MyExperiencesChefUiState
import gourmetgo.client.data.models.Experience
import gourmetgo.client.data.repository.MyExperiencesChefRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job

class MyExperiencesChefViewModel(
    private val repository: MyExperiencesChefRepository,
) : ViewModel() { 
        var uiState by mutableStateOf(MyExperiencesChefUiState())
        private set

    private var searchJob: Job? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, error = null)

                val experiencesResult = repository.getAllMyExperiences()

                experiencesResult
                    .onSuccess { experiences ->
                        uiState = uiState.copy(
                            experiences = experiences,
                            isLoading = false
                        )
                        Log.d("MyExperiencesChefViewModel", "Loaded ${experiences.size} experiences")
                    }
                    .onFailure { error ->
                        uiState = uiState.copy(
                            isLoading = false,
                            error = error.message ?: "Error desconocido"
                        )
                        Log.e("MyExperiencesChefViewModel", "Error loading experiences", error)
                    }

            } catch (e: Exception) {
                Log.e("MyExperiencesChefViewModel", "Error in loadInitialData", e)
                uiState = uiState.copy(isLoading = false, error = e.message ?: "Error desconocido")
            }
        }
    }

    fun refreshExperiences() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(refreshing = true, error = null)

                repository.getAllMyExperiences().onSuccess { experiences ->
                    uiState = uiState.copy(
                        experiences = experiences,
                        refreshing = false
                    )
                    Log.d("MyExperiencesChefViewModel", "Refreshed ${experiences.size} experiences")
                }.onFailure { error ->
                    uiState = uiState.copy(
                        refreshing = false,
                        error = error.message ?: "Error desconocido"
                    )
                    Log.e("MyExperiencesChefViewModel", "Error refreshing experiences", error)
                }

            } catch (e: Exception) {
                uiState = uiState.copy(
                    refreshing = false,
                    error = "Error inesperado al actualizar: ${e.message}"
                )
                Log.e("MyExperiencesChefViewModel", "Unexpected error in refreshExperiences", e)
            }

        }
    }
}