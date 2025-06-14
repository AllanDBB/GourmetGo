package gourmetgo.client.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.data.repository.RatingRepository
import gourmetgo.client.data.repository.ExperienceDetailsRepository
import gourmetgo.client.data.repository.BookingRepository
import gourmetgo.client.viewmodel.statesUi.ExperienceReviewsUiState
import kotlinx.coroutines.launch

class ExperienceReviewsViewModel(
    private val ratingRepository: RatingRepository,
    private val experienceRepository: ExperienceDetailsRepository,
    private val bookingRepository: BookingRepository,
    private val experienceId: String
) : ViewModel() {

    var uiState by mutableStateOf(ExperienceReviewsUiState(experienceId = experienceId))
        private set

    init {
        loadReviews()
    }    fun loadReviews() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, error = null)
                
                // Cargar la experiencia primero
                val experienceResult = experienceRepository.getExperienceDetails(experienceId)
                
                experienceResult
                    .onSuccess { experience ->
                        uiState = uiState.copy(experience = experience)
                        
                        // Cargar las reservas del usuario en paralelo
                        loadUserBookings()
                        
                        // Luego cargar las reseñas
                        loadRatings()
                    }
                    .onFailure { error ->
                        uiState = uiState.copy(
                            isLoading = false,
                            error = error.message ?: "Error al cargar la experiencia"
                        )
                        Log.e("ExperienceReviewsViewModel", "Error loading experience", error)
                    }
                    
            } catch (e: Exception) {
                Log.e("ExperienceReviewsViewModel", "Error in loadReviews", e)
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }
    
    private suspend fun loadUserBookings() {
        try {
            val bookingsResult = bookingRepository.getMyBookings()
            
            bookingsResult
                .onSuccess { bookings ->
                    uiState = uiState.copy(userBookings = bookings)
                    Log.d("ExperienceReviewsViewModel", "Loaded ${bookings.size} user bookings")
                }
                .onFailure { error ->
                    // No es crítico si no se pueden cargar las reservas
                    Log.w("ExperienceReviewsViewModel", "Could not load user bookings: ${error.message}")
                }
        } catch (e: Exception) {
            Log.w("ExperienceReviewsViewModel", "Error loading user bookings", e)
        }
    }
    
    private suspend fun loadRatings() {
        try {
            val result = ratingRepository.getExperienceRatings(experienceId)
            
            result
                .onSuccess { reviews ->
                    val averageRating = if (reviews.isNotEmpty()) {
                        reviews.map { it.score }.average()
                    } else {
                        0.0
                    }
                    
                    uiState = uiState.copy(
                        isLoading = false,
                        reviews = reviews,
                        averageRating = averageRating,
                        totalReviews = reviews.size
                    )
                    
                    Log.d("ExperienceReviewsViewModel", "Loaded ${reviews.size} reviews")
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        error = error.message ?: "Error al cargar las reseñas"
                    )
                    Log.e("ExperienceReviewsViewModel", "Error loading reviews", error)
                }
                
        } catch (e: Exception) {
            Log.e("ExperienceReviewsViewModel", "Error in loadRatings", e)
            uiState = uiState.copy(
                isLoading = false,
                error = e.message ?: "Error desconocido"
            )
        }
    }

    fun retryLoading() {
        loadReviews()
    }
}
