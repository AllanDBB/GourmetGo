package gourmetgo.client.data.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

/**
 * CloudinaryService - Servicio para subir imágenes a Cloudinary
 */
class CloudinaryService(private val context: Context) {

    companion object {
        // Credenciales públicas de Cloudinary (funcionan para testing)
        private const val CLOUD_NAME = "dsr48ffu2"
        private const val UPLOAD_PRESET = "gourmetgo_users"
        private const val CLOUDINARY_URL = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request()
            Log.d("CloudinaryService", "🌐 URL: ${request.url}")
            Log.d("CloudinaryService", "📝 Method: ${request.method}")
            val response = chain.proceed(request)
            Log.d("CloudinaryService", "📥 Response: ${response.code}")
            response
        }
        .build()

    /**
     * Sube imagen de perfil de usuario
     * Alias para uploadImage() con logs específicos
     */
    suspend fun uploadUserProfileImage(imageUri: Uri): Result<String> {
        Log.d("CloudinaryService", "👤 Uploading USER profile image...")
        return uploadImage(imageUri)
    }

    /**
     * Sube imagen de perfil de chef
     * Alias para uploadImage() con logs específicos
     */
    suspend fun uploadChefProfileImage(imageUri: Uri): Result<String> {
        Log.d("CloudinaryService", "🧑‍🍳 Uploading CHEF profile image...")
        return uploadImage(imageUri)
    }

    /**
     * Sube imagen a Cloudinary y devuelve la URL
     * Función principal que maneja todas las subidas
     */
    suspend fun uploadImage(imageUri: Uri): Result<String> = withContext(Dispatchers.IO) {
        var tempFile: File? = null

        return@withContext try {
            Log.d("CloudinaryService", "🚀 Starting upload to Cloudinary...")

            // 1. Convertir URI a File temporal
            tempFile = uriToTempFile(imageUri)
            Log.d("CloudinaryService", "📂 Temp file: ${tempFile.absolutePath}")
            Log.d("CloudinaryService", "📏 File size: ${tempFile.length()} bytes")

            if (tempFile.length() == 0L) {
                return@withContext Result.failure(Exception("El archivo está vacío"))
            }

            // 2. Crear request multipart con preset válido
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    tempFile.name,
                    tempFile.asRequestBody("image/*".toMediaType())
                )
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .build()

            val request = Request.Builder()
                .url(CLOUDINARY_URL)
                .post(requestBody)
                .addHeader("User-Agent", "GourmetGo-Android/1.0")
                .build()

            Log.d("CloudinaryService", "📤 Sending request...")
            Log.d("CloudinaryService", "🔗 URL: $CLOUDINARY_URL")
            Log.d("CloudinaryService", "🎛️ Preset: $UPLOAD_PRESET")

            // 3. Ejecutar request
            val response = client.newCall(request).execute()

            response.use { resp ->
                val responseBody = resp.body?.string()

                Log.d("CloudinaryService", "📥 Response code: ${resp.code}")
                Log.d("CloudinaryService", "📄 Response body: ${responseBody?.take(500)}...")

                if (resp.isSuccessful) {
                    val imageUrl = parseCloudinaryResponse(responseBody)

                    if (imageUrl.isNotEmpty()) {
                        Log.d("CloudinaryService", "✅ Upload successful!")
                        Log.d("CloudinaryService", "🔗 Image URL: $imageUrl")
                        Result.success(imageUrl)
                    } else {
                        Log.e("CloudinaryService", "❌ No se pudo extraer URL de la respuesta")
                        Log.e("CloudinaryService", "📄 Full response: $responseBody")
                        Result.failure(Exception("Error al procesar respuesta del servidor"))
                    }
                } else {
                    // Manejo específico de errores HTTP
                    val errorMessage = when (resp.code) {
                        400 -> {
                            Log.e("CloudinaryService", "❌ Bad Request (400)")
                            Log.e("CloudinaryService", "📄 Error details: $responseBody")

                            val cloudinaryError = parseCloudinaryError(responseBody)
                            "Error 400: $cloudinaryError"
                        }
                        401 -> "Error de autenticación (401)"
                        403 -> "Acceso denegado (403)"
                        413 -> "Archivo muy grande (413)"
                        else -> "Error HTTP ${resp.code}"
                    }

                    Result.failure(Exception(errorMessage))
                }
            }
        } catch (e: Exception) {
            Log.e("CloudinaryService", "❌ Unexpected error", e)
            Result.failure(Exception("Error al subir imagen: ${e.message}"))
        } finally {
            // 4. Limpiar archivo temporal
            tempFile?.let {
                if (it.exists()) {
                    it.delete()
                    Log.d("CloudinaryService", "🗑️ Temp file deleted")
                }
            }
        }
    }

    /**
     * Convierte URI a archivo temporal
     */
    private fun uriToTempFile(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("No se puede abrir la imagen")

        val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")

        inputStream.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    }

    /**
     * Extrae URL de la respuesta JSON de Cloudinary
     */
    private fun parseCloudinaryResponse(responseBody: String?): String {
        return try {
            if (responseBody.isNullOrEmpty()) return ""

            val jsonObject = JSONObject(responseBody)
            jsonObject.getString("secure_url")
        } catch (e: Exception) {
            Log.e("CloudinaryService", "❌ Error parsing JSON response", e)
            ""
        }
    }

    /**
     * Extrae mensaje de error específico de Cloudinary
     */
    private fun parseCloudinaryError(responseBody: String?): String {
        return try {
            if (responseBody.isNullOrEmpty()) return "Error desconocido"

            val jsonObject = JSONObject(responseBody)

            when {
                jsonObject.has("error") -> {
                    val error = jsonObject.getJSONObject("error")
                    error.getString("message")
                }
                jsonObject.has("message") -> {
                    jsonObject.getString("message")
                }
                else -> "Error en la configuración de Cloudinary"
            }
        } catch (e: Exception) {
            Log.e("CloudinaryService", "❌ Error parsing error response", e)
            "Error al procesar respuesta de error"
        }
    }
}