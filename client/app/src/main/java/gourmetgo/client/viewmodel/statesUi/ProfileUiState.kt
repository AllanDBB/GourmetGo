package gourmetgo.client.viewmodel.statesUi

import gourmetgo.client.data.models.User

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val updateSuccess: Boolean = false,
    val error: String? = null
)