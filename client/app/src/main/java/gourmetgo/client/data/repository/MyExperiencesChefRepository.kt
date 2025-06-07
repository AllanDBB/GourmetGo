package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.data.remote.ApiService
import gourmetgo.client.data.mockups.ExperiencesMockup
import gourmetgo.client.data.models.Experience
import gourmetgo.client.data.models.dtos.ExperiencesResponse
import gourmetgo.client.data.models.User
import kotlinx.coroutines.delay
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager

class MyExperiencesChefRepository(
    private val apiService: ApiService,
    private val sprefsManager: SharedPrefsManager
) {
    suspend fun getAllMyExperiences(): Result<List<Experience>> {
    
        val id =sprefsManager.getChef()?._id ?: run {
            Log.e("MyExperiencesChefRepository", "User ID is null in SharedPrefs")
            return Result.failure(Exception("Usuario no encontrado en SharedPrefs"))
        }
        if (id == null) {
            return Result.failure(Exception("Usuario no encontrado en SharedPrefs"))
        }
        return try {
            if (AppConfig.USE_MOCKUP) {
                getAllMyExperiencesWithMockup(id)
            } else {
                getAllMyExperiencesWithApi(id)
            }
        } catch (e: Exception) {
            Log.e("MyExperiencesChefRepository", "Error getting all experiences", e)
            Result.failure(Exception("Error connection: ${e.message}"))
        }
    }

    suspend fun getAllMyExperiencesWithApi(id: String): Result<List<Experience>> {
        return try {
            val experiences = apiService.getChefExperiences(id).experiences
            Log.d("MyExperiencesChefRepository", "Loaded ${experiences.size} experiences from API for chef $id")
            Result.success(experiences)
        } catch (e: Exception) {
            Log.e("MyExperiencesChefRepository", "Error in API getAllMyExperiences", e)
            Result.failure(Exception("Error connection to server"))
        }
    }

    suspend fun getAllMyExperiencesWithMockup(id: String): Result<List<Experience>> {
        return try {
            val experiences = ExperiencesMockup.getExperiencesByChef(id)
            Log.d("MyExperiencesChefRepository", "Loaded ${experiences.size} experiences from mockup for chef $id")
            Result.success(experiences)
        } catch (e: Exception) {
            Log.e("MyExperiencesChefRepository", "Error in mockup getAllMyExperiences", e)
            Result.failure(Exception("Error loading mockup data"))
        }
    }
}