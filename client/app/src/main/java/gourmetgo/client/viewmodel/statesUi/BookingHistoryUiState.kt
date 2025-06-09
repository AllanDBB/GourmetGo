package gourmetgo.client.viewmodel.statesUi

import gourmetgo.client.data.models.Booking

data class BookingHistoryUiState(
    val isLoading: Boolean = false,
    val bookings: List<Booking> = emptyList(),
    val isCancelling: Boolean = false,
    val cancelSuccess: Boolean = false,
    val error: String? = null
)