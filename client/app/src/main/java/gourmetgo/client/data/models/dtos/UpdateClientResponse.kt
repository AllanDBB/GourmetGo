package gourmetgo.client.data.models.dtos

import gourmetgo.client.data.models.Client

data class UpdateUserResponse(
    val message: String,
    val user: Client
)