package gourmetgo.client.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.viewmodel.statesUi.ViewAssistanceUiState
import gourmetgo.client.data.models.Experience
import gourmetgo.client.data.repository.ViewAssistanceRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job

class ViewAssistanceViewModel(
    private val repository: ViewAssistanceRepository,
    private val experienceId: String 
) : ViewModel() { 
        var uiState by mutableStateOf(ViewAssistanceUiState())
        private set

    private var searchJob: Job? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, error = null)

                val assistanceResult = repository.getAssistanceById(experienceId)
                val experienceResult = repository.getCurrentExperience(experienceId)

                assistanceResult
                    .onSuccess { bookings ->
                        uiState = uiState.copy(
                            bookings = bookings,
                            isLoading = false,
                            experience = experienceResult
                        )
                        Log.d("ViewAssistanceViewModel", "Loaded ${bookings} assistance records")
                    }
                    .onFailure { error ->
                        uiState = uiState.copy(
                            isLoading = false,
                            error = error.message ?: "Error desconocido"
                        )
                        Log.e("ViewAssistanceViewModel", "Error loading assistance", error)
                    }

            } catch (e: Exception) {
                Log.e("ViewAssistanceViewModel", "Error in loadInitialData", e)
                uiState = uiState.copy(isLoading = false, error = e.message ?: "Error desconocido")
            }
        }
    }

    fun refreshAssistance(){
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, error = null)

                val assistanceResult = repository.getAssistanceById(experienceId)
                val experience = repository.getCurrentExperience(experienceId)

                assistanceResult
                    .onSuccess { bookings ->
                        uiState = uiState.copy(
                            bookings = bookings,
                            isLoading = false,
                            experience = experience ?: Experience()
                        )
                        Log.d("ViewAssistanceViewModel", "Refreshed "+bookings+" assistance records")
                    }
                    .onFailure { error ->
                        uiState = uiState.copy(
                            isLoading = false,
                            error = error.message ?: "Error desconocido"
                        )
                        Log.e("ViewAssistanceViewModel", "Error refreshing assistance", error)
                    }

            } catch (e: Exception) {
                Log.e("ViewAssistanceViewModel", "Error in refreshAssistance", e)
                uiState = uiState.copy(isLoading = false, error = e.message ?: "Error desconocido")
            }
        }
    }
}
