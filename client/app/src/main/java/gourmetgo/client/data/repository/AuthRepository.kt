package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.User
import gourmetgo.client.data.models.dtos.LoginRequest
import gourmetgo.client.data.models.dtos.UpdateUserRequest
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

    private fun loginWithMockup(email: String, password: String): Result<User> {
        return Result.success(User())
    }

    private suspend fun loginWithApi(email: String, password: String): Result<User> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            sharedPrefs.saveToken(response.token)
            val client = apiService.getMe("Bearer ${response.token}")
            sharedPrefs.saveUser(client)
            Result.success(response.user)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error in API login", e)
            Result.failure(Exception("Error connection server"))
        }
    }

    suspend fun updateUserProfile(user: Client): Result<Client> {
        return try {
            if (AppConfig.USE_MOCKUP) {
                updateUserWithMockup(user)
            } else {
                updateUserWithApi(user)
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error updating user profile", e)
            Result.failure(Exception("Error al actualizar perfil: ${e.message}"))
        }
    }

    private suspend fun updateUserWithMockup(user: Client): Result<Client> {
        return try {
            // Simulate network delay
            kotlinx.coroutines.delay(AppConfig.MOCK_NETWORK_DELAY)

            // Update local storage
            sharedPrefs.saveUser(user)
            Log.d("AuthRepository", "User updated with mockup: ${user.name}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error in mockup updateUser", e)
            Result.failure(e)
        }
    }

    private suspend fun updateUserWithApi(user: Client): Result<Client> {
        return try {
            val token = sharedPrefs.getToken()
            if (token == null) {
                Log.e("AuthRepository", "No token found for API update")
                return Result.failure(Exception("Token no encontrado. Inicia sesión nuevamente."))
            }

            val updateRequest = UpdateUserRequest(
                email = user.email,
                phone = user.phone,
                identification = user.dni,
                photoUrl = user.avatar.takeIf { it.isNotBlank() },
                preferences = user.preferences
            )

            val updatedUser = apiService.updateProfile("Bearer $token", updateRequest)

            // Update local storage with the response from server
            sharedPrefs.saveUser(updatedUser)
            Log.d("AuthRepository", "User updated via API: ${updatedUser.name}")
            Result.success(updatedUser)

        } catch (e: Exception) {
            Log.e("AuthRepository", "Error in API updateUser", e)
            Result.failure(Exception("Error al conectar con el servidor"))
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

    // Keep this method for local updates (backward compatibility)
    fun updateUser(user: Client) {
        try {
            sharedPrefs.saveUser(user)
            Log.d("AuthRepository", "User updated locally: ${user.name}")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error updating user locally", e)
            throw Exception("Error al guardar datos localmente")
        }
    }
}