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

class CloudinaryService(private val context: Context) {
    
    companion object {
        private const val CLOUD_NAME = "dsr48ffu2"
        private const val UPLOAD_PRESET = "gourmetgo_users"
        private const val CLOUDINARY_URL = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"
    }
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun uploadUserProfileImage(imageUri: Uri): Result<String> {
        Log.d("CloudinaryService", "Uploading user profile image")
        return uploadImage(imageUri)
    }

    suspend fun uploadChefProfileImage(imageUri: Uri): Result<String> {
        Log.d("CloudinaryService", "Uploading chef profile image")
        return uploadImage(imageUri)
    }

    suspend fun uploadImage(imageUri: Uri): Result<String> = withContext(Dispatchers.IO) {
        var tempFile: File? = null
        
        try {
            Log.d("CloudinaryService", "Starting image upload")
            
            tempFile = createTempFileFromUri(imageUri)
            
            if (tempFile.length() == 0L) {
                return@withContext Result.failure(Exception("File is empty"))
            }
            
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
                .build()

            val response = client.newCall(request).execute()
            
            response.use { resp ->
                val responseBody = resp.body?.string()
                
                if (resp.isSuccessful) {
                    val imageUrl = extractImageUrl(responseBody)
                    
                    if (imageUrl.isNotEmpty()) {
                        Log.d("CloudinaryService", "Upload successful: $imageUrl")
                        Result.success(imageUrl)
                    } else {
                        Log.e("CloudinaryService", "Could not extract URL from response")
                        Result.failure(Exception("Failed to process server response"))
                    }
                } else {
                    val errorMessage = handleHttpError(resp.code, responseBody)
                    Log.e("CloudinaryService", "Upload failed: $errorMessage")
                    Result.failure(Exception(errorMessage))
                }
            }
        } catch (e: Exception) {
            Log.e("CloudinaryService", "Upload error", e)
            Result.failure(Exception("Upload failed: ${e.message}"))
        } finally {
            tempFile?.let {
                if (it.exists()) {
                    it.delete()
                }
            }
        }
    }

    private fun createTempFileFromUri(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Cannot open image")
        
        val tempFile = File(context.cacheDir, "temp_${System.currentTimeMillis()}.jpg")
        
        inputStream.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        
        return tempFile
    }

    private fun extractImageUrl(responseBody: String?): String {
        return try {
            if (responseBody.isNullOrEmpty()) return ""
            
            val jsonObject = JSONObject(responseBody)
            jsonObject.getString("secure_url")
        } catch (e: Exception) {
            Log.e("CloudinaryService", "Error parsing response", e)
            ""
        }
    }

    private fun handleHttpError(code: Int, responseBody: String?): String {
        return when (code) {
            400 -> {
                val errorDetail = parseErrorMessage(responseBody)
                "Bad request: $errorDetail"
            }
            401 -> "Authentication failed"
            403 -> "Access denied"
            413 -> "File too large"
            else -> "HTTP error $code"
        }
    }

    private fun parseErrorMessage(responseBody: String?): String {
        return try {
            if (responseBody.isNullOrEmpty()) return "Unknown error"
            
            val jsonObject = JSONObject(responseBody)
            
            when {
                jsonObject.has("error") -> {
                    val error = jsonObject.getJSONObject("error")
                    error.getString("message")
                }
                jsonObject.has("message") -> {
                    jsonObject.getString("message")
                }
                else -> "Configuration error"
            }
        } catch (e: Exception) {
            "Error parsing response"
        }
    }
}