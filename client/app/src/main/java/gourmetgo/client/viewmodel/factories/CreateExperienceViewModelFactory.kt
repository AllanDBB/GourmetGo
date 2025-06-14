package gourmetgo.client.viewmodel.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gourmetgo.client.data.repository.CreateExperienceRepository
import gourmetgo.client.viewmodel.CreateExperienceViewModel
import gourmetgo.client.data.remote.Connection
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.remote.CloudinaryService

class CreateExperienceViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateExperienceViewModel::class.java)) {
            val connection = Connection()
            val apiService = connection.apiService
            val sharedPrefs = SharedPrefsManager(context)
            val cloudinaryService = CloudinaryService(context)
            val repository = CreateExperienceRepository(apiService, sharedPrefs)
            @Suppress("UNCHECKED_CAST")
            return CreateExperienceViewModel(repository, cloudinaryService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
