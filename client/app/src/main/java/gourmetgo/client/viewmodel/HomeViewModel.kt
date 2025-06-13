package gourmetgo.client.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gourmetgo.client.AppConfig
import gourmetgo.client.data.repository.HomeRepository
import gourmetgo.client.viewmodel.statesUi.HomeUiState
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import java.util.concurrent.TimeUnit

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    private var searchJob: Job? = null
    private var autoRefreshJob: Job? = null
    
    companion object {
        private const val AUTO_REFRESH_INTERVAL = 30_000L // 30 segundos
    }

    init {
        loadInitialData()
        startAutoRefresh()
    }
    
    override fun onCleared() {
        super.onCleared()
        autoRefreshJob?.cancel()
        searchJob?.cancel()
    }
    
    private fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (true) {
                delay(AUTO_REFRESH_INTERVAL)
                refreshData()
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, error = null)

                // Cargar datos en paralelo
                val popularResult = repository.getPopularExperiences()
                val upcomingResult = repository.getUpcomingExperiences()
                val categoriesResult = repository.getCategories()

                // Procesar resultados
                val popularExperiences = popularResult.getOrElse { 
                    if (AppConfig.ENABLE_LOGGING) {
                        Log.e("HomeViewModel", "Error loading popular experiences: ${it.message}")
                    }
                    emptyList()
                }

                val upcomingExperiences = upcomingResult.getOrElse { 
                    if (AppConfig.ENABLE_LOGGING) {
                        Log.e("HomeViewModel", "Error loading upcoming experiences: ${it.message}")
                    }
                    emptyList()
                }

                val categories = categoriesResult.getOrElse { 
                    if (AppConfig.ENABLE_LOGGING) {
                        Log.e("HomeViewModel", "Error loading categories: ${it.message}")
                    }
                    listOf("Eventos", "Clases", "Restaurantes") // Fallback por defecto
                }

                uiState = uiState.copy(
                    isLoading = false,
                    popularExperiences = popularExperiences,
                    upcomingExperiences = upcomingExperiences,
                    categories = categories,
                    error = null
                )

                if (AppConfig.ENABLE_LOGGING) {
                    Log.d("HomeViewModel", "Loaded ${popularExperiences.size} popular and ${upcomingExperiences.size} upcoming experiences")
                }

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error cargando datos: ${e.message}"
                )
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("HomeViewModel", "Error in loadInitialData", e)
                }
            }
        }
    }

    fun searchExperiences(query: String) {
        if (query.isBlank()) {
            clearSearch()
            return
        }

        // Cancelar búsqueda anterior
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            try {
                uiState = uiState.copy(
                    isSearching = true,
                    searchQuery = query,
                    error = null
                )

                // Pequeño delay para evitar búsquedas muy rápidas
                delay(300)

                val searchResult = repository.searchExperiences(query)

                searchResult
                    .onSuccess { experiences ->
                        uiState = uiState.copy(
                            isSearching = false,
                            popularExperiences = experiences,
                            upcomingExperiences = emptyList(), // En búsqueda solo mostramos resultados populares
                            error = null
                        )
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.d("HomeViewModel", "Search found ${experiences.size} experiences for query: $query")
                        }
                    }
                    .onFailure { error ->
                        uiState = uiState.copy(
                            isSearching = false,
                            error = error.message ?: "Error en búsqueda"
                        )
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.e("HomeViewModel", "Search error for query '$query'", error)
                        }
                    }

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSearching = false,
                    error = "Error inesperado en búsqueda: ${e.message}"
                )
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("HomeViewModel", "Unexpected error in search", e)
                }
            }
        }
    }    
    
    fun filterByCategory(category: String) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(
                    isLoading = true,
                    selectedCategory = category,
                    searchQuery = "",
                    error = null
                )

                val categoryResult = repository.getExperiencesByCategory(category)

                categoryResult
                    .onSuccess { experiences ->
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.d("HomeViewModel", "🎉 SUCCESS: Found ${experiences.size} experiences for category: $category")
                            experiences.forEach { exp ->
                                Log.d("HomeViewModel", "  📋 ${exp.title} (Category: '${exp.category}', Status: '${exp.status}')")
                            }
                        }
                        
                        val activeExperiences = experiences.filter { it.status == "Activa" }
                        val upcomingExperiences = experiences.filter { it.status == "Próximamente" }
                        
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.d("HomeViewModel", "✅ FILTERED RESULTS:")
                            Log.d("HomeViewModel", "  🟢 Active experiences: ${activeExperiences.size}")
                            Log.d("HomeViewModel", "  🟡 Upcoming experiences: ${upcomingExperiences.size}")
                        }
                        
                        uiState = uiState.copy(
                            isLoading = false,
                            popularExperiences = activeExperiences,
                            upcomingExperiences = upcomingExperiences,
                            error = null
                        )
                    }
                    .onFailure { error ->
                        uiState = uiState.copy(
                            isLoading = false,
                            error = error.message ?: "Error filtrando por categoría"
                        )
                        if (AppConfig.ENABLE_LOGGING) {
                            Log.e("HomeViewModel", "❌ Category filter error for '$category'", error)
                        }
                    }} catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error inesperado filtrando: ${e.message}"
                )
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("HomeViewModel", "Unexpected error in category filter", e)
                }
            }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        uiState = uiState.copy(
            searchQuery = "",
            isSearching = false
        )
        loadInitialData() // Recargar datos originales
    }

    fun clearCategoryFilter() {
        uiState = uiState.copy(selectedCategory = null)
        loadInitialData() // Recargar datos originales
    }

    fun refreshData() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(refreshing = true, error = null)

                // Si hay filtros activos, aplicarlos de nuevo
                when {
                    uiState.searchQuery.isNotBlank() -> {
                        searchExperiences(uiState.searchQuery)
                    }
                    uiState.selectedCategory != null -> {
                        filterByCategory(uiState.selectedCategory!!)
                    }
                    else -> {
                        loadInitialData()
                    }
                }

                uiState = uiState.copy(refreshing = false)

            } catch (e: Exception) {
                uiState = uiState.copy(
                    refreshing = false,
                    error = "Error actualizando datos: ${e.message}"
                )
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("HomeViewModel", "Error in refreshData", e)
                }
            }
        }
    }

    fun getCurrentSectionTitle(): String {
        return when {
            uiState.searchQuery.isNotBlank() -> "Resultados de búsqueda"
            uiState.selectedCategory != null -> "Categoría: ${uiState.selectedCategory}"
            else -> "Inicio"
        }
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }
    
    // Mostrar todas las experiencias
    fun loadAllExperiences() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, error = null)
                
                val result = repository.getAllExperiences()
                val allExperiences = result.getOrElse { 
                    if (AppConfig.ENABLE_LOGGING) {
                        Log.e("HomeViewModel", "Error loading all experiences: ${it.message}")
                    }
                    emptyList()
                }
                
                uiState = uiState.copy(
                    isLoading = false,
                    allExperiences = allExperiences,
                    showingAllExperiences = true,
                    error = null
                )
                
                if (AppConfig.ENABLE_LOGGING) {
                    Log.d("HomeViewModel", "Loaded ${allExperiences.size} total experiences")
                }
                
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error cargando todas las experiencias: ${e.message}"
                )
                if (AppConfig.ENABLE_LOGGING) {
                    Log.e("HomeViewModel", "Error in loadAllExperiences", e)
                }
            }
        }
    }
    
    // Volver a vista principal (popular + upcoming)
    fun showHomeView() {
        uiState = uiState.copy(
            showingAllExperiences = false,
            allExperiences = emptyList()
        )
    }

    // Funciones de utilidad para la UI
    fun hasActiveFilters(): Boolean {
        return uiState.searchQuery.isNotBlank() || uiState.selectedCategory != null
    }

    fun getPopularExperiences() = uiState.popularExperiences
    fun getUpcomingExperiences() = uiState.upcomingExperiences
    fun getCategories() = uiState.categories
}
