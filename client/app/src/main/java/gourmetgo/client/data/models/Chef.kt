package gourmetgo.client.data.models


data class Chef(
    val _id: String = "",
    val name: String = "",
    val contactPerson: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = "",
    val preferences: List<String> = emptyList(), // tipo cocina
    val password: String = "",
    val avatar: String = "",
)