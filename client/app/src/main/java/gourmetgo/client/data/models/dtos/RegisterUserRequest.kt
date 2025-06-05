package gourmetgo.client.data.models.dtos

data class RegisterUserRequest(
    val name: String,
    val email: String,
    val phone: String,
    val identification: String,
    val password: String,
    val photoUrl: String? = null,
    val preferences: List<String> = emptyList()
)