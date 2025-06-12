package gourmetgo.client.viewmodel.statesUi

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val isPasswordChanged: Boolean = false,
    val error: String? = null,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val currentPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    val showCurrentPassword: Boolean = false,
    val showNewPassword: Boolean = false,
    val showConfirmPassword: Boolean = false,
    val isFormValid: Boolean = false
)