package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.models.Booking
import gourmetgo.client.data.models.Experience
import gourmetgo.client.data.models.dtos.BookingRequest
import gourmetgo.client.data.remote.ApiService

class BookingRepository(
    private val apiService: ApiService,
    private val sharedPrefs: SharedPrefsManager
) {

    suspend fun createBooking(
        experienceId: String,
        people: Int,
        name: String,
        email: String,
        phone: String,
        termsAccepted: Boolean,
        paymentMethod: String
    ): Result<Booking> {
        return try {
            val token = sharedPrefs.getToken()
            if (token == null) {
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("BookingRepository", "No token found")
                }
                return Result.failure(Exception("User not authenticated"))
            }

            val request = BookingRequest(
                experienceId = experienceId,
                people = people,
                name = name,
                email = email,
                phone = phone,
                termsAccepted = termsAccepted,
                paymentMethod = paymentMethod
            )
            Log.d("Booking","Sending $request")
            val response = apiService.createBooking("Bearer $token", request)

            if (AppConfig.ENABLE_LOGGING) {
                Log.d("BookingRepository", "Booking created successfully: ${response.booking.bookingCode}")
            }

            Result.success(response.booking)

        } catch (e: Exception) {
            if (AppConfig.ENABLE_LOGGING) {
                Log.e("BookingRepository", "Error creating booking", e)
            }
            Result.failure(Exception("Error creating booking: ${e.message}"))
        }
    }

    suspend fun getMyBookings(): Result<List<Booking>> {
        return try {
            val token = sharedPrefs.getToken()
            if (token == null) {
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("BookingRepository", "No token found")
                }
                return Result.failure(Exception("User not authenticated"))
            }

            val response = apiService.getMyBookings("Bearer $token")

            if (AppConfig.ENABLE_LOGGING) {
                Log.d("BookingRepository", "Retrieved ${response.bookings.size} bookings")
            }

            Result.success(response.bookings)

        } catch (e: Exception) {
            if (AppConfig.ENABLE_LOGGING) {
                Log.e("BookingRepository", "Error getting bookings", e)
            }
            Result.failure(Exception("Error getting bookings: ${e.message}"))
        }
    }

    suspend fun getExperienceById(experienceId: String): Result<Experience> {
        return try {
            val response = apiService.getExperienceById(experienceId)

            if (AppConfig.ENABLE_LOGGING) {
                Log.d("BookingRepository", "Retrieved experience: ${response.title}")
            }

            Result.success(response)

        } catch (e: Exception) {
            if (AppConfig.ENABLE_LOGGING) {
                Log.e("BookingRepository", "Error getting experience", e)
            }
            Result.failure(Exception("Error getting experience: ${e.message}"))
        }
    }
}