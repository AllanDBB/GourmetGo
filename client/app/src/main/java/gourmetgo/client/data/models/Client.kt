package gourmetgo.client.data.models


data class Client(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val identification: String = "",
    val password: String = "",
    val photoUrl: String = "",
    val preferences: List<String> = emptyList()
)
