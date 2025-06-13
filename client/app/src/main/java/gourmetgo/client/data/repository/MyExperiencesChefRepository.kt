package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.data.remote.ApiService
import gourmetgo.client.data.models.Experience
import gourmetgo.client.data.models.User
import kotlinx.coroutines.delay
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager

class MyExperiencesChefRepository(
    private val apiService: ApiService,
    private val sprefsManager: SharedPrefsManager
) {    suspend fun getAllMyExperiences(): Result<List<Experience>> {
        Log.d("MyExperiencesChefRepository", "Entrando a getAllMyExperiences")
        val chef = sprefsManager.getChef()
        Log.d("MyExperiencesChefRepository", "Chef from SharedPrefs: $chef")
        val id = chef?._id ?: run {
            Log.e("MyExperiencesChefRepository", "User ID is null in SharedPrefs")
            return Result.failure(Exception("Usuario no encontrado en SharedPrefs"))
        }
        return try {
            getAllMyExperiencesWithApi(id)
        
        } catch (e: Exception) {
            Log.e("MyExperiencesChefRepository", "Error getting all experiences", e)
            Result.failure(Exception("Error connection: ${e.message}"))
        }
    }

    suspend fun getAllMyExperiencesWithApi(id: String): Result<List<Experience>> {
        return try {
            Log.d("MyExperiencesChefRepository", "Llamando a getChefExperiences con id: $id")
            val experiences = apiService.getChefExperiences(id)
            Log.d("MyExperiencesChefRepository", "Loaded "+experiences.size+" experiences from API for chef "+id)
            Result.success(experiences)
        } catch (e: Exception) {
            Log.e("MyExperiencesChefRepository", "Error in API getAllMyExperiences", e)
            Result.failure(Exception("Error connection to server"))
        }
    }
}