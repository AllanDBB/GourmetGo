package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.models.dtos.ChangePasswordRequest
import gourmetgo.client.data.remote.ApiService

class ChangePasswordRepository(
    private val apiService: ApiService,
    private val sharedPrefs: SharedPrefsManager
) {
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<String> {
        return try {
            val token = sharedPrefs.getToken()
            if (token == null) {
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("ChangePasswordRepository", "No token found")
                }
                return Result.failure(Exception("User not authenticated"))
            }

            val request = ChangePasswordRequest(
                currentPassword = currentPassword,
                newPassword = newPassword
            )

            val response = apiService.changePassword("Bearer $token", request)

            if (AppConfig.ENABLE_LOGGING) {
                Log.d("ChangePasswordRepository", "Password changed successfully")
            }

            Result.success(response.message)

        } catch (e: Exception) {
            if (AppConfig.ENABLE_LOGGING) {
                Log.e("ChangePasswordRepository", "Error changing password", e)
            }
            Result.failure(Exception("Error changing password: ${e.message}"))
        }
    }
}