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

    // ✅ Detectar si es cliente o chef
    val isChef = viewModel.isChef()
    val isClient = viewModel.isClient()

    // Estados comunes
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var phoneDisplayValue by remember { mutableStateOf("") }

    // Estados específicos para cliente
    var dniInput by remember { mutableStateOf("") }
    var dniDisplayValue by remember { mutableStateOf("") }
    var selectedPreferences by remember { mutableStateOf(emptyList<String>()) }

    // Estados específicos para chef
    var contactPerson by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var cuisineType by remember { mutableStateOf("") }

    // Estados de error
    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var dniError by remember { mutableStateOf("") }

    val availablePreferences = viewModel.getPreferences()
    val scrollState = rememberScrollState()

    // ✅ Cargar datos iniciales basados en el tipo de usuario
    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }

    LaunchedEffect(uiState.client, uiState.chef) {
        when {
            isClient -> {
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
            isChef -> {
                uiState.chef?.let { chef ->
                    name = chef.name
                    email = chef.email
                    phoneInput = EditProfileUtils.cleanPhoneInput(chef.phone)
                    phoneDisplayValue = EditProfileUtils.formatPhoneForDisplay(chef.phone)
                    contactPerson = chef.contactPerson
                    location = chef.location
                    cuisineType = chef.preferences.firstOrNull() ?: ""
                }
            }
        }
    }

    fun validateFields(): Boolean {
        nameError = if (!EditProfileUtils.isValidName(name)) "El nombre solo puede contener letras y espacios" else ""
        emailError = if (!EditProfileUtils.isValidEmail(email)) "Formato de correo electrónico inválido" else ""
        phoneError = if (phoneInput.isNotEmpty() && !EditProfileUtils.isValidPhone(phoneInput)) "El teléfono debe tener 8 dígitos" else ""

        if (isClient) {
            dniError = if (dniInput.isNotEmpty() && !EditProfileUtils.isValidDNI(dniInput)) "La cédula debe tener 9 dígitos" else ""
        }

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
                    text = if (isChef) "Editar Perfil Chef" else "Editar Perfil",
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

            // ✅ Avatar
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
                        if (isChef) Icons.Default.Restaurant else Icons.Default.Person,
                        contentDescription = if (isChef) "Chef" else "Usuario",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isChef) "Chef" else "Usuario",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ✅ Campo Nombre (común para ambos)
            ProfileTextField(
                label = if (isChef) "Nombre del Restaurante" else "Nombre Completo",
                value = name,
                onValueChange = { newValue ->
                    val cleaned = EditProfileUtils.cleanNameInput(newValue)
                    if (cleaned.length <= 50) {
                        name = cleaned
                        nameError = ""
                    }
                },
                placeholder = if (isChef) "Nombre del restaurante" else "Tu nombre completo",
                isError = nameError.isNotEmpty(),
                errorMessage = nameError,
                keyboardType = KeyboardType.Text,
                focusManager = focusManager
            )

            // ✅ Campo Email (común para ambos)
            ProfileTextField(
                label = "Email",
                value = email,
                onValueChange = { newValue ->
                    email = newValue.trim()
                    emailError = ""
                },
                placeholder = "ejemplo@gmail.com",
                isError = emailError.isNotEmpty(),
                errorMessage = emailError,
                keyboardType = KeyboardType.Email,
                focusManager = focusManager
            )

            // ✅ Campo Teléfono (común para ambos)
            ProfileTextField(
                label = "Teléfono",
                value = phoneDisplayValue,
                onValueChange = { newValue ->
                    val cleanInput = EditProfileUtils.cleanPhoneInput(newValue)
                    if (cleanInput.length <= 8) {
                        phoneInput = cleanInput
                        phoneDisplayValue = cleanInput
                        phoneError = ""
                    }
                },
                placeholder = "+506 8888 8888",
                isError = phoneError.isNotEmpty(),
                errorMessage = phoneError,
                keyboardType = KeyboardType.Phone,
                focusManager = focusManager,
                onFocusLost = {
                    if (phoneInput.isNotEmpty()) {
                        phoneDisplayValue = EditProfileUtils.formatPhoneForDisplay(phoneInput)
                        phoneError = if (!EditProfileUtils.isValidPhone(phoneInput)) "El teléfono debe tener 8 dígitos" else ""
                    }
                }
            )

            // ✅ Campos específicos para CLIENTE
            if (isClient) {
                ProfileTextField(
                    label = "Cédula",
                    value = dniDisplayValue,
                    onValueChange = { newValue ->
                        val cleanInput = EditProfileUtils.cleanDNIInput(newValue)
                        if (cleanInput.length <= 9) {
                            dniInput = cleanInput
                            dniDisplayValue = cleanInput
                            dniError = if (cleanInput.isNotEmpty() && cleanInput.length != 9) "La cédula debe tener 9 dígitos" else ""
                        }
                    },
                    placeholder = "1-2345-6789",
                    isError = dniError.isNotEmpty(),
                    errorMessage = dniError,
                    keyboardType = KeyboardType.Number,
                    focusManager = focusManager,
                    onFocusLost = {
                        if (dniInput.isNotEmpty()) {
                            dniDisplayValue = EditProfileUtils.formatDNIForDisplay(dniInput)
                            dniError = if (!EditProfileUtils.isValidDNI(dniInput)) "La cédula debe tener 9 dígitos" else ""
                        }
                    }
                )

                // Preferencias gastronómicas
                Text(
                    text = "Preferencias Gastronómicas (opcional)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Grid de preferencias
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
            }

            // ✅ Campos específicos para CHEF
            if (isChef) {
                ProfileTextField(
                    label = "Persona de Contacto",
                    value = contactPerson,
                    onValueChange = { contactPerson = it },
                    placeholder = "Nombre del encargado",
                    keyboardType = KeyboardType.Text,
                    focusManager = focusManager
                )

                ProfileTextField(
                    label = "Ubicación",
                    value = location,
                    onValueChange = { location = it },
                    placeholder = "Dirección del restaurante",
                    keyboardType = KeyboardType.Text,
                    focusManager = focusManager
                )

                ProfileTextField(
                    label = "Tipo de Cocina",
                    value = cuisineType,
                    onValueChange = { cuisineType = it },
                    placeholder = "Ej: Italiana, Asiática, Fusión",
                    keyboardType = KeyboardType.Text,
                    focusManager = focusManager
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ✅ Botón Guardar
            Button(
                onClick = {
                    if (validateFields()) {
                        when {
                            isClient -> {
                                viewModel.updateClientProfile(
                                    name = name.trim(),
                                    email = email.trim(),
                                    phone = EditProfileUtils.phoneToApiFormat(phoneInput),
                                    identification = EditProfileUtils.dniToApiFormat(dniInput),
                                    preferences = selectedPreferences
                                )
                            }
                            isChef -> {
                                viewModel.updateChefProfile(
                                    name = name.trim(),
                                    email = email.trim(),
                                    phone = EditProfileUtils.phoneToApiFormat(phoneInput),
                                    contactPerson = contactPerson.trim(),
                                    location = location.trim(),
                                    cuisineType = cuisineType.trim()
                                )
                            }
                        }
                    }
                },
                enabled = !uiState.isLoading && name.isNotBlank() && email.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
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

// ✅ Componente auxiliar para campos de texto
@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    focusManager: androidx.compose.ui.focus.FocusManager,
    onFocusLost: () -> Unit = {}
) {
    Text(
        text = label,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, fontSize = 14.sp) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (!focusState.isFocused && value.isNotEmpty()) {
                    onFocusLost()
                }
            },
        singleLine = true,
        isError = isError,
        supportingText = if (errorMessage.isNotEmpty()) {
            { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
        } else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )

    Spacer(modifier = Modifier.height(16.dp))
}