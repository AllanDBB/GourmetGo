package gourmetgo.client.data.repository

import android.net.Uri
import android.util.Log
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.dtos.RegisterUserRequest
import gourmetgo.client.data.remote.ApiService
import gourmetgo.client.data.remote.CloudinaryService

class RegisterUserRepository(
    private val apiService: ApiService,
    private val sharedPrefs: SharedPrefsManager,
    private val cloudinaryService: CloudinaryService
) {

    suspend fun registerUser(
        name: String,
        email: String,
        phone: String,
        identification: String,
        password: String,
        preferences: List<String>,
        photoUri: Uri?
    ): Result<Client> {
        return try {
            // 1. Subir imagen a Cloudinary PRIMERO
            val photoUrl = if (photoUri != null) {
                Log.d("RegisterUserRepository", "Uploading image to Cloudinary...")

                cloudinaryService.uploadUserProfileImage(photoUri)
                    .onSuccess { url ->
                        Log.d("RegisterUserRepository", "Image uploaded successfully: $url")
                    }
                    .onFailure { error ->
                        Log.e("RegisterUserRepository", "Failed to upload image", error)
                        return Result.failure(Exception("Error al subir la imagen: ${error.message}"))
                    }
                    .getOrThrow()
            } else {
                ""
            }

            // 2. Registrar usuario con URL de Cloudinary
            val registerRequest = RegisterUserRequest(
                name = name,
                email = email,
                phone = phone,
                identification = identification,
                password = password,
                photoUrl = photoUrl,
                preferences = preferences
            )

            Log.d("RegisterUserRepository", "Sending user to API with photo URL: $photoUrl")
            val response = apiService.register(registerRequest)


            Result.success(response.client)
        } catch (e: Exception) {
            Log.e("RegisterUserRepository", "Error in API registration", e)
            Result.failure(Exception("Error de conexión con el servidor: ${e.message}"))
        }
    }
}