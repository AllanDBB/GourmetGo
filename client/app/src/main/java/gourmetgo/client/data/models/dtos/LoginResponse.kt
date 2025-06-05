package gourmetgo.client.data.models.dtos

import gourmetgo.client.data.models.User

data class LoginResponse(
    val token: String,
    val user: User
)