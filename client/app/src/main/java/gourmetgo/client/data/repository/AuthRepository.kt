package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.mockups.UserMockup
import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.Chef
import gourmetgo.client.data.models.User
import gourmetgo.client.data.models.dtos.LoginRequest
import gourmetgo.client.data.remote.ApiService
import kotlinx.coroutines.delay

class AuthRepository(
    private val apiService: ApiService,
    private val sharedPrefs: SharedPrefsManager
) {

    suspend fun login(email: String, password: String): Result<Any> {
        return if (AppConfig.USE_MOCKUP) {
            loginWithMockup(email, password)
        } else {
            loginWithApi(email, password)
        }
    }

    private suspend fun loginWithMockup(email: String, password: String): Result<Any> {
        return try {
            if (AppConfig.ENABLE_LOGGING) {
                Log.d("AuthRepository", "Attempting mockup login for: $email")
            }
            
            delay(AppConfig.MOCK_NETWORK_DELAY)

            val user = UserMockup.validateCredentials(email, password)

            if (user != null) {
                // Generar token falso
                val fakeToken = "mock_token_${System.currentTimeMillis()}"
                sharedPrefs.saveToken(fakeToken)

                // Mapear según el rol
                when (user.role) {
                    "user" -> {
                        val client = UserMockup.mapUserToClient(user)
                        if (client != null) {
                            sharedPrefs.saveUser(client)
                            Result.success(client)
                        } else {
                            Result.failure(Exception("Error al obtener datos del usuario"))
                        }
                    }
                    "chef" -> {
                        val chef = UserMockup.mapUserToChef(user) 
                        if (chef != null) {
                            sharedPrefs.saveChef(chef)
                            Result.success(chef)
                        } else {
                            Result.failure(Exception("Error al obtener datos del chef"))
                        }
                    }
                    else -> Result.failure(Exception("Tipo de usuario no válido: ${user.role}"))
                }
            } else {
                Result.failure(Exception("Email o contraseña incorrectos"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error in mockup login", e)
            Result.failure(Exception("Error: ${e.message}"))
        }
    }


    private suspend fun loginWithApi(email: String, password: String): Result<Any> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            
            if (AppConfig.ENABLE_LOGGING) {
                Log.d("AuthRepository", "API login successful for user: ${response.user.email}, role: ${response.user.role}")
            }
            
            // Guardar token JWT real
            sharedPrefs.saveToken(response.token)
            
            // Mapear según el rol del usuario
            when (response.user.role) {
                "user" -> {
                    val client = mapUserToClient(response.user)
                    sharedPrefs.saveUser(client)
                    Result.success(client)
                }
                "chef" -> {
                    val chef = mapUserToChef(response.user)
                    sharedPrefs.saveChef(chef)
                    Result.success(chef)
                }
                else -> {
                    Result.failure(Exception("Tipo de usuario no válido: ${response.user.role}"))
                }
            }
        } catch (e: retrofit2.HttpException) {
            Log.e("AuthRepository", "HTTP Error in API login", e)
            
            val errorMessage = when (e.code()) {
                400 -> "Credenciales inválidas"
                500 -> "Error interno del servidor"
                else -> "Error de conexión: ${e.message()}"
            }
            
            Result.failure(Exception(errorMessage))
        } catch (e: java.net.UnknownHostException) {
            Log.e("AuthRepository", "Network error", e)
            Result.failure(Exception("Sin conexión a internet"))
        } catch (e: java.net.SocketTimeoutException) {
            Log.e("AuthRepository", "Timeout error", e)
            Result.failure(Exception("Tiempo de espera agotado"))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Unexpected error in API login", e)
            Result.failure(Exception("Error de conexión con el servidor"))
        }
    }


    private fun mapUserToClient(user: User): Client {
        return Client(
            name = user.name,
            email = user.email,
            phone = "",           
            identification = "",  
            photoUrl = "",        
            preferences = emptyList() 
        )
    }


    private fun mapUserToChef(user: User): Chef {
        return Chef(
            name = user.name,
            contactPerson = "",   
            email = user.email,
            phone = "",          
            location = "",       
            cuisineType = "",   
            photoUrl = "",       
            bio = "",          
            experience = "",  
            socialLinks = emptyList() 
        )
    }


    fun isLoggedIn(): Boolean {
        return sharedPrefs.isLoggedIn()
    }


    fun getCurrentUser(): Any? {
        return try {
            // Intentar obtener como usuario normal primero
            sharedPrefs.getUser() ?: sharedPrefs.getChef()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error getting current user", e)
            null
        }
    }


    fun getCurrentUserType(): String? {
        return when {
            sharedPrefs.getUser() != null -> "user"
            sharedPrefs.getChef() != null -> "chef"
            else -> null
        }
    }


    fun getToken(): String? {
        return sharedPrefs.getToken()
    }

    fun logout() {
        if (AppConfig.ENABLE_LOGGING) {
            Log.d("AuthRepository", "Logging out user")
        }
        sharedPrefs.logout()
    }

    suspend fun checkLoginStatus(): Result<Any?> {
        return try {
            val token = getToken()
            
            if (token.isNullOrEmpty()) {
                Result.success(null) 
            } else {
                val currentUser = getCurrentUser()
                if (currentUser != null) {
                    Result.success(currentUser)
                } else {
                    logout()
                    Result.success(null)
                }
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error checking login status", e)
            logout()
            Result.success(null)
        }
    }
}