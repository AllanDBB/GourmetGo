package gourmetgo.client.viewmodel.statesUi

data class DeleteExperienceUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val mail: String? = null
)