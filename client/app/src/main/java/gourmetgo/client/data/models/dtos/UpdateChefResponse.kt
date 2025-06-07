package gourmetgo.client.data.models.dtos

import gourmetgo.client.data.models.Chef

data class UpdateChefResponse(
    val message: String,
    val chef: Chef
)