package gourmetgo.client.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gourmetgo.client.utils.EditProfileUtils
import gourmetgo.client.viewmodel.ProfileViewModel

@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val uiState = viewModel.uiState

    var name by remember { mutableStateOf(uiState.user?.name ?: "") }
    var email by remember { mutableStateOf(uiState.user?.email ?: "") }
    var phoneInput by remember { mutableStateOf(EditProfileUtils.cleanPhoneInput(uiState.user?.phone ?: "")) }
    var dniInput by remember { mutableStateOf(EditProfileUtils.cleanDNIInput(uiState.user?.dni ?: "")) }
    var selectedPreferences by remember { mutableStateOf(uiState.user?.preferences ?: emptyList()) }

    var phoneDisplayValue by remember { mutableStateOf(EditProfileUtils.formatPhoneForDisplay(uiState.user?.phone ?: "")) }
    var dniDisplayValue by remember { mutableStateOf(EditProfileUtils.formatDNIForDisplay(uiState.user?.dni ?: "")) }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var dniError by remember { mutableStateOf("") }

    val availablePreferences = viewModel.getPreferences()
    val scrollState = rememberScrollState()

    fun validateFields(): Boolean {
        nameError = if (!EditProfileUtils.isValidName(name)) "Name must contain only letters and spaces" else ""
        emailError = if (!EditProfileUtils.isValidEmail(email)) "Invalid email format" else ""
        phoneError = if (phoneInput.isNotEmpty() && !EditProfileUtils.isValidPhone(phoneInput)) "Phone must have 8 digits" else ""
        dniError = if (dniInput.isNotEmpty() && !EditProfileUtils.isValidDNI(dniInput)) "ID must have 9 digits" else ""

        return nameError.isEmpty() && emailError.isEmpty() && phoneError.isEmpty() && dniError.isEmpty()
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            viewModel.clearUpdateSuccess()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Edit Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        Toast.makeText(context, "Change photo coming soon", Toast.LENGTH_SHORT).show()
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Contact",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Contact",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Full Name",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { newValue ->
                    val cleaned = EditProfileUtils.cleanNameInput(newValue)
                    if (cleaned.length <= 50) {
                        name = cleaned
                        nameError = ""
                    }
                },
                placeholder = { Text("Enter your full name", fontSize = 14.sp) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused && name.isNotEmpty()) {
                            nameError = if (!EditProfileUtils.isValidName(name)) "Name must contain only letters and spaces" else ""
                        }
                    },
                singleLine = true,
                isError = nameError.isNotEmpty(),
                supportingText = if (nameError.isNotEmpty()) {
                    { Text(nameError, color = MaterialTheme.colorScheme.error) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Email",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { newValue ->
                    email = newValue.trim()
                    emailError = ""
                },
                placeholder = { Text("example@gmail.com", fontSize = 14.sp) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused && email.isNotEmpty()) {
                            emailError = if (!EditProfileUtils.isValidEmail(email)) "Invalid email format" else ""
                        }
                    },
                singleLine = true,
                isError = emailError.isNotEmpty(),
                supportingText = if (emailError.isNotEmpty()) {
                    { Text(emailError, color = MaterialTheme.colorScheme.error) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Phone",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phoneDisplayValue,
                onValueChange = { newValue ->
                    val cleanInput = EditProfileUtils.cleanPhoneInput(newValue)
                    if (cleanInput.length <= 8) {
                        phoneInput = cleanInput
                        phoneDisplayValue = if (cleanInput.isNotEmpty()) {
                            EditProfileUtils.formatPhoneForDisplay(cleanInput)
                        } else {
                            ""
                        }
                        phoneError = ""
                    }
                },
                placeholder = { Text("+506 8888 8888", fontSize = 14.sp) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            phoneDisplayValue = EditProfileUtils.formatPhoneForDisplay(phoneInput)
                            if (phoneInput.isNotEmpty()) {
                                phoneError = if (!EditProfileUtils.isValidPhone(phoneInput)) "Phone must have 8 digits" else ""
                            }
                        }
                    },
                singleLine = true,
                isError = phoneError.isNotEmpty(),
                supportingText = if (phoneError.isNotEmpty()) {
                    { Text(phoneError, color = MaterialTheme.colorScheme.error) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ID Number",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = dniDisplayValue,
                onValueChange = { newValue ->
                    val cleanInput = EditProfileUtils.cleanDNIInput(newValue)
                    if (cleanInput.length <= 9) {
                        dniInput = cleanInput
                        dniDisplayValue = if (cleanInput.isNotEmpty()) {
                            EditProfileUtils.formatDNIForDisplay(cleanInput)
                        } else {
                            ""
                        }
                        dniError = ""
                    }
                },
                placeholder = { Text("1-2345-6789", fontSize = 14.sp) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            dniDisplayValue = EditProfileUtils.formatDNIForDisplay(dniInput)
                            if (dniInput.isNotEmpty()) {
                                dniError = if (!EditProfileUtils.isValidDNI(dniInput)) "ID must have 9 digits" else ""
                            }
                        }
                    },
                singleLine = true,
                isError = dniError.isNotEmpty(),
                supportingText = if (dniError.isNotEmpty()) {
                    { Text(dniError, color = MaterialTheme.colorScheme.error) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Food Preferences (optional)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availablePreferences.chunked(3).forEach { row ->
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { preference ->
                            FilterChip(
                                onClick = {
                                    selectedPreferences = if (selectedPreferences.contains(preference)) {
                                        selectedPreferences - preference
                                    } else {
                                        selectedPreferences + preference
                                    }
                                },
                                label = {
                                    Text(
                                        text = preference,
                                        fontSize = 12.sp
                                    )
                                },
                                selected = selectedPreferences.contains(preference),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (validateFields()) {
                        viewModel.updateProfile(
                            name = name.trim(),
                            email = email.trim(),
                            phone = EditProfileUtils.phoneToApiFormat(phoneInput),
                            identification = EditProfileUtils.dniToApiFormat(dniInput),
                            preferences = selectedPreferences
                        )
                    }
                },
                enabled = !uiState.isLoading && name.isNotBlank() && email.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B7280),
                    contentColor = Color.White
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Save Changes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}