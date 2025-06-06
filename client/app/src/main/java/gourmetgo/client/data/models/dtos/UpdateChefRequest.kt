package gourmetgo.client.data.models.dtos
//NOTE: social links has any use?, API required it but I think it is not required
data class UpdateChefRequest(
    val contactPerson: String?,
    val email: String?,
    val phone: String?,
    val location: String?,
    val cuisineType: String, // tipo cocina
    val photoUrl: String?,
    val socialLinks : List<String> = listOf("https://instagram.com/")
)