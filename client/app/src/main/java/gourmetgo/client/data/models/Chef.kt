package gourmetgo.client.data.models


data class Chef(
    val _id: String = "",
    val name: String = "",
    val contactPerson: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = "",
    val preferences: List<String> = emptyList(), // tipos de cocina
    val password: String = "",
    val avatar: String = "",
    val bio: String = "",
    val experience: String = "",
    val socialLinks: List<String> = emptyList()
)