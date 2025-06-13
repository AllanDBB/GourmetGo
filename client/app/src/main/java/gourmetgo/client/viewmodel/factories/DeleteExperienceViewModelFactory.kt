package gourmetgo.client.viewmodel.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.remote.Connection
import gourmetgo.client.data.repository.DeleteExperienceRepository
import gourmetgo.client.viewmodel.DeleteExperienceViewModel

class DeleteExperienceViewModelFactory(
    private val context: Context,
    private val idExperience: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeleteExperienceViewModel::class.java)) {
            val connection = Connection()
            val sharedPrefs = SharedPrefsManager(context)
            val repository = DeleteExperienceRepository(
                apiService = connection.apiService,
                sharedPrefs = sharedPrefs
            )
            return DeleteExperienceViewModel(repository, idExperience) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
