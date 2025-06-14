package gourmetgo.client.utils

import gourmetgo.client.data.models.dtos.BookingSummary
import java.text.SimpleDateFormat
import java.util.*

object ExperienceDateUtils {
    
    /**
     * Verifica si una experiencia ya ocurrió (fecha pasada)
     * @param experienceDate Fecha de la experiencia en formato ISO
     * @return true si la experiencia ya ocurrió, false si aún no
     */
    fun hasExperienceOccurred(experienceDate: String): Boolean {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            
            val experienceDateTime = inputFormat.parse(experienceDate)
            val currentTime = Date()
            
            experienceDateTime?.before(currentTime) ?: false
        } catch (e: Exception) {
            // Si hay error al parsear, asumir que no ha ocurrido por seguridad
            false
        }
    }
    
    /**
     * Verifica si el usuario tiene una reserva activa para una experiencia
     * @param experienceId ID de la experiencia
     * @param userBookings Lista de reservas del usuario
     * @return true si el usuario tiene una reserva válida
     */
    fun hasUserBookingForExperience(experienceId: String, userBookings: List<BookingSummary>): Boolean {
        return userBookings.any { booking ->
            booking.experience?._id == experienceId && 
            booking.status in listOf("pending", "confirmed", "attended")
        }
    }
    
    /**
     * Verifica si se puede dejar una reseña para una experiencia
     * @param experienceDate Fecha de la experiencia
     * @param experienceId ID de la experiencia
     * @param userBookings Lista de reservas del usuario (opcional)
     * @return true si se puede dejar reseña
     */
    fun canLeaveReview(
        experienceDate: String, 
        experienceId: String, 
        userBookings: List<BookingSummary> = emptyList()
    ): Boolean {
        // Permite reseña si la experiencia ya ocurrió
        if (hasExperienceOccurred(experienceDate)) {
            return true
        }
        
        // También permite reseña si el usuario tiene una reserva para esta experiencia
        return hasUserBookingForExperience(experienceId, userBookings)
    }
    
    /**
     * Obtiene un mensaje explicativo sobre el estado de las reseñas
     * @param experienceDate Fecha de la experiencia
     * @param experienceId ID de la experiencia
     * @param userBookings Lista de reservas del usuario (opcional)
     * @return Mensaje explicativo
     */
    fun getReviewStatusMessage(
        experienceDate: String, 
        experienceId: String, 
        userBookings: List<BookingSummary> = emptyList()
    ): String {
        val hasOccurred = hasExperienceOccurred(experienceDate)
        val hasBooking = hasUserBookingForExperience(experienceId, userBookings)
        
        return when {
            hasOccurred -> "Esta experiencia ya ocurrió, puedes dejar tu reseña"
            hasBooking -> "Tienes una reserva para esta experiencia, puedes dejar tu reseña"
            else -> "Solo puedes dejar reseñas después de que haya ocurrido la experiencia o si tienes una reserva"
        }
    }
    
    /**
     * Obtiene un mensaje explicativo sobre por qué no se puede dejar reseña
     * @deprecated Usar getReviewStatusMessage en su lugar
     */
    @Deprecated("Usar getReviewStatusMessage en su lugar")
    fun getReviewBlockedMessage(experienceDate: String): String {
        return if (!hasExperienceOccurred(experienceDate)) {
            "Solo puedes dejar reseñas después de que haya ocurrido la experiencia"
        } else {
            ""
        }
    }
}
