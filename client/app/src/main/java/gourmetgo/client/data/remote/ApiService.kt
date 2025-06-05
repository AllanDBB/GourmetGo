package gourmetgo.client.data.remote

import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.dtos.LoginRequest
import gourmetgo.client.data.models.dtos.LoginResponse
import gourmetgo.client.data.models.dtos.ExperiencesResponse
import gourmetgo.client.data.models.dtos.UpdateUserRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("experiences")
    suspend fun getExperiences(): ExperiencesResponse

    @GET("users/me")
    suspend fun getMe(@Header("Authorization") token: String): Client

    @PUT("users/me")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateUserRequest
    ): Client
}