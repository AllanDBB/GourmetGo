package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.models.Experience
import gourmetgo.client.data.models.dtos.UpdateExperienceRequest
import gourmetgo.client.data.remote.ApiService

class UpdateExperienceRepository(
    private val apiService: ApiService,
    private val sharedPrefs: SharedPrefsManager
    private val idExperience: String 
) {
    suspend fun updateExperience(
        experienceId: String,
        location: String,
        date: String,
        status: String,
        capacity: Int,
        price: Double
    ): Result<Experience> {
        return try {
            val token = sharedPrefs.getToken()
            if (token == null) {
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("UpdateExperienceRepository", "No token found")
                }
                return Result.failure(Exception("User not authenticated"))
            }

            val request = UpdateExperienceRequest(
                location = location,
                date = date,
                status = status,
                capacity = capacity,
                price = price
            )
            Log.d("UpdateExperience", "Sending $request")
            val response = apiService.updateExperience("Bearer $token", experienceId, request)

            if (AppConfig.ENABLE_LOGGING) {
                Log.d("UpdateExperienceRepository", "Experience updated successfully: ${response.`_id`}")
            }

            Result.success(response)

        } catch (e: Exception) {
            if (AppConfig.ENABLE_LOGGING) {
                Log.e("UpdateExperienceRepository", "Error updating experience", e)
            }
            Result.failure(Exception("Error updating experience: ${e.message}"))
        }
    }

    suspend fun getCurrentExperience(idExperience: String): Experience {
        
        return try {
            val response = apiService.getExperienceById(idExperience)
            if (AppConfig.ENABLE_LOGGING) {
                Log.d("UpdateExperienceRepository", "Current experience loaded: ${response.title}")
            }
    
        } catch (e: Exception) {
            if (AppConfig.ENABLE_LOGGING) {
                Log.e("UpdateExperienceRepository", "Error loading current experience", e)
            }
            throw Exception("Error loading current experience: ${e.message}")
        }
    }
}