package gourmetgo.client.viewmodel.statesUi

import gourmetgo.client.data.models.Chef
import gourmetgo.client.data.models.Client

data class ProfileUiState(
    val isLoading: Boolean = false,
    val client: Client? = null,
    val chef: Chef? = null,
    val updateSuccess: Boolean = false,
    val error: String? = null
)