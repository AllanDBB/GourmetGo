package gourmetgo.client.viewmodel.statesUi
import gourmetgo.client.data.models.Experience

data class MyExperiencesChefUiState(
    val isLoading: Boolean = false,
    val experiences: List<Experience> = emptyList(),
    val error: String? = null,
    val refreshing: Boolean = false,
) {
    fun isEmpty(): Boolean {
        return experiences.isEmpty()
    }
}