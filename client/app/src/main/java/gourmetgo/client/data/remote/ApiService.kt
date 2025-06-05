package gourmetgo.client.data.remote

import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.dtos.LoginRequest
import gourmetgo.client.data.models.dtos.LoginResponse
import gourmetgo.client.data.models.dtos.ExperiencesResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("experiences")
    suspend fun getExperiences(): ExperiencesResponse

    @GET("users/me/")
    suspend fun getMe(@Header("Authorization") token: String,
    ): Client

    @PATCH("user/")

}