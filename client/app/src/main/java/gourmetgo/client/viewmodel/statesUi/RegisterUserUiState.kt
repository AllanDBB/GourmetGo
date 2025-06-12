package gourmetgo.client.viewmodel.statesUi
import android.net.Uri


data class RegisterUserUiState(
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val error: String? = null,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val identification: String = "",
    val password: String = "",
    val selectedPreferences: Set<String> = emptySet(),
    val selectedImageUri: Uri? = null,
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val identificationError: String? = null,
    val passwordError: String? = null,
    val preferencesError: String? = null,
    val isFormValid: Boolean = false,
    val showPassword: Boolean = false,
    val photoError: String? = null,
)