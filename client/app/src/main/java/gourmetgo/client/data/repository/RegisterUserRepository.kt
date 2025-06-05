package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.mockups.ClientMockup
import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.dtos.RegisterUserRequest
import gourmetgo.client.data.remote.ApiService
import kotlinx.coroutines.delay

class RegisterUserRepository(
    private val apiService: ApiService,
    private val sharedPrefs: SharedPrefsManager
) {

    suspend fun registerUser(
        name: String,
        email: String,
        phone: String,
        identification: String,
        password: String,
        preferences: List<String>,
        photoUrl: String?
    ): Result<Client> {
        return try {
            if (AppConfig.USE_MOCKUP) {
                registerWithMockup(name, email, phone, identification, password, preferences, photoUrl)
            } else {
                registerWithApi(name, email, phone, identification, password, preferences, photoUrl)
            }
        } catch (e: Exception) {
            Log.e("RegisterUserRepository", "Error in register", e)
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    private suspend fun registerWithMockup(
        name: String,
        email: String,
        phone: String,
        identification: String,
        password: String,
        preferences: List<String>,
        photoUrl: String?
    ): Result<Client> {
        return try {
            if (AppConfig.ENABLE_LOGGING) {
                Log.d("RegisterUserRepository", "Attempting mockup registration for: $email")
            }
            delay(AppConfig.MOCK_NETWORK_DELAY)

            // Verificar si el email ya existe
            if (ClientMockup.emailExists(email)) {
                return Result.failure(Exception("El correo electrónico ya está registrado"))
            }

            // Crear nuevo usuario
            val newClient = Client(
                name = name,
                email = email,
                phone = phone,
                identification = identification,
                password = password,
                photoUrl = photoUrl ?: "",
                preferences = preferences
            )

            // Agregar usuario al mockup
            val registeredUser = ClientMockup.addUser(newClient)

            // Generar token falso y auto-login
            val fakeToken = "mock_token_${System.currentTimeMillis()}"
            sharedPrefs.saveToken(fakeToken)
            sharedPrefs.saveUser(registeredUser) 

            Result.success(registeredUser)
        } catch (e: Exception) {
            Log.e("RegisterUserRepository", "Error in mockup registration", e)
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    private suspend fun registerWithApi(
        name: String,
        email: String,
        phone: String,
        identification: String,
        password: String,
        preferences: List<String>,
        photoUrl: String?
    ): Result<Client> {
        return try {
            val registerRequest = RegisterUserRequest(
                name = name,
                email = email,
                phone = phone,
                identification = identification,
                password = password,
                photoUrl = "https://res.cloudinary.com/tu-cloud/image/upload/v1234567890/ejemplo.jpg",
                preferences = preferences
            )
            Log.w("Send user to API", "Sending user to API: $registerRequest")
            val response = apiService.register(registerRequest)
            

            Result.success(response.client)
        } catch (e: Exception) {
            Log.e("RegisterUserRepository", "Error in API registration", e)
            Result.failure(Exception("Error de conexión con el servidor"))
        }
    }
}