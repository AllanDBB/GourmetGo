package gourmetgo.client.viewmodel.statesUi

import gourmetgo.client.data.models.Booking
import gourmetgo.client.data.models.Experience

data class BookingUiState(
    val isLoading: Boolean = false,
    val isBooking: Boolean = false,
    val experience: Experience? = null,
    val booking: Booking? = null,
    val bookingSuccess: Boolean = false,
    val error: String? = null
)