// MyBookingsResponse.kt
package gourmetgo.client.data.models.dtos

import gourmetgo.client.data.models.Booking

data class MyBookingsResponse(
    val bookings: List<Booking>
)