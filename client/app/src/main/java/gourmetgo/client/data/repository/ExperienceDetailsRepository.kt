package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.models.Experience
import gourmetgo.client.data.models.dtos.LoginRequest
//import gourmetgo.client.data.models.dtos.SpecificExperienceResponse
import gourmetgo.client.data.remote.ApiService
import kotlinx.coroutines.delay

class ExperienceDetailsRepository(
    private val apiService: ApiService,
    private val sharedPrefs: SharedPrefsManager
) {

    suspend fun getExperienceDetails(experienceId: String): Result<Experience> {
        return try {
            getExperienceDetailsWithApi(experienceId)
            
        } catch (e: Exception) {
            Log.e("ExperienceDetailsRepository", "Error in getExperienceDetails", e)
            Result.failure(Exception("Error : ${e.message}"))
        }
    }


    private suspend fun getExperienceDetailsWithApi(experienceId: String): Result<Experience> {
        return try {
            Log.d("ExperienceDetailsRepository", "Fetching experience details from API for ID: $experienceId")
            val experience = apiService.getExperienceById(experienceId)
            Log.d("ExperienceDetailsRepository", "Received experience details: $experience")
            Result.success(experience)
        } catch (e: Exception) {
            Log.e("ExperienceDetailsRepository", "Error in API fetch", e)
            Result.failure(Exception("Error : ${e.message}"))
        }
    }
}