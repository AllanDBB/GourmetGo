package gourmetgo.client.utils

import android.content.Context
import android.net.Uri
import gourmetgo.client.data.remote.CloudinaryService

class ImageUploadUtils(private val context: Context) {

    private val cloudinaryService = CloudinaryService(context)

    suspend fun uploadUserImage(imageUri: Uri): Result<String> {
        return cloudinaryService.uploadUserProfileImage(imageUri)
    }

    suspend fun uploadChefImage(imageUri: Uri): Result<String> {
        return cloudinaryService.uploadChefProfileImage(imageUri)
    }
}