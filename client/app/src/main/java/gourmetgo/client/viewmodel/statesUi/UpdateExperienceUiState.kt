package gourmetgo.client.viewmodel.statesUi

import gourmetgo.client.data.models.Experience

data class UpdateExperienceUiState(
    val isLoading: Boolean = false,
    val experience: Experience? = null,
    val updateSuccess: Boolean = false,
    val error: String? = null
) 