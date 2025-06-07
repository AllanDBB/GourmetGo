package gourmetgo.client.data.models.dtos

data class UpdateClientRequest(
    val email: String?,
    val phone: String?,
    val identification: String?,
    val avatar: String?,
    val preferences: List<String>?
)