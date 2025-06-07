package gourmetgo.client.viewmodel.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.remote.Connection
import gourmetgo.client.data.repository.AuthRepository
import gourmetgo.client.utils.ImageUploadUtils
import gourmetgo.client.viewmodel.ProfileViewModel

class ProfileViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {

            val connection = Connection()
            val sharedPrefs = SharedPrefsManager(context)
            val repository = AuthRepository(
                apiService = connection.apiService,
                sharedPrefs = sharedPrefs
            )
            val imageUploadUseCase = ImageUploadUtils(context)

            return ProfileViewModel(repository, imageUploadUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}