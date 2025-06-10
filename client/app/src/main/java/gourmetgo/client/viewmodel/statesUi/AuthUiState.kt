package gourmetgo.client.viewmodel.statesUi
import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.User

import gourmetgo.client.data.models.Chef


data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: Any? = null,
    val userType: String? = null,
    val client: Client? = currentUser as? Client,
    val chef: Chef? = currentUser as? Chef,
    val error: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null
)