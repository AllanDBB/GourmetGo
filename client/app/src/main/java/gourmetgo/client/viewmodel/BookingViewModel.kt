package gourmetgo.client.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.AppConfig
import gourmetgo.client.data.repository.BookingRepository
import gourmetgo.client.viewmodel.statesUi.BookingUiState
import kotlinx.coroutines.launch

class BookingViewModel(
    private val repository: BookingRepository
) : ViewModel() {

    var uiState by mutableStateOf(BookingUiState())
        private set

    fun loadExperience(experienceId: String) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, error = null)

                repository.getExperienceById(experienceId)
                    .onSuccess { experience ->
                        uiState = uiState.copy(
                            isLoading = false,
                            experience = experience,
                            error = null
                        )
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.d("BookingViewModel", "Experience loaded: ${experience.title}")
                        }
                    }
                    .onFailure { error ->
                        uiState = uiState.copy(
                            isLoading = false,
                            error = error.message ?: "Error loading experience"
                        )
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.e("BookingViewModel", "Error loading experience", error)
                        }
                    }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Unexpected error: ${e.message}"
                )
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("BookingViewModel", "Unexpected error in loadExperience", e)
                }
            }
        }
    }

    fun createBooking(
        experienceId: String,
        people: Int,
        name: String,
        email: String,
        phone: String,
        termsAccepted: Boolean,
        paymentMethod: String
    ) {
        if (name.isBlank() || email.isBlank() || paymentMethod.isBlank() || !termsAccepted) {
            uiState = uiState.copy(error = "All required fields must be completed")
            return
        }

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isBooking = true, error = null)

                repository.createBooking(
                    experienceId = experienceId,
                    people = people,
                    name = name,
                    email = email,
                    phone = phone,
                    termsAccepted = termsAccepted,
                    paymentMethod = paymentMethod
                )
                    .onSuccess { booking ->
                        uiState = uiState.copy(
                            isBooking = false,
                            booking = booking,
                            bookingSuccess = true,
                            error = null
                        )
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.d("BookingViewModel", "Booking created successfully: ${booking.bookingCode}")
                        }
                    }
                    .onFailure { error ->
                        uiState = uiState.copy(
                            isBooking = false,
                            error = error.message ?: "Error creating booking"
                        )
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.e("BookingViewModel", "Error creating booking", error)
                        }
                    }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isBooking = false,
                    error = "Unexpected error: ${e.message}"
                )
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("BookingViewModel", "Unexpected error in createBooking", e)
                }
            }
        }
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    fun clearBookingSuccess() {
        uiState = uiState.copy(bookingSuccess = false)
    }
}