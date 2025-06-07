package gourmetgo.client.viewmodel.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.remote.Connection
import gourmetgo.client.data.remote.CloudinaryService  
import gourmetgo.client.data.repository.RegisterChefRepository
import gourmetgo.client.viewmodel.RegisterChefViewModel

class RegisterChefViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterChefViewModel::class.java)) {

            val connection = Connection()
            val sharedPrefs = SharedPrefsManager(context)
            val cloudinaryService = CloudinaryService(context)

            val repository = RegisterChefRepository(
                apiService = connection.apiService,
                sharedPrefs = sharedPrefs,
                cloudinaryService = cloudinaryService
            )

            return RegisterChefViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}