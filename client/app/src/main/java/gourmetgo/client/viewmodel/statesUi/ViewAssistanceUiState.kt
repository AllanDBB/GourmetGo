package gourmetgo.client.viewmodel.statesUi

import gourmetgo.client.data.models.Booking
import gourmetgo.client.data.models.Experience
import gourmetgo.client.data.models.dtos.AssistanceResponse

data class ViewAssistanceUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val refreshing: Boolean = false,
    val bookings : List<AssistanceResponse>? = null,
    val experience: Experience? = null
) 