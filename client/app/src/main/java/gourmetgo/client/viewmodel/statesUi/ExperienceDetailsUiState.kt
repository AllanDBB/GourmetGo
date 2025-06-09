package gourmetgo.client.viewmodel.statesUi

import gourmetgo.client.data.models.Experience

data class ExperienceDetailsUiState(
    val isLoading: Boolean = false,
    val experience: Experience? = null,
    val error: String? = null
) 
