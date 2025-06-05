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

    var name by remember { mutableStateOf(uiState.client?.name ?: "") }
    var email by remember { mutableStateOf(uiState.client?.email ?: "") }
    var phoneInput by remember { mutableStateOf(EditProfileUtils.cleanPhoneInput(uiState.client?.phone ?: "")) }
    var dniInput by remember { mutableStateOf(EditProfileUtils.cleanDNIInput(uiState.client?.identification ?: "")) }
    var selectedPreferences by remember { mutableStateOf(uiState.client?.preferences ?: emptyList()) }

    var phoneDisplayValue by remember { mutableStateOf(EditProfileUtils.formatPhoneForDisplay(uiState.client?.phone ?: "")) }
    var dniDisplayValue by remember { mutableStateOf(EditProfileUtils.formatDNIForDisplay(uiState.client?.identification ?: "")) }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var dniError by remember { mutableStateOf("") }

    val availablePreferences = viewModel.getPreferences()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }

    LaunchedEffect(uiState.client) {
        uiState.client?.let { client ->
            name = client.name
            email = client.email
            phoneInput = EditProfileUtils.cleanPhoneInput(client.phone)
            dniInput = EditProfileUtils.cleanDNIInput(client.identification)
            selectedPreferences = client.preferences
            phoneDisplayValue = EditProfileUtils.formatPhoneForDisplay(client.phone)
            dniDisplayValue = EditProfileUtils.formatDNIForDisplay(client.identification)
        }
    }

    fun validateFields(): Boolean {
        nameError = if (!EditProfileUtils.isValidName(name)) "El nombre solo puede contener letras y espacios" else ""
        emailError = if (!EditProfileUtils.isValidEmail(email)) "Formato de correo electrónico inválido" else ""
        phoneError = if (phoneInput.isNotEmpty() && !EditProfileUtils.isValidPhone(phoneInput)) "El teléfono debe tener 8 dígitos" else ""
        dniError = if (dniInput.isNotEmpty() && !EditProfileUtils.isValidDNI(dniInput)) "La cédula debe tener 9 dígitos" else ""

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
            Toast.makeText(context, "Perfil actualizado exitosamente", Toast.LENGTH_SHORT).show()
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
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Editar Perfil",
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
                        Toast.makeText(context, "Cambiar foto próximamente", Toast.LENGTH_SHORT).show()
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
                        text = "Contacto",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Nombre Completo",
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
                placeholder = { Text("Ingresa tu nombre completo", fontSize = 14.sp) },
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
                            nameError = if (!EditProfileUtils.isValidName(name)) "El nombre solo puede contener letras y espacios" else ""
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
                placeholder = { Text("ejemplo@gmail.com", fontSize = 14.sp) },
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
                            emailError = if (!EditProfileUtils.isValidEmail(email)) "Formato de correo electrónico inválido" else ""
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
                text = "Teléfono",
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
                        phoneDisplayValue = cleanInput
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
                        if (!focusState.isFocused && phoneInput.isNotEmpty()) {
                            phoneDisplayValue = EditProfileUtils.formatPhoneForDisplay(phoneInput)
                            phoneError = if (!EditProfileUtils.isValidPhone(phoneInput)) "El teléfono debe tener 8 dígitos" else ""
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
                text = "Cédula",
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
                        dniDisplayValue = cleanInput
                        dniError = if (cleanInput.isNotEmpty() && cleanInput.length != 9) "La cédula debe tener 9 dígitos" else ""
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
                        if (!focusState.isFocused && dniInput.isNotEmpty()) {
                            dniDisplayValue = EditProfileUtils.formatDNIForDisplay(dniInput)
                            dniError = if (!EditProfileUtils.isValidDNI(dniInput)) "La cédula debe tener 9 dígitos" else ""
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
                text = "Preferencias Gastronómicas (opcional)",
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
                        text = "Guardar Cambios",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}