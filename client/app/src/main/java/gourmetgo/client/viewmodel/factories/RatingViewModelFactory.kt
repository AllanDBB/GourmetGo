package gourmetgo.client.viewmodel.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.remote.Connection
import gourmetgo.client.data.repository.RatingRepository
import gourmetgo.client.viewmodel.RatingViewModel

class RatingViewModelFactory(
    private val context: Context,
    private val experienceId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RatingViewModel::class.java)) {
            val connection = Connection()
            val sharedPrefs = SharedPrefsManager(context)
            val repository = RatingRepository(
                apiService = connection.apiService,
                sharedPrefs = sharedPrefs
            )

            return RatingViewModel(repository, experienceId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}