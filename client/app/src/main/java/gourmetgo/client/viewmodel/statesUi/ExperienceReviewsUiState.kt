package gourmetgo.client.viewmodel.statesUi

import gourmetgo.client.data.models.dtos.RatingWithUser
import gourmetgo.client.data.models.Experience
import gourmetgo.client.data.models.dtos.BookingSummary

data class ExperienceReviewsUiState(
    val isLoading: Boolean = false,
    val reviews: List<RatingWithUser> = emptyList(),
    val error: String? = null,
    val experienceId: String = "",
    val experience: Experience? = null,
    val userBookings: List<BookingSummary> = emptyList(),
    val averageRating: Double = 0.0,
    val totalReviews: Int = 0
)
