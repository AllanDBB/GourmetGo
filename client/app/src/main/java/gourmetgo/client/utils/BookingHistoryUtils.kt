package gourmetgo.client.utils

import gourmetgo.client.data.models.dtos.BookingSummary
import java.text.SimpleDateFormat
import java.util.*

object BookingHistoryUtils {

    fun formatPhoneForDisplay(phone: String): String {
        if (phone.isBlank()) return ""

        val numbers = phone.replace(Regex("[^0-9]"), "")

        return when {
            numbers.length == 11 && numbers.startsWith("506") -> {
                val localNumber = numbers.substring(3)
                "+506 ${localNumber.substring(0, 4)} ${localNumber.substring(4)}"
            }
            numbers.length == 8 -> {
                "+506 ${numbers.substring(0, 4)} ${numbers.substring(4)}"
            }
            numbers.length == 10 && numbers.startsWith("06") -> {
                val localNumber = numbers.substring(2)
                "+506 ${localNumber.substring(0, 4)} ${localNumber.substring(4)}"
            }
            numbers.length >= 4 -> {
                val cleanLocal = if (numbers.startsWith("506")) {
                    numbers.substring(3)
                } else {
                    numbers.take(8)
                }

                when {
                    cleanLocal.length <= 4 -> "+506 $cleanLocal"
                    else -> "+506 ${cleanLocal.substring(0, 4)} ${cleanLocal.substring(4)}"
                }
            }
            else -> numbers
        }
    }

    fun formatCreatedDate(createdAt: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(createdAt)

            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            try {
                val fallbackFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                fallbackFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = fallbackFormat.parse(createdAt)

                val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                createdAt
            }
        }
    }

    fun getStatusDisplayText(status: String): String {
        return when (status) {
            "pending" -> "Pendiente"
            "confirmed" -> "Confirmada"
            "cancelled" -> "Cancelada"
            "expired" -> "Expirada"
            "attended" -> "Asistida"
            else -> "Desconocido"
        }
    }

    fun canCancelBooking(booking: BookingSummary): Boolean {
        return booking.status == "pending"
    }    fun canRateBooking(booking: BookingSummary): Boolean {
        // Permite reseñas para experiencias asistidas, confirmadas o pendientes
        return booking.status in listOf("attended", "confirmed", "pending")
    }

    fun getStatusColor(status: String): String {
        return when (status) {
            "pending" -> "orange"
            "confirmed" -> "green"
            "cancelled" -> "red"
            "expired" -> "gray"
            "attended" -> "blue"
            else -> "gray"
        }
    }
}