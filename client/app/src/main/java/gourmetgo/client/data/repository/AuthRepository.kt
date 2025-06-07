package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.models.Chef
import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.User
import gourmetgo.client.data.models.dtos.LoginRequest
import gourmetgo.client.data.remote.ApiService

class AuthRepository(
    private val apiService: ApiService,
    private val sharedPrefs: SharedPrefsManager
) {

    suspend fun login(email: String, password: String):Result<User>{
        return if (AppConfig.USE_MOCKUP) {
            //loginWithMockup(email, password)
            Result.success(User())
        } else {
            loginWithApi(email, password)

        }
    }

    private suspend fun loginWithApi(email: String, password: String):Result<User> {
        return try {
            val response = apiService.login(LoginRequest(email, password))

            sharedPrefs.saveToken(response.token)
            sharedPrefs.saveUser(response.user)

            when (response.user.role) {
                "user" -> {
                    mapUserToClient()
                    if (AppConfig.ENABLE_LOGGING)
                        Log.d("AuthRepository", "API login successful for user: ${response.user.email}, role: ${response.user.role}")
                    Result.success(response.user)
                }
                "chef" -> {
                    mapUserToChef()
                    if (AppConfig.ENABLE_LOGGING)
                        Log.d("AuthRepository", "API login successful for user: ${response.user.email}, role: ${response.user.role}")
                    Result.success(response.user)
                }
                else -> {
                    Log.e("AuthRepository","Unknown user type: ${response.user.role}")
                    Result.failure(Exception("Unknown user type: ${response.user.role}"))
                }
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error in API login", e)
            Result.failure(Exception("Error connection server"))
        }
    }

    private suspend fun mapUserToClient() {
        val client = apiService.getClientMe(token = "Bearer ${sharedPrefs.getToken()}" )
        sharedPrefs.saveClient(client)
    }

    private suspend fun mapUserToChef() {
        val chef = apiService.getChefMe(token = "Bearer ${sharedPrefs.getToken()}")
        sharedPrefs.saveChef(chef)
    }

    fun isLoggedIn(): Boolean {
        return sharedPrefs.isLoggedIn()
    }

    fun getCurrentClient(): Client? {
        return try {
            sharedPrefs.getClient()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error getting current client", e)
            null
        }
    }
    fun getCurrentChef(): Chef? {
        return try {
            sharedPrefs.getChef()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error getting current chef", e)
            null
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

    fun logout() {
        if (AppConfig.ENABLE_LOGGING) {
            Log.d("AuthRepository", "Logging out user")
        }
        sharedPrefs.logout()
    }
}


