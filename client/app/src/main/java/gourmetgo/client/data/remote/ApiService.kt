package gourmetgo.client.data.remote

import gourmetgo.client.data.models.Chef
import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.dtos.LoginRequest
import gourmetgo.client.data.models.dtos.LoginResponse
import gourmetgo.client.data.models.dtos.ExperiencesResponse
import gourmetgo.client.data.models.dtos.RegisterUserRequest  
import gourmetgo.client.data.models.dtos.RegisterResponse     
import gourmetgo.client.data.requests.RegisterChefRequest
import gourmetgo.client.data.responses.RegisterChefResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterUserRequest): RegisterResponse

    @GET("experiences")
    suspend fun getExperiences(): ExperiencesResponse

    @POST("auth/register-chef")
    suspend fun registerChef(@Body request: RegisterChefRequest): RegisterChefResponse

    @GET("users/me")
    suspend fun getClientMe(@Header("Authorization") token: String): Client

    @GET("users/me")
    suspend fun getChefMe(@Header("Authorization") token: String): Chef

}