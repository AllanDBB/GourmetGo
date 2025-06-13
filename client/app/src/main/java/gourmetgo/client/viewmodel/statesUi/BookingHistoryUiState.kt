package gourmetgo.client.viewmodel.statesUi

import gourmetgo.client.data.models.dtos.BookingSummary

data class BookingHistoryUiState(
    val isLoading: Boolean = false,
    val bookings: List<BookingSummary> = emptyList(),
    val isCancelling: Boolean = false,
    val cancelSuccess: Boolean = false,
    val error: String? = null
)