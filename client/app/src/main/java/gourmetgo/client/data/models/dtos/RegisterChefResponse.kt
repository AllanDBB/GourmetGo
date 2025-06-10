package gourmetgo.client.data.responses

import gourmetgo.client.data.models.Chef

data class RegisterChefResponse(
    val chef: Chef,
    val token: String
)