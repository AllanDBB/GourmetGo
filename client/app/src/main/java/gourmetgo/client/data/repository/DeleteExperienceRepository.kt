package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.models.dtos.LoginRequest
import gourmetgo.client.data.models.dtos.DeleteExperienceRequest
import gourmetgo.client.data.models.dtos.RequestDeleteRequest
import gourmetgo.client.data.remote.ApiService
import kotlinx.coroutines.delay
import retrofit2.HttpException

class DeleteExperienceRepository(
    private val apiService: ApiService,
    private val sharedPrefs: SharedPrefsManager
) {
    suspend fun requestDelete(experienceId: String, mail: String): Result<String> {
        return try {
            val request = RequestDeleteRequest(email = mail)
            requestDeleteWithApi(experienceId, request)
        } catch (e: Exception) {
            Log.e("DeleteExperienceRepository", "Error in deleteExperience", e)
            Result.failure(Exception("Error : ${e.message}"))
        }
    }

    private suspend fun requestDeleteWithApi(experienceId: String, request: RequestDeleteRequest): Result<String> {
        val token = sharedPrefs.getToken()
        if (token == null) {
            if (AppConfig.ENABLE_LOGGING) {
                Log.e("DeleteExperienceRepository", "No token found")
            }
            return Result.failure(Exception("User not authenticated"))
        }
        return try {
            Log.d("DeleteExperienceRepository", "Requesting delete for experience ID: $experienceId")
            apiService.requestExperienceDelete("Bearer $token", experienceId, request)
            Log.d("DeleteExperienceRepository", "Successfully requested delete for experience with ID: $experienceId")
            Result.success("Verification code sent to email")
        } catch (e: HttpException) {
            if (e.code() == 403) {
                Result.failure(Exception("Correo incorrecto"))
            } else {
                Log.e("DeleteExperienceRepository", "Error in API request-delete", e)
                Result.failure(Exception("Error : ${e.message()}"))
            }
        } catch (e: Exception) {
            Log.e("DeleteExperienceRepository", "Error in API request-delete", e)
            Result.failure(Exception("Error : ${e.message}"))
        }
    }    suspend fun deleteExperience(experienceId: String, mail: String, code: String): Result<String> {
        val token = sharedPrefs.getToken()
        if (token == null) {
            if (AppConfig.ENABLE_LOGGING) {
                Log.e("DeleteExperienceRepository", "No token found")
            }
            return Result.failure(Exception("User not authenticated"))
        }
        return try {
            Log.d("DeleteExperienceRepository", "Starting delete process for experience ID: $experienceId")
            deleteExperienceWithApi(experienceId, mail, code)
        } catch (e: Exception) {
            Log.e("DeleteExperienceRepository", "Error in deleteExperience", e)
            Result.failure(Exception("Error : ${e.message}"))
        }
    }

    private suspend fun deleteExperienceWithApi(experienceId: String, mail: String, code: String): Result<String> {
        val token = sharedPrefs.getToken()
        if (token == null) {
            if (AppConfig.ENABLE_LOGGING) {
                Log.e("DeleteExperienceRepository", "No token found")
            }
            return Result.failure(Exception("User not authenticated"))
        }
        return try {
            Log.d("DeleteExperienceRepository", "Deleting experience with ID: $experienceId")
            val request = DeleteExperienceRequest(email = mail, code = code)
            apiService.deleteExperience("Bearer $token", experienceId, request)
            Log.d("DeleteExperienceRepository", "Successfully deleted experience with ID: $experienceId")
            Result.success("Experience deleted successfully")
        } catch (e: HttpException) {
            if (e.code() == 400) {
                Result.failure(Exception("Código de verificación incorrecto o expirado, intenta de nuevo."))
            } else {
                Log.e("DeleteExperienceRepository", "Error in API delete", e)
                Result.failure(Exception("Error : ${e.message()}"))
            }
        } catch (e: Exception) {
            Log.e("DeleteExperienceRepository", "Error in API delete", e)
            Result.failure(Exception("Error : ${e.message}"))
        }
    }

}
