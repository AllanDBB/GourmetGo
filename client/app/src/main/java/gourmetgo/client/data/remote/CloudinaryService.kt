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
import gourmetgo.client.AppConfig

class CloudinaryService(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request()
            if(AppConfig.ENABLE_LOGGING){
                Log.d("CloudinaryService", "🌐 URL: ${request.url}")
                Log.d("CloudinaryService", "📝 Method: ${request.method}")
            }
            val response = chain.proceed(request)
            if(AppConfig.ENABLE_LOGGING)
                Log.d("CloudinaryService", "📥 Response: ${response.code}")
            response
        }
        .build()


    suspend fun uploadUserProfileImage(imageUri: Uri): Result<String> {
        if(AppConfig.ENABLE_LOGGING)
            Log.d("CloudinaryService", "👤 Uploading USER profile image...")
        return uploadImage(imageUri)
    }

    suspend fun uploadChefProfileImage(imageUri: Uri): Result<String> {
        if(AppConfig.ENABLE_LOGGING)
            Log.d("CloudinaryService", "🧑‍🍳 Uploading CHEF profile image...")
        return uploadImage(imageUri)
    }

    private suspend fun uploadImage(imageUri: Uri): Result<String> = withContext(Dispatchers.IO) {
        var tempFile: File? = null

        return@withContext try {
            if(AppConfig.ENABLE_LOGGING)
                Log.d("CloudinaryService", "🚀 Starting upload to Cloudinary...")

            tempFile = uriToTempFile(imageUri)
            if(AppConfig.ENABLE_LOGGING) {
                Log.d("CloudinaryService", "📂 Temp file: ${tempFile.absolutePath}")
                Log.d("CloudinaryService", "📏 File size: ${tempFile.length()} bytes")
            }

            if (tempFile.length() == 0L) {
                return@withContext Result.failure(Exception("El archivo está vacío"))
            }

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    tempFile.name,
                    tempFile.asRequestBody("image/*".toMediaType())
                )
                .addFormDataPart("upload_preset", AppConfig.UPLOAD_PRESET)
                .build()

            val request = Request.Builder()
                .url(AppConfig.CLOUDINARY_URL)
                .post(requestBody)
                .addHeader("User-Agent", "GourmetGo-Android/1.0")
                .build()
            if(AppConfig.ENABLE_LOGGING) {
                Log.d("CloudinaryService", "📤 Sending request...")
                Log.d("CloudinaryService", "🔗 URL: $AppConfig.CLOUDINARY_URL")
                Log.d("CloudinaryService", "🎛️ Preset: $AppConfig.UPLOAD_PRESET")
            }
            val response = client.newCall(request).execute()

            response.use { resp ->
                val responseBody = resp.body?.string()
                if(AppConfig.ENABLE_LOGGING) {
                    Log.d("CloudinaryService", "📥 Response code: ${resp.code}")
                    Log.d("CloudinaryService", "📄 Response body: ${responseBody?.take(500)}...")
                }
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
            tempFile?.let {
                if (it.exists()) {
                    it.delete()
                    if(AppConfig.ENABLE_LOGGING)
                        Log.d("CloudinaryService", "🗑️ Temp file deleted")
                }
            }
        }
    }


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