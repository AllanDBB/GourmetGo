package gourmetgo.client.data.models.dtos

data class RatingWithUser(
    val _id: String = "",
    val user: UserInfo = UserInfo(),
    val experience: String = "",
    val score: Int = 0,
    val comment: String = "",
    val createdAt: String = ""
)
