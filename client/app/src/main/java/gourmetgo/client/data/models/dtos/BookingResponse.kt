package gourmetgo.client.data.models.dtos

import gourmetgo.client.data.models.Booking

data class BookingResponse(
    val message: String,
    val booking: Booking
)