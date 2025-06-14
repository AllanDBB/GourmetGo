package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.models.Experience
import gourmetgo.client.data.models.dtos.CreateExperienceRequest
import gourmetgo.client.data.models.dtos.CreateExperienceResponse
import gourmetgo.client.data.remote.ApiService
import gourmetgo.client.data.models.dtos.Menu

class CreateExperienceRepository(
    private val apiService: ApiService,
    private val sharedPrefs: SharedPrefsManager
) {
    suspend fun createExperience(
        title: String,
        description: String,
        date: String,
        location: String, 
        capacity: Int,
        price: Double,
        duration: Int,
        category: String,
        images: List<String>, // URLs
        requirements: String,
        status: String,
        menuImage: String?,   // URL
        menuText: String
    ): Result<CreateExperienceResponse> {
        return try {
            val token = sharedPrefs.getToken()
            if (token == null) {
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("CreateExperienceRepository", "No token found")
                }
                return Result.failure(Exception("User not authenticated"))
            }

            // 3. Crear el request con URLs
            val exp = CreateExperienceRequest(
                title = title,
                description = description,
                date = date,
                location = location,
                capacity = capacity,
                price = price,
                duration = duration,
                category = category,
                images = images,
                requirements = requirements,
                status = status,
                menu = Menu(
                    image = menuImage ?: "",
                    text = menuText
                )
            )

            Log.d("CreateExperience", "Sending $exp to API")
            val response = apiService.createExperience("Bearer $token", exp)

            if (AppConfig.ENABLE_LOGGING) {
                Log.d("CreateExperienceRepository", "Experience created successfully: ${response.experience._id}")
            }

            Result.success(response)

        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("CreateExperienceRepository", "Error creating experience", e)
            Log.e("CreateExperienceRepository", "API error body: $errorBody")
            Result.failure(Exception("Error creating experience: ${e.message()}\n$errorBody"))
        } catch (e: Exception) {
            if (AppConfig.ENABLE_LOGGING) {
                Log.e("CreateExperienceRepository", "Error creating experience", e)
            }
            Result.failure(Exception("Error creating experience: ${e.message}"))
        }
    }

}