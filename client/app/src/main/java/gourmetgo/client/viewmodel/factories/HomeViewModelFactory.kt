package gourmetgo.client.viewmodel.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.remote.Connection
import gourmetgo.client.data.repository.HomeRepository
import gourmetgo.client.viewmodel.HomeViewModel

class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {

            val connection = Connection()
            val sharedPrefs = SharedPrefsManager(context)
            val repository = HomeRepository(
                apiService = connection.apiService,
                sharedPrefs = sharedPrefs
            )

            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
