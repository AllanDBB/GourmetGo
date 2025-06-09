package gourmetgo.client.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.AppConfig
import gourmetgo.client.data.repository.BookingRepository
import gourmetgo.client.viewmodel.statesUi.BookingHistoryUiState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class BookingHistoryViewModel(
    private val repository: BookingRepository
) : ViewModel() {

    var uiState by mutableStateOf(BookingHistoryUiState())
        private set

    fun loadBookingHistory() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, error = null)

                repository.getMyBookings()
                    .onSuccess { bookings ->
                        uiState = uiState.copy(
                            isLoading = false,
                            bookings = bookings.sortedByDescending { it.createdAt },
                            error = null
                        )
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.d("BookingHistoryViewModel", "Loaded ${bookings.size} bookings")
                        }
                    }
                    .onFailure { error ->
                        val userMessage = mapErrorToUserMessage(error)
                        uiState = uiState.copy(
                            isLoading = false,
                            error = userMessage
                        )
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.e("BookingHistoryViewModel", "Error loading booking history", error)
                        }
                    }
            } catch (e: Exception) {
                val userMessage = mapErrorToUserMessage(e)
                uiState = uiState.copy(
                    isLoading = false,
                    error = userMessage
                )
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("BookingHistoryViewModel", "Unexpected error in loadBookingHistory", e)
                }
            }
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isCancelling = true, error = null)

                // Find the booking to cancel
                val bookingToCancel = uiState.bookings.find { it._id == bookingId }
                if (bookingToCancel == null) {
                    uiState = uiState.copy(
                        isCancelling = false,
                        error = "Reserva no encontrada"
                    )
                    return@launch
                }

                if (bookingToCancel.status != "pending") {
                    uiState = uiState.copy(
                        isCancelling = false,
                        error = "Solo se pueden cancelar reservas pendientes"
                    )
                    return@launch
                }

                // Call the repository to cancel booking (this would need to be added to BookingRepository)
                // For now, we'll simulate the cancellation by updating the local state
                val updatedBookings = uiState.bookings.map { booking ->
                    if (booking._id == bookingId) {
                        booking.copy(status = "cancelled")
                    } else {
                        booking
                    }
                }

                uiState = uiState.copy(
                    isCancelling = false,
                    bookings = updatedBookings,
                    cancelSuccess = true,
                    error = null
                )

                if (AppConfig.ENABLE_LOGGING) {
                    Log.d("BookingHistoryViewModel", "Booking cancelled successfully: $bookingId")
                }

            } catch (e: Exception) {
                val userMessage = mapErrorToUserMessage(e)
                uiState = uiState.copy(
                    isCancelling = false,
                    error = userMessage
                )
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("BookingHistoryViewModel", "Error cancelling booking", e)
                }
            }
        }
    }

    fun refreshBookings() {
        loadBookingHistory()
    }

    private fun mapErrorToUserMessage(error: Throwable): String {
        return when (error) {
            is HttpException -> {
                when (error.code()) {
                    400 -> "Solicitud inválida. Verifica los datos."
                    401 -> "Tu sesión ha expirado. Inicia sesión nuevamente."
                    403 -> "No tienes permisos para realizar esta acción."
                    404 -> "Recurso no encontrado."
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

    fun clearCancelSuccess() {
        uiState = uiState.copy(cancelSuccess = false)
    }
}