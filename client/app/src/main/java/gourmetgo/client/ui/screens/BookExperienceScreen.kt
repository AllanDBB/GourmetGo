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
import gourmetgo.client.ui.components.FilterChip
import gourmetgo.client.utils.BookingUtils
import gourmetgo.client.viewmodel.BookingViewModel

@Composable
fun BookExperienceScreen(
    experienceId: String,
    viewModel: BookingViewModel,
    onNavigateBack: () -> Unit,
    onBookingSuccess: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("Brian Ramirez") }
    var email by remember { mutableStateOf("brianramirez01arias@gmail.com") }
    var rawPhoneInput by remember { mutableStateOf("87044846") }
    var phoneDisplayValue by remember { mutableStateOf("") }
    var people by remember { mutableIntStateOf(1) }
    var selectedPaymentMethod by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var paymentMethodError by remember { mutableStateOf("") }

    val paymentMethods = listOf("Pago en el lugar", "Transferencia")

    fun validateFields(): Boolean {
        nameError = if (!BookingUtils.isValidName(name)) "El nombre debe tener al menos 2 caracteres" else ""
        emailError = if (!BookingUtils.isValidEmail(email)) "Formato de correo electrónico inválido" else ""
        phoneError = if (rawPhoneInput.isNotEmpty() && !BookingUtils.isValidPhone(rawPhoneInput)) "El teléfono debe tener 8 dígitos" else ""
        paymentMethodError = if (selectedPaymentMethod.isEmpty()) "Debe seleccionar un método de pago" else ""

        return nameError.isEmpty() && emailError.isEmpty() && phoneError.isEmpty() &&
                paymentMethodError.isEmpty() && termsAccepted
    }

    LaunchedEffect(experienceId) {
        viewModel.loadExperience(experienceId)
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.bookingSuccess) {
        if (uiState.bookingSuccess) {
            Toast.makeText(context, "Reservación realizada exitosamente", Toast.LENGTH_SHORT).show()
            onBookingSuccess()
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
                    text = "Reservar Experiencia",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.experience == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                uiState.experience?.let { experience ->
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = experience.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Precio: ₡${String.format("%,.0f", experience.price)} por persona",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Disponibles: ${experience.remainingCapacity}/${experience.capacity}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Nombre completo *",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = ""
                        },
                        placeholder = { Text("Ingrese su nombre completo", fontSize = 14.sp) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = nameError.isNotEmpty(),
                        supportingText = if (nameError.isNotEmpty()) {
                            { Text(nameError, color = MaterialTheme.colorScheme.error) }
                        } else null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Correo electrónico *",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it.trim()
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
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = emailError.isNotEmpty(),
                        supportingText = if (emailError.isNotEmpty()) {
                            { Text(emailError, color = MaterialTheme.colorScheme.error) }
                        } else null
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

                    var isPhoneFocused by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = if (isPhoneFocused) rawPhoneInput else phoneDisplayValue,
                        onValueChange = { newValue ->
                            val cleanInput = BookingUtils.cleanPhoneInput(newValue)
                            if (cleanInput.length <= 8) {
                                rawPhoneInput = cleanInput
                                if (!isPhoneFocused && cleanInput.isNotEmpty()) {
                                    phoneDisplayValue = BookingUtils.formatPhoneForDisplay(cleanInput)
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
                                    phoneDisplayValue = BookingUtils.formatPhoneForDisplay(rawPhoneInput)
                                    phoneError = if (!BookingUtils.isValidPhone(rawPhoneInput)) "El teléfono debe tener 8 dígitos" else ""
                                }
                            },
                        singleLine = true,
                        isError = phoneError.isNotEmpty(),
                        supportingText = if (phoneError.isNotEmpty()) {
                            { Text(phoneError, color = MaterialTheme.colorScheme.error) }
                        } else null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Cantidad de personas *",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { if (people > 1) people-- },
                            enabled = people > 1,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Text("-", fontSize = 18.sp)
                        }

                        Text(
                            text = people.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )

                        OutlinedButton(
                            onClick = { if (people < experience.remainingCapacity) people++ },
                            enabled = people < experience.remainingCapacity,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Text("+", fontSize = 18.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Método de pago *",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        paymentMethods.forEach { method ->
                            FilterChip(
                                onClick = {
                                    selectedPaymentMethod = method
                                    paymentMethodError = ""
                                },
                                label = {
                                    Text(
                                        text = method,
                                        fontSize = 14.sp
                                    )
                                },
                                selected = selectedPaymentMethod == method,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    if (paymentMethodError.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = paymentMethodError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it }
                        )
                        Text(
                            text = "Acepto los términos y condiciones *",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Resumen de la reserva",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Personas: $people",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Precio por persona: ₡${String.format("%,.0f", experience.price)}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Total: ₡${String.format("%,.0f", experience.price * people)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (validateFields()) {
                                viewModel.createBooking(
                                    experienceId = experienceId,
                                    people = people,
                                    name = name.trim(),
                                    email = email.trim(),
                                    phone = BookingUtils.phoneToApiFormat(rawPhoneInput),
                                    termsAccepted = termsAccepted,
                                    paymentMethod = selectedPaymentMethod
                                )
                            }
                        },
                        enabled = !uiState.isBooking && uiState.experience.remainingCapacity>0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        if (uiState.isBooking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Confirmar Reserva",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}