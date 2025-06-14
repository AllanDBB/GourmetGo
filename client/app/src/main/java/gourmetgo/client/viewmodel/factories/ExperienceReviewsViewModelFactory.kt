package gourmetgo.client.viewmodel.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.remote.Connection
import gourmetgo.client.data.repository.RatingRepository
import gourmetgo.client.data.repository.ExperienceDetailsRepository
import gourmetgo.client.data.repository.BookingRepository
import gourmetgo.client.viewmodel.ExperienceReviewsViewModel

class ExperienceReviewsViewModelFactory(
    private val context: Context,
    private val experienceId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExperienceReviewsViewModel::class.java)) {
            val connection = Connection()
            val sharedPrefs = SharedPrefsManager(context)
            val ratingRepository = RatingRepository(
                apiService = connection.apiService,
                sharedPrefs = sharedPrefs
            )
            val experienceRepository = ExperienceDetailsRepository(
                apiService = connection.apiService,
                sharedPrefs = sharedPrefs
            )
            val bookingRepository = BookingRepository(
                apiService = connection.apiService,
                sharedPrefs = sharedPrefs
            )

            return ExperienceReviewsViewModel(
                ratingRepository, 
                experienceRepository, 
                bookingRepository, 
                experienceId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
