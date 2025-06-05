package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.mockups.ChefMockup
import gourmetgo.client.data.models.Chef
import gourmetgo.client.data.requests.RegisterChefRequest
import gourmetgo.client.data.remote.ApiService
import kotlinx.coroutines.delay

class RegisterChefRepository(
    private val apiService: ApiService,
    private val sharedPrefs: SharedPrefsManager
) {

    suspend fun registerChef(
        name: String,
        contactPerson: String,
        email: String,
        phone: String,
        location: String,
        cuisinetype: String,
        password: String,
        bio: String,
        experience: String,
        sociallinks: List<String>,
        photoUrl: String?
    ): Result<Chef> {
        return try {
            if (AppConfig.USE_MOCKUP) {
                registerWithMockup(name, contactPerson, email, phone, location, cuisinetype, password, bio, experience, sociallinks, photoUrl)
            } else {
                registerWithApi(name, contactPerson, email, phone, location, cuisinetype, password, bio, experience, sociallinks, photoUrl)
            }
        } catch (e: Exception) {
            Log.e("RegisterChefRepository", "Error in register", e)
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    private suspend fun registerWithMockup(
        name: String,
        contactPerson: String,
        email: String,
        phone: String,
        location: String,
        cuisinetype: String,
        password: String,
        bio: String,
        experience: String,
        sociallinks: List<String>,
        photoUrl: String?
    ): Result<Chef> {
        return try {
            if (AppConfig.ENABLE_LOGGING) {
                Log.d("RegisterChefRepository", "Attempting mockup registration for: $email")
            }
            delay(AppConfig.MOCK_NETWORK_DELAY)

            // Verificar si el email ya existe
            if (ChefMockup.emailExists(email)) {
                return Result.failure(Exception("El correo electrónico ya está registrado"))
            }

            // Crear nuevo chef
            val newChef = Chef(
                name = name,
                contactPerson = contactPerson,
                email = email,
                phone = phone,
                location = location,
                cuisineType = cuisinetype,
                password = password,
                photoUrl = photoUrl ?: "",
                bio = bio,
                experience = experience,
                socialLinks = sociallinks
            )

            // Agregar chef al mockup
            val registeredChef = ChefMockup.addChef(newChef)

            // Generar token falso y auto-login
            val fakeToken = "mock_chef_token_${System.currentTimeMillis()}"
            sharedPrefs.saveToken(fakeToken)
            sharedPrefs.saveChef(registeredChef) 

            Result.success(registeredChef)
        } catch (e: Exception) {
            Log.e("RegisterChefRepository", "Error in mockup registration", e)
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    private suspend fun registerWithApi(
        name: String,
        contactPerson: String,
        email: String,
        phone: String,
        location: String,
        cuisinetype: String,
        password: String,
        bio: String,
        experience: String,
        sociallinks: List<String>,
        photoUrl: String?
    ): Result<Chef> {
        return try {
            val registerRequest = RegisterChefRequest(
                name = name,
                contactPerson = contactPerson,
                email = email,
                phone = phone,
                location = location,
                cuisinetype = cuisinetype,
                password = password,
                photoUrl = photoUrl ?: "",
                bio = bio,
                experience = experience,
                sociallinks = sociallinks
            )

            val response = apiService.registerChef(registerRequest)
            
            // Auto-login después del registro exitoso
            sharedPrefs.saveToken(response.token)
            sharedPrefs.saveChef(response.chef) 
            
            Result.success(response.chef)
        } catch (e: Exception) {
            Log.e("RegisterChefRepository", "Error in API registration", e)
            Result.failure(Exception("Error de conexión con el servidor"))
        }
    }
}