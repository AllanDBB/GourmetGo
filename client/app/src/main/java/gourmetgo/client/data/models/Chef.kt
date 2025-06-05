package gourmetgo.client.data.models


data class Chef(
    val name: String = "",
    val contactPerson: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = "",
    val cuisineType: String = "",
    val password: String = "",
    val photoUrl: String = "",
    val bio : String = "",
    val experience : String = "",
    val socialLinks: List<String> = emptyList()
)