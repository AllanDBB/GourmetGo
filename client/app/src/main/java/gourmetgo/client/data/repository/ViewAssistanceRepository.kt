package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.remote.ApiService
import gourmetgo.client.data.models.Booking
import gourmetgo.client.data.models.Experience
import gourmetgo.client.data.models.dtos.AssistanceResponse

class ViewAssistanceRepository(
    private val apiService: ApiService,
    private val sprefsManager: SharedPrefsManager
) {
    suspend fun getAssistanceById(id: String): Result<List<AssistanceResponse>> {
        val token = sprefsManager.getToken()
        if (token.isNullOrEmpty()) {
            Log.e("ViewAssistanceRepository", "Token is null or empty")
            return Result.failure(Exception("Token is null or empty"))
        }

        Log.d("ViewAssistanceRepository", "Entrando a getAssistanceById con id: $id")
        return try {
            val assistance = apiService.getExperienceBookings("Bearer $token", id)
            Log.d("ViewAssistanceRepository", "Asistencia obtenida: $assistance")
            Result.success(assistance)
        } catch (e: Exception) {
            Log.e("ViewAssistanceRepository", "Error al obtener asistencia por ID", e)
            Result.failure(Exception("Error connection to server"))
        }
    }

    suspend fun getCurrentExperience(idExperience: String): Experience {
        return try {
            val response = apiService.getExperienceById(idExperience)
            if (AppConfig.ENABLE_LOGGING) {
                Log.d("UpdateExperienceRepository", "Current experience loaded: ${response.title}")
            }
            response
        } catch (e: Exception) {
            if (AppConfig.ENABLE_LOGGING) {
                Log.e("UpdateExperienceRepository", "Error loading current experience", e)
            }
            throw Exception("Error loading current experience: ${e.message}")
        }
    }


}