package gourmetgo.client.viewmodel.statesUi
import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.User

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val client: Client? = null,
    val error: String? = null
)