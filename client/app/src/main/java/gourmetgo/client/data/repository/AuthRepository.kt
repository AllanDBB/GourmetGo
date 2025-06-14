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
import kotlinx.coroutines.delay

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
    }    private suspend fun loginWithApi(email: String, password: String):Result<User> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            
            sharedPrefs.saveToken(response.token)
            sharedPrefs.saveUser(response.user)

            when (response.user.role) {                "user" -> {
                    mapUserToClient()
                    // Asegurar que los datos estén completamente guardados antes de continuar
                    val savedClient = getCurrentClient()
                    if (savedClient != null) {
                        if (AppConfig.ENABLE_LOGGING)
                            Log.d("AuthRepository", "API login successful for user: ${response.user.email}, role: ${response.user.role}")
                        Result.success(response.user)
                    } else {
                        Log.e("AuthRepository", "Failed to save client data properly")
                        Result.failure(Exception("Error guardando datos del usuario"))
                    }
                }                "chef" -> {
                    mapUserToChef()
                    
                    // Delay adicional para asegurar sincronización
                    delay(200)
                    
                    // Verificación final
                    val savedChef = getCurrentChef()
                    if (savedChef != null) {
                        if (AppConfig.ENABLE_LOGGING)
                            android.util.Log.d("AuthRepository", "API login successful for chef: ${response.user.email}, role: ${response.user.role}, id: ${response.user._id}")
                        Result.success(response.user)
                    } else {
                        android.util.Log.e("AuthRepository", "Failed to save chef data properly - datos no sincronizados")
                        Result.failure(Exception("Error guardando datos del chef - sincronización fallida"))
                    }
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
    }    private suspend fun mapUserToClient() {
        try {
            Log.d("AuthRepository", "Getting client data from API...")
            val client = apiService.getClientMe(token = "Bearer ${sharedPrefs.getToken()}")
            Log.d("AuthRepository", "Client received from API: $client")
            
            // Guardar cliente
            sharedPrefs.saveClient(client)
            Log.d("AuthRepository", "Client saved to SharedPrefs")
            
            // Verificar que se guardó correctamente
            val savedClient = sharedPrefs.getClient()
            if (savedClient != null) {
                Log.d("AuthRepository", "Client verification successful: ${savedClient.name}")
            } else {
                Log.e("AuthRepository", "Client verification failed - client is null after save")
                throw Exception("Failed to save client data")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error in mapUserToClient", e)
            throw e
        }
    }    private suspend fun mapUserToChef() {
        try {
            android.util.Log.d("AuthRepository", "Getting chef data from API...")
            val chef = apiService.getChefMe(token = "Bearer ${sharedPrefs.getToken()}")
            android.util.Log.d("AuthRepository", "Chef received from API: $chef")
            
            // Guardar chef
            sharedPrefs.saveChef(chef)
            android.util.Log.d("AuthRepository", "Chef saved to SharedPrefs")
              // Verificación múltiple para asegurar sincronización
            var savedChef: gourmetgo.client.data.models.Chef? = null
            var attempts = 0
            while (savedChef == null && attempts < 8) {
                delay(75) // Delay incrementado
                savedChef = sharedPrefs.getChef()
                attempts++
                if (savedChef == null) {
                    android.util.Log.d("AuthRepository", "Intento $attempts: chef aún no disponible, reintentando...")
                    
                    // Si fallan varios intentos, volver a guardar
                    if (attempts % 3 == 0) {
                        android.util.Log.d("AuthRepository", "Reintentando guardar chef en intento $attempts")
                        sharedPrefs.saveChef(chef)
                        delay(100)
                    }
                }
            }
            
            if (savedChef != null) {
                android.util.Log.d("AuthRepository", "Chef verification successful after $attempts attempts: ${savedChef.name}")
            } else {
                android.util.Log.e("AuthRepository", "Chef verification failed after $attempts attempts")
                throw Exception("Failed to save chef data after multiple attempts")
            }
            
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Error in mapUserToChef", e)
            throw e
        }
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
    }    fun getCurrentChef(): Chef? {
        return try {
            val chef = sharedPrefs.getChef()
            if (chef == null) {
                // Si el chef es null, intentar forzar una recarga con múltiples intentos
                val user = sharedPrefs.getUser()
                if (user?.role == "chef") {
                    android.util.Log.d("AuthRepository", "Chef is null, intentando recarga...")
                    
                    // Múltiples intentos de recarga con delays incrementales
                    for (attempt in 1..3) {
                        Thread.sleep(50L * attempt) // Delay incremental
                        val reloadedChef = sharedPrefs.getChef()
                        if (reloadedChef != null) {
                            android.util.Log.d("AuthRepository", "Chef recargado exitosamente en intento $attempt")
                            return reloadedChef
                        }
                        android.util.Log.d("AuthRepository", "Intento $attempt fallido, reintentando...")
                    }
                    android.util.Log.e("AuthRepository", "No se pudo recargar el chef después de 3 intentos")
                }
            }
            chef
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Error getting current chef", e)
            null
        }
    }

    fun hasActiveSession(): Boolean {
        return try {
            val token = sharedPrefs.getToken()
            val user = sharedPrefs.getUser()
            !token.isNullOrEmpty() && user != null
        } catch (e: Exception) {
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
                avatar = client.avatar,
                preferences = client.preferences
            )

            val updatedClient = apiService.updateClientProfile("Bearer $token", updateRequest).user

            sharedPrefs.saveClient(updatedClient)

            val currentUser = sharedPrefs.getUser()
            currentUser?.let { user ->
                val updatedUser = user.copy(
                    name = updatedClient.name,
                    email = updatedClient.email
                )
                sharedPrefs.saveUser(updatedUser)
            }

            if (AppConfig.ENABLE_LOGGING)
                Log.d("AuthRepository", "User updated via API: ${sharedPrefs.getUser()?.name}")
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
                avatar = chef.avatar,
                cuisineType = chef.preferences[0]
            )
            val updatedChef = apiService.updateChefProfile("Bearer ${sharedPrefs.getToken()}", updateRequest).chef
            //TODO:API return something that does not match with chef model
            sharedPrefs.saveChef(chef)

            val currentUser = sharedPrefs.getUser()
            currentUser?.let { user ->
                val updatedUser = user.copy(
                    name = chef.name,
                    email = chef.email
                )
                sharedPrefs.saveUser(updatedUser)
            }

            if (AppConfig.ENABLE_LOGGING)
                Log.d("AuthRepository", "User updated via API: ${ sharedPrefs.getUser()?.name}")
            Result.success(updatedChef)

        } catch (e: Exception) {
            Log.e("AuthRepository", "Error in API updateUser", e)
            Result.failure(Exception("Error in API updateUser"))
        }
    }

}