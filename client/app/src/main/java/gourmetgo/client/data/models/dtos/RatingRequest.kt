package gourmetgo.client.data.models.dtos


data class RatingRequest(
    val experienceId: String,
    val score: Int,
    val comment: String
)

