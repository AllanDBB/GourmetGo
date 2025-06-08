package gourmetgo.client.data.repository

import android.net.Uri
import android.util.Log
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.models.Chef
import gourmetgo.client.data.models.dtos.RegisterChefRequest
import gourmetgo.client.data.remote.ApiService
import gourmetgo.client.data.remote.CloudinaryService

class RegisterChefRepository(
    private val apiService: ApiService,
    private val sharedPrefs: SharedPrefsManager,
    private val cloudinaryService: CloudinaryService
) {

    suspend fun registerChef(
        name: String,
        contactPerson: String,
        email: String,
        phone: String,
        location: String,
        cuisineType: String,        // ← CORREGIDO: cuisinetype → cuisineType
        password: String,
        bio: String,
        experience: String,
        socialLinks: List<String>,  // ← CORREGIDO: sociallinks → socialLinks
        photoUri: Uri?
    ): Result<Chef> {
        return try {
            // 1. Subir imagen a Cloudinary PRIMERO
            val photoUrl = if (photoUri != null) {
                Log.d("RegisterChefRepository", "Uploading chef image to Cloudinary...")
                
                cloudinaryService.uploadChefProfileImage(photoUri)
                    .onSuccess { url ->
                        Log.d("RegisterChefRepository", "Chef image uploaded successfully: $url")
                    }
                    .onFailure { error ->
                        Log.e("RegisterChefRepository", "Failed to upload chef image", error)
                        return Result.failure(Exception("Error al subir la imagen: ${error.message}"))
                    }
                    .getOrThrow()
            } else {
                ""
            }

            // 2. Registrar chef con URL de Cloudinary
            val registerRequest = RegisterChefRequest(
                name = name,
                contactPerson = contactPerson,
                email = email,
                phone = phone,
                location = location,
                cuisineType = cuisineType,     // ← CORREGIDO: nombres coinciden
                password = password,
                photoUrl = photoUrl,
                bio = bio,
                experience = experience,
                socialLinks = socialLinks      // ← CORREGIDO: nombres coinciden
            )
            
            Log.d("RegisterChefRepository", "Sending chef to API with photo URL: $photoUrl")
            val response = apiService.registerChef(registerRequest)



            Result.success(response.chef)
        } catch (e: Exception) {
            Log.e("RegisterChefRepository", "❌ Unexpected error in chef API registration", e)
            Result.failure(Exception("Error de conexión con el servidor: ${e.message}"))
        }
    }
}