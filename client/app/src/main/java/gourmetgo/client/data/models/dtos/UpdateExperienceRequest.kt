package gourmetgo.client.data.models.dtos

data class UpdateExperienceRequest(
    val location: String,
    val date: String,
    val status: String,
    val capacity: Int,
    val price: Double
)