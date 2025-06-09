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
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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
                        val userMessage = mapErrorToUserMessage(error)
                        uiState = uiState.copy(
                            isLoading = false,
                            error = userMessage
                        )
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.e("BookingViewModel", "Error loading experience", error)
                        }
                    }
            } catch (e: Exception) {
                val userMessage = mapErrorToUserMessage(e)
                uiState = uiState.copy(
                    isLoading = false,
                    error = userMessage
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
            uiState = uiState.copy(error = "Todos los campos requeridos deben estar completos")
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
                        val userMessage = mapErrorToUserMessage(error)
                        uiState = uiState.copy(
                            isBooking = false,
                            error = userMessage
                        )
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.e("BookingViewModel", "Error creating booking", error)
                        }
                    }
            } catch (e: Exception) {
                val userMessage = mapErrorToUserMessage(e)
                uiState = uiState.copy(
                    isBooking = false,
                    error = userMessage
                )
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("BookingViewModel", "Unexpected error in createBooking", e)
                }
            }
        }
    }

    private fun mapErrorToUserMessage(error: Throwable): String {
        return when (error) {
            is HttpException -> {
                when (error.code()) {
                    400 -> "Los datos ingresados no son válidos. Revisa la información."
                    401 -> "Tu sesión ha expirado. Inicia sesión nuevamente."
                    403 -> "No tienes permisos para realizar esta acción."
                    404 -> "La experiencia solicitada no existe."
                    409 -> "Ya tienes una reservación para esta experiencia."
                    422 -> "Los datos no cumplen los requisitos. Verifica la información."
                    429 -> "Demasiadas solicitudes. Espera un momento e intenta nuevamente."
                    in 500..599 -> "Error del servidor. Intenta nuevamente en unos minutos."
                    else -> "Error del servidor (${error.code()}). Intenta nuevamente."
                }
            }
            is ConnectException, is UnknownHostException -> {
                "No se pudo conectar al servidor. Revisa tu conexión a internet."
            }
            is SocketTimeoutException -> {
                "La conexión tardó demasiado. Intenta nuevamente."
            }
            else -> {
                "Ocurrió un error inesperado. Intenta nuevamente."
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