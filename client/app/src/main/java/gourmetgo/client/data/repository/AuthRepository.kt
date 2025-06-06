package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.models.Chef
import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.User
import gourmetgo.client.data.models.dtos.LoginRequest
import gourmetgo.client.data.models.dtos.UpdateChefRequest
import gourmetgo.client.data.models.dtos.UpdateClientRequest
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
    suspend fun updateClientProfile(client: Client): Result<Client> {
        return try {
            if (AppConfig.USE_MOCKUP) {
                Result.success(Client())
            } else {
                updateClientWithApi(client)
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error updating user profile", e)
            Result.failure(Exception("Error updating user profile: ${e.message}"))
        }
    }

    suspend fun updateChefProfile(chef: Chef): Result<Chef> {
        return try {
            if (AppConfig.USE_MOCKUP) {
                Result.success(Chef())
            } else {
                updateChefWithApi(chef)
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error updating user profile", e)
            Result.failure(Exception("Error updating user profile: ${e.message}"))
        }
    }

    private suspend fun updateClientWithApi(client: Client): Result<Client> {
        return try {
            val token = sharedPrefs.getToken()
            if (token == null) {
                Log.e("AuthRepository", "No token found for API update")
                return Result.failure(Exception("No token found for API update"))
            }

            val updateRequest = UpdateClientRequest(
                email = client.email,
                phone = client.phone,
                identification = client.identification,
                photoUrl = client.avatar,
                preferences = client.preferences
            )

            val updatedClient = apiService.updateClientProfile("Bearer $token", updateRequest)

            sharedPrefs.saveClient(updatedClient)
            if (AppConfig.ENABLE_LOGGING)
                Log.d("AuthRepository", "User updated via API: ${updatedClient.name}")
            Result.success(updatedClient)

        } catch (e: Exception) {
            Log.e("AuthRepository", "Error in API updateUser", e)
            Result.failure(Exception("Error in API updateUser"))
        }
    }

    private suspend fun updateChefWithApi(chef: Chef): Result<Chef> {
        return try {
            val token = sharedPrefs.getToken()
            if (token == null) {
                Log.e("AuthRepository", "No token found for API update")
                return Result.failure(Exception("No token found for API update"))
            }

            val updateRequest = UpdateChefRequest(
                contactPerson = chef.contactPerson,
                email = chef.email,
                phone = chef.phone,
                location = chef.location,
                photoUrl = chef.photoUrl,
                cuisineType = chef.preferences[0] // tipo cocina
            )

            val updatedChef = apiService.updateChefProfile("Bearer $token", updateRequest)

            sharedPrefs.saveChef(updatedChef)
            if (AppConfig.ENABLE_LOGGING)
                Log.d("AuthRepository", "User updated via API: ${updatedChef.name}")
            Result.success(updatedChef)

        } catch (e: Exception) {
            Log.e("AuthRepository", "Error in API updateUser", e)
            Result.failure(Exception("Error in API updateUser"))
        }
    }

}