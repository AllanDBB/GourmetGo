package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.models.Rating
import gourmetgo.client.data.models.dtos.RatingRequest
import gourmetgo.client.data.models.dtos.RatingWithUser
import gourmetgo.client.data.remote.ApiService

class RatingRepository(
    private val apiService: ApiService,
    private val sharedPrefs: SharedPrefsManager
) {

    suspend fun createRating(
        experienceId: String,
        score: Int,
        comment: String
    ): Result<Rating> {
        return try {
            val token = sharedPrefs.getToken()
            if (token == null) {
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("RatingRepository", "No token found")
                }
                return Result.failure(Exception("User not authenticated"))
            }

            val request = RatingRequest(
                experienceId = experienceId,
                score = score,
                comment = comment
            )

            val response = apiService.createRating("Bearer $token", request)

            if (AppConfig.ENABLE_LOGGING) {
                Log.d("RatingRepository", "Rating created successfully: ${response.rating._id}")
            }

            Result.success(response.rating)

        } catch (e: Exception) {
            if (AppConfig.ENABLE_LOGGING) {
                Log.e("RatingRepository", "Error creating rating", e)
            }
            Result.failure(Exception("Error creating rating: ${e.message}"))
        }
    }

    suspend fun getExperienceRatings(experienceId: String): Result<List<RatingWithUser>> {
        return try {
            if (AppConfig.ENABLE_LOGGING) {
                Log.d("RatingRepository", "Fetching ratings for experience: $experienceId")
            }

            val ratings = apiService.getExperienceRatings(experienceId)

            if (AppConfig.ENABLE_LOGGING) {
                Log.d("RatingRepository", "Fetched ${ratings.size} ratings")
            }

            Result.success(ratings)

        } catch (e: Exception) {
            if (AppConfig.ENABLE_LOGGING) {
                Log.e("RatingRepository", "Error fetching ratings", e)
            }
            Result.failure(Exception("Error fetching ratings: ${e.message}"))
        }
    }
}