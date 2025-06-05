package gourmetgo.client.data.models


data class Client(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "user",
    val avatar: String = "",
    val dni: String = "",
    val preferences: List<String> = emptyList(),
    val createdAt: String = ""
)