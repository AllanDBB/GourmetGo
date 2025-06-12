package gourmetgo.client.viewmodel.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.remote.Connection
import gourmetgo.client.data.repository.ViewAssistanceRepository
import gourmetgo.client.viewmodel.ViewAssistanceViewModel

class ViewAssistanceViewModelFactory(
    private val context: Context,
    private val experienceId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewAssistanceViewModel::class.java)) {
            val connection = Connection()
            val sharedPrefs = SharedPrefsManager(context)
            val repository = ViewAssistanceRepository(
                apiService = connection.apiService,
                sprefsManager = sharedPrefs
            )
            return ViewAssistanceViewModel(repository, experienceId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
