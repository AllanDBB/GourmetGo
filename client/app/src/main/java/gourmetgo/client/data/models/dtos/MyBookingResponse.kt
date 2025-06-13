package gourmetgo.client.data.models.dtos

import gourmetgo.client.data.models.Experience

data class MyBookingsResponse(
    val bookings: List<BookingSummary>
)

data class BookingSummary(
    val _id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val people: Int = 0,
    val termsAccepted : Boolean ,
    val paymentMethod: String = "",
    val status: String = "pending",
    val bookingCode: String = "",
    val qrCodes : List<String>,
    val createdAt: String = "",

    val experience: Experience = Experience()
)
