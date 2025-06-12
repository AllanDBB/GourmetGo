package gourmetgo.client.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.AppConfig
import gourmetgo.client.data.models.dtos.BookingSummary
import gourmetgo.client.data.repository.RatingRepository
import gourmetgo.client.viewmodel.statesUi.RatingUiState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RatingViewModel(
    private val repository: RatingRepository,
    private val experienceId: String
) : ViewModel() {

    var uiState by mutableStateOf(RatingUiState())
        private set

    fun submitRating(score: Int, comment: String) {
        if (score < 1 || score > 5) {
            uiState = uiState.copy(error = "La calificación debe estar entre 1 y 5 estrellas")
            return
        }

        if (comment.isBlank()) {
            uiState = uiState.copy(error = "El comentario es obligatorio")
            return
        }

        if (comment.length < 10) {
            uiState = uiState.copy(error = "El comentario debe tener al menos 10 caracteres")
            return
        }

        if (comment.length > 500) {
            uiState = uiState.copy(error = "El comentario no puede exceder 500 caracteres")
            return
        }

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isSubmitting = true, error = null)

                repository.createRating(
                    experienceId = experienceId,
                    score = score,
                    comment = comment.trim()
                )
                    .onSuccess { rating ->
                        uiState = uiState.copy(
                            isSubmitting = false,
                            rating = rating,
                            ratingSuccess = true,
                            error = null
                        )
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.d("RatingViewModel", "Rating submitted successfully: ${rating._id}")
                        }
                    }
                    .onFailure { error ->
                        val userMessage = mapErrorToUserMessage(error)
                        uiState = uiState.copy(
                            isSubmitting = false,
                            error = userMessage
                        )
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.e("RatingViewModel", "Error submitting rating", error)
                        }
                    }
            } catch (e: Exception) {
                val userMessage = mapErrorToUserMessage(e)
                uiState = uiState.copy(
                    isSubmitting = false,
                    error = userMessage
                )
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("RatingViewModel", "Unexpected error in submitRating", e)
                }
            }
        }
    }

    private fun mapErrorToUserMessage(error: Throwable): String {
        return when (error) {
            is HttpException -> {
                when (error.code()) {
                    400 -> "Los datos de la calificación no son válidos. Revisa la información."
                    401 -> "Tu sesión ha expirado. Inicia sesión nuevamente."
                    403 -> "No tienes permisos para realizar esta acción."
                    404 -> "La experiencia no existe."
                    409 -> "Ya has calificado esta experiencia anteriormente."
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

    fun clearRatingSuccess() {
        uiState = uiState.copy(ratingSuccess = false)
    }
}