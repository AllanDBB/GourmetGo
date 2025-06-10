package gourmetgo.client.viewmodel.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.remote.Connection
import gourmetgo.client.data.repository.ExperienceDetailsRepository
import gourmetgo.client.viewmodel.ExperienceDetailsViewModel

class ExperienceDetailsViewModelFactory(
    private val context: Context,
    private val experienceId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExperienceDetailsViewModel::class.java)) {
            val connection = Connection()
            val sharedPrefs = SharedPrefsManager(context)
            val repository = ExperienceDetailsRepository(
                apiService = connection.apiService,
                sharedPrefs = sharedPrefs
            )
            return ExperienceDetailsViewModel(repository, experienceId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}