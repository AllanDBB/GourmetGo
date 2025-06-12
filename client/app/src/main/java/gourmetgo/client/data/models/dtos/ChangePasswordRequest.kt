package gourmetgo.client.data.models.dtos

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)