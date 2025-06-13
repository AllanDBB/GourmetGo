package gourmetgo.client.data.models.dtos

import gourmetgo.client.data.models.Rating

data class RatingResponse(
    val message: String,
    val rating: Rating
)