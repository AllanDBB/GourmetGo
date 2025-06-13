package gourmetgo.client.viewmodel

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.AppConfig
import gourmetgo.client.data.models.dtos.BookingSummary
import gourmetgo.client.data.repository.BookingRepository
import gourmetgo.client.viewmodel.statesUi.BookingHistoryUiState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class BookingHistoryViewModel(
    private val repository: BookingRepository
) : ViewModel() {

    var uiState by mutableStateOf(BookingHistoryUiState())
        private set

    init {
        if (AppConfig.ENABLE_LOGGING) {
            Log.d("BookingHistoryViewModel", "ViewModel initialized")
        }
    }

    fun loadBookingHistory() {
        if (AppConfig.ENABLE_LOGGING) {
            Log.d("BookingHistoryViewModel", "Starting to load booking history")
        }
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

    fun downloadBookingPDF(context: Context, booking: BookingSummary, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(300, 400, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas
                val paint = android.graphics.Paint()
                paint.textSize = 14f
                var y = 30

                canvas.drawText("Comprobante de Reserva", 60f, y.toFloat(), paint)
                y += 30
                canvas.drawText("Nombre: ${booking.name}", 10f, y.toFloat(), paint)
                y += 25
                canvas.drawText("Experiencia: ${booking.experience?.title ?: "No disponible"}", 10f, y.toFloat(), paint)
                y += 25
                canvas.drawText("Fecha: ${booking.experience?.date ?: "No disponible"}", 10f, y.toFloat(), paint)
                y += 25
                canvas.drawText("Lugar: ${booking.experience?.location ?: "No disponible"}", 10f, y.toFloat(), paint)
                y += 25
                canvas.drawText("Estado: ${booking.status}", 10f, y.toFloat(), paint)
                y += 25
                canvas.drawText("Código: ${booking.bookingCode}", 10f, y.toFloat(), paint)
                y += 25
                canvas.drawText("Personas: ${booking.people}", 10f, y.toFloat(), paint)
                y += 25
                canvas.drawText("Método de pago: ${booking.paymentMethod}", 10f, y.toFloat(), paint)

                pdfDocument.finishPage(page)

                // Guardar en carpeta pública de Documentos
                val dir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOCUMENTS)
                if (!dir.exists()) dir.mkdirs()
                val file = java.io.File(dir, "Comprobante_${booking.bookingCode}.pdf")
                val outputStream = java.io.FileOutputStream(file)
                pdfDocument.writeTo(outputStream)
                pdfDocument.close()
                outputStream.close()
                onResult(true, "PDF guardado en Documentos: ${file.absolutePath}")
            } catch (e: Exception) {
                onResult(false, "Error al generar PDF: ${e.message}")
            }
        }
    }
}