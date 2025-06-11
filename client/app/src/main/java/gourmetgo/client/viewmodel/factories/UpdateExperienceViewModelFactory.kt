package gourmetgo.client.viewmodel.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.remote.Connection
import gourmetgo.client.data.repository.UpdateExperienceRepository
import gourmetgo.client.viewmodel.UpdateExperienceViewModel

class UpdateExperienceViewModelFactory(
    private val context: Context,
    private val idExperience: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpdateExperienceViewModel::class.java)) {
            val connection = Connection()
            val sharedPrefs = SharedPrefsManager(context)
            val repository = UpdateExperienceRepository(
                apiService = connection.apiService,
                sharedPrefs = sharedPrefs,
                idExperience = idExperience
            )
            return UpdateExperienceViewModel(repository, idExperience) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
