package gourmetgo.client.data.remote

import gourmetgo.client.data.models.Chef
import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.Experience
import gourmetgo.client.data.models.dtos.BookingRequest
import gourmetgo.client.data.models.dtos.BookingResponse
import gourmetgo.client.data.models.dtos.BookingSummary
import gourmetgo.client.data.models.dtos.CancelBookingResponse
import gourmetgo.client.data.models.dtos.LoginRequest
import gourmetgo.client.data.models.dtos.LoginResponse
import gourmetgo.client.data.models.dtos.ExperiencesResponse
import gourmetgo.client.data.models.dtos.UpdateChefRequest
import gourmetgo.client.data.models.dtos.UpdateChefResponse
import gourmetgo.client.data.models.dtos.UpdateClientRequest
import gourmetgo.client.data.models.dtos.UpdateUserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("experiences")
    suspend fun getExperiences(): ExperiencesResponse

    @GET("users/me")
    suspend fun getClientMe(@Header("Authorization") token: String): Client

    @GET("users/me")
    suspend fun getChefMe(@Header("Authorization") token: String): Chef

    @PUT("users/me")
    suspend fun updateClientProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateClientRequest
    ): UpdateUserResponse

    @PUT("chefs/me")
    suspend fun updateChefProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateChefRequest
    ): UpdateChefResponse

    @POST("bookings")
    suspend fun createBooking(
        @Header("Authorization") token: String,
        @Body request: BookingRequest
    ): BookingResponse

    @GET("bookings/my")
    suspend fun getMyBookings(
        @Header("Authorization") token: String
    ): List<BookingSummary>

    @PUT("bookings/{id}/cancel")
    suspend fun cancelBooking(
        @Header("Authorization") token: String,
        @Path("id") bookingId: String
    ): CancelBookingResponse

    @GET("experiences/{id}")
    suspend fun getExperienceById(@Path("id") id: String): Experience
}