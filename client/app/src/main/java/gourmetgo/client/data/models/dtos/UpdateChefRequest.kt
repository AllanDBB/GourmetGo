package gourmetgo.client.data.models.dtos

data class UpdateChefRequest(
    val contactPerson: String?,
    val email: String?,
    val phone: String?,
    val location: String?,
    val cuisineType: String, // tipo cocina
    val photoUrl: String?,
)