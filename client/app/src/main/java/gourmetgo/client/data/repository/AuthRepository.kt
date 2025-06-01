package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.mockups.UserMockup
import gourmetgo.client.data.models.User
import gourmetgo.client.data.models.dtos.LoginRequest
import gourmetgo.client.data.remote.ApiService
import kotlinx.coroutines.delay

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

    private suspend fun loginWithMockup(email: String, password: String): Result<User> {
        return try {
            if (AppConfig.ENABLE_LOGGING) {
                Log.d("AuthRepository", "Attempting mockup login for: $email")
            }
            delay(AppConfig.MOCK_NETWORK_DELAY)

            val user = UserMockup.validateCredentials(email, password)

            if (user != null) {
                val fakeToken = "mock_token_${System.currentTimeMillis()}"

                sharedPrefs.saveToken(fakeToken)
                sharedPrefs.saveUser(user)

                Result.success(user)
            } else {
                Result.failure(Exception("Email o contraseña incorrectos"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error in mockup login", e)
            Result.failure(Exception("Error : ${e.message}"))
        }
    }

    private suspend fun loginWithApi(email: String, password: String): Result<User> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            sharedPrefs.saveToken(response.token)
            sharedPrefs.saveUser(response.user)
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

    fun getCurrentUser(): User? {
        return try {
            sharedPrefs.getUser()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error getting current user", e)
            null
        }
    }

    fun getToken(): String? {
        return try {
            sharedPrefs.getToken()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error getting token", e)
            null
        }
    }
}