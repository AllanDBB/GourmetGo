package gourmetgo.client.data.models.dtos

data class UpdateClientRequest(
    val email: String?,
    val phone: String?,
    val identification: String?,
    val photoUrl: String? = null,
    val preferences: List<String>?
)