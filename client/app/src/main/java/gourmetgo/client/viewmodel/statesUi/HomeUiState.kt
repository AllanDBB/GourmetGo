package gourmetgo.client.viewmodel.statesUi

import gourmetgo.client.data.models.Experience

data class HomeUiState(
    val isLoading: Boolean = false,
    val popularExperiences: List<Experience> = emptyList(),
    val upcomingExperiences: List<Experience> = emptyList(),
    val allExperiences: List<Experience> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val error: String? = null,
    val refreshing: Boolean = false,
    val showingAllExperiences: Boolean = false
) {
    fun hasData(): Boolean {
        return popularExperiences.isNotEmpty() || upcomingExperiences.isNotEmpty() || allExperiences.isNotEmpty()
    }
    
    fun isEmpty(): Boolean {
        return popularExperiences.isEmpty() && upcomingExperiences.isEmpty() && allExperiences.isEmpty()
    }
}
