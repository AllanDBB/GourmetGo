package gourmetgo.client.data.models


data class Client(
    val id : String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val identification: String = "",
    val password: String = "",
    val role:String="",
    val avatar:String?="",
    val preferences: List<String> = emptyList(),
    val createdAt: String=""
)