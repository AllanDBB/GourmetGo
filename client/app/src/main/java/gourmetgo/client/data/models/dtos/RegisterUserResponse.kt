package gourmetgo.client.data.models.dtos
import gourmetgo.client.data.models.Client

data class RegisterResponse(
    val client: Client,
    val token: String,
)