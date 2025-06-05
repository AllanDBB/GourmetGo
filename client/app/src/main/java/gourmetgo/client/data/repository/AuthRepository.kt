package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager
//import gourmetgo.client.data.mockups.UserMockup
import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.User
import gourmetgo.client.data.models.dtos.LoginRequest
import gourmetgo.client.data.remote.ApiService

class AuthRepository(
    private val apiService: ApiService,
    private val sharedPrefs: SharedPrefsManager
) {

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            if (AppConfig.USE_MOCKUP) {
                loginWithMockup(email, password)
            } else {
                loginWithApi(email, password)
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error in login", e)
            Result.failure(Exception("Error : ${e.message}"))
        }
    }

    private  fun loginWithMockup(email: String, password: String): Result<User> {
       return  Result.success(User())
    }

    private suspend fun loginWithApi(email: String, password: String): Result<User> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            sharedPrefs.saveToken(response.token)
            sharedPrefs.saveUser(apiService.getMe(response.token))
            Result.success(response.user)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error in API login", e)
            Result.failure(Exception("Error connection server"))
        }
    }

    fun logout() {
        try {
            sharedPrefs.logout()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error in logout", e)
        }
    }

    fun isLoggedIn(): Boolean {
        return try {
            sharedPrefs.isLoggedIn()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error checking login status", e)
            false
        }
    }

    fun getCurrentUser(): Client? {
        return try {
            sharedPrefs.getUser()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error getting current user", e)
            null
        }
    }


    fun updateUserLocally(user: Client) {
        try {
            sharedPrefs.saveUser(user)
            Log.d("AuthRepository", "User updated locally: ${user.name}")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error updating user locally", e)
            throw Exception("Error al guardar datos localmente")
        }
    }
}