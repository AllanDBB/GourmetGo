package gourmetgo.client.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import gourmetgo.client.ui.components.ProfileTextField
import gourmetgo.client.ui.components.FilterChip
import gourmetgo.client.ui.components.ProfileImage
import gourmetgo.client.viewmodel.ProfileViewModel

@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val uiState = viewModel.uiState

    val isChef = viewModel.isChef()
    val isClient = viewModel.isClient()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var rawPhoneInput by remember { mutableStateOf("") }
    var phoneDisplayValue by remember { mutableStateOf("") }

    var dniInput by remember { mutableStateOf("") }
    var dniDisplayValue by remember { mutableStateOf("") }
    var selectedPreferences by remember { mutableStateOf(emptyList<String>()) }

    var contactPerson by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    // Cambiado: ahora usamos una lista de tipos de cocina seleccionados
    var selectedCuisineTypes by remember { mutableStateOf(emptyList<String>()) }

    var emailError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var dniError by remember { mutableStateOf("") }
    var contactPersonError by remember { mutableStateOf("") }
    var locationError by remember { mutableStateOf("") }
    var cuisineTypeError by remember { mutableStateOf("") }

    val availablePreferences = viewModel.getPreferences()
    val scrollState = rememberScrollState()

    fun validateChefFields(): Boolean {
        contactPersonError = if (!EditProfileUtils.isValidContactPerson(contactPerson)) {
            "La persona de contacto debe tener al menos 2 caracteres y solo letras"
        } else ""

        locationError = if (!EditProfileUtils.isValidLocation(location)) {
            "La ubicación debe tener entre 5 y 100 caracteres"
        } else ""

        cuisineTypeError = if (selectedCuisineTypes.isEmpty()) {
            "Debe seleccionar al menos un tipo de cocina"
        } else ""

        return contactPersonError.isEmpty() && locationError.isEmpty() && cuisineTypeError.isEmpty()
    }

    fun validateFields(): Boolean {
        emailError = if (!EditProfileUtils.isValidEmail(email)) "Formato de correo electrónico inválido" else ""
        phoneError = if (rawPhoneInput.isNotEmpty() && !EditProfileUtils.isValidPhone(rawPhoneInput)) "El teléfono debe tener 8 dígitos" else ""

        val basicValidation = emailError.isEmpty() && phoneError.isEmpty()

        return if (isChef) {
            basicValidation && validateChefFields()
        } else {
            if (isClient) {
                dniError = if (dniInput.isNotEmpty() && !EditProfileUtils.isValidDNI(dniInput)) "La cédula debe tener 9 dígitos" else ""
                basicValidation && dniError.isEmpty()
            } else {
                basicValidation
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }

    LaunchedEffect(uiState.client, uiState.chef) {
        when {
            isClient -> {
                uiState.client?.let { client ->
                    name = client.name
                    email = client.email
                    rawPhoneInput = EditProfileUtils.cleanPhoneInput(client.phone)
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
                    rawPhoneInput = EditProfileUtils.cleanPhoneInput(chef.phone)
                    phoneDisplayValue = EditProfileUtils.formatPhoneForDisplay(chef.phone)
                    contactPerson = chef.contactPerson
                    location = chef.location
                    // Actualizado: cargamos las preferencias del chef como tipos de cocina
                    selectedCuisineTypes = chef.preferences
                }
            }
        }
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

            ProfileImage(
                imageUrl = viewModel.getCurrentImageUrl(),
                isChef = isChef,
                onImageCaptured = { uri ->
                    viewModel.uploadProfileImage(uri)
                },
                onImageUploadError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

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

            Text(
                text = "Teléfono",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(8.dp))

            var isPhoneFocused by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = if (isPhoneFocused) rawPhoneInput else phoneDisplayValue,
                onValueChange = { newValue ->
                    val cleanInput = EditProfileUtils.cleanPhoneInput(newValue)
                    if (cleanInput.length <= 8) {
                        rawPhoneInput = cleanInput
                        if (!isPhoneFocused && cleanInput.isNotEmpty()) {
                            phoneDisplayValue = EditProfileUtils.formatPhoneForDisplay(cleanInput)
                        } else if (cleanInput.isEmpty()) {
                            phoneDisplayValue = ""
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
                        isPhoneFocused = focusState.isFocused
                        if (!focusState.isFocused && rawPhoneInput.isNotEmpty()) {
                            phoneDisplayValue = EditProfileUtils.formatPhoneForDisplay(rawPhoneInput)
                            phoneError = if (!EditProfileUtils.isValidPhone(rawPhoneInput)) "El teléfono debe tener 8 dígitos" else ""
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
            }

            if (isChef) {
                ProfileTextField(
                    label = "Persona de Contacto",
                    value = contactPerson,
                    onValueChange = {
                        contactPerson = it
                        contactPersonError = ""
                    },
                    placeholder = "Nombre del encargado",
                    isError = contactPersonError.isNotEmpty(),
                    errorMessage = contactPersonError,
                    keyboardType = KeyboardType.Text,
                    focusManager = focusManager,
                    onFocusLost = {
                        if (!EditProfileUtils.isValidContactPerson(contactPerson)) {
                            contactPersonError = "La persona de contacto debe tener al menos 2 caracteres y solo letras"
                        }
                    }
                )

                ProfileTextField(
                    label = "Ubicación",
                    value = location,
                    onValueChange = {
                        location = it
                        locationError = ""
                    },
                    placeholder = "Dirección del restaurante",
                    isError = locationError.isNotEmpty(),
                    errorMessage = locationError,
                    keyboardType = KeyboardType.Text,
                    focusManager = focusManager,
                    onFocusLost = {
                        if (!EditProfileUtils.isValidLocation(location)) {
                            locationError = "La ubicación debe tener entre 5 y 100 caracteres"
                        }
                    }
                )

                Text(
                    text = "Tipos de Cocina *",
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
                            row.forEach { cuisineType ->
                                FilterChip(
                                    onClick = {
                                        selectedCuisineTypes = if (selectedCuisineTypes.contains(cuisineType)) {
                                            selectedCuisineTypes - cuisineType
                                        } else {
                                            selectedCuisineTypes + cuisineType
                                        }
                                        cuisineTypeError = ""
                                    },
                                    label = {
                                        Text(
                                            text = cuisineType,
                                            fontSize = 12.sp
                                        )
                                    },
                                    selected = selectedCuisineTypes.contains(cuisineType),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                if (cuisineTypeError.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = cuisineTypeError,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (validateFields()) {
                        when {
                            isClient -> {
                                viewModel.updateClientProfile(
                                    name = name.trim(),
                                    email = email.trim(),
                                    phone = EditProfileUtils.phoneToApiFormat(rawPhoneInput),
                                    identification = EditProfileUtils.dniToApiFormat(dniInput),
                                    preferences = selectedPreferences
                                )
                            }
                            isChef -> {
                                viewModel.updateChefProfile(
                                    name = name.trim(),
                                    email = email.trim(),
                                    phone = EditProfileUtils.phoneToApiFormat(rawPhoneInput),
                                    contactPerson = contactPerson.trim(),
                                    location = location.trim(),
                                    cuisineTypes = selectedCuisineTypes
                                )
                            }
                        }
                    }
                },
                enabled = !uiState.isLoading && email.isNotBlank(),
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