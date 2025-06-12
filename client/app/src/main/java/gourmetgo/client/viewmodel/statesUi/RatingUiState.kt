package gourmetgo.client.viewmodel.statesUi

import gourmetgo.client.data.models.Rating
import gourmetgo.client.data.models.dtos.BookingSummary

data class RatingUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val rating: Rating? = null,
    val ratingSuccess: Boolean = false,
    val error: String? = null
)