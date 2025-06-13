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
        Log.d("MyExperiencesChefRepository", sprefsManager.debugUserData())
        
        val chef = sprefsManager.getChef()
        
        if (chef == null) {
            Log.e("MyExperiencesChefRepository", "Chef is null in SharedPrefs")
            sprefsManager.clearCorruptedData()
            return Result.failure(Exception("Chef no encontrado en SharedPrefs. Por favor, inicia sesión nuevamente."))
        }
        
        val id = chef._id
        if (id.isBlank()) {
            Log.e("MyExperiencesChefRepository", "Chef ID is blank in SharedPrefs")
            sprefsManager.clearCorruptedData()
            return Result.failure(Exception("ID del chef no válido. Por favor, inicia sesión nuevamente."))
        }
        
        Log.d("MyExperiencesChefRepository", "Using chef ID: '$id'")
        return try {
            getAllMyExperiencesWithApi(id)
        } catch (e: Exception) {
            Log.e("MyExperiencesChefRepository", "Error getting all experiences", e)
            Result.failure(Exception("Error de conexión: ${e.message}"))
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