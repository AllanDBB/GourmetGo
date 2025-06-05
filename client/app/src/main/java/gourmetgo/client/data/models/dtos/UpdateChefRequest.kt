package gourmetgo.client.data.models.dtos

data class UpdateChefRequest(
    val contactPerson:String?,
    val email: String?,
    val location: String?,
    val cuisineType:String?,
    val phone: String?,
    val photoUrl: String? = null,
    val bio: String?,
    val experience: List<String>?,
    val socialLinks:List<String>?
)