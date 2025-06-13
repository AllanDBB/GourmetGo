package gourmetgo.client.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gourmetgo.client.viewmodel.UpdateExperienceViewModel
import gourmetgo.client.viewmodel.DeleteExperienceViewModel
import gourmetgo.client.ui.components.ProfileTextField
import java.util.Calendar
import androidx.compose.material3.TextFieldDefaults

@Composable
fun UpdateExperienceScreen(
    viewModel: UpdateExperienceViewModel,
    onNavigateBack: () -> Unit,
    onDelete: () -> Unit,
    onUpdateSuccess: () -> Unit,
    deleteExperienceViewModel: DeleteExperienceViewModel // <-- Add this parameter
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()

    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val deleteUiState = deleteExperienceViewModel.uiState
    var showDeleteEmailDialog by remember { mutableStateOf(false) }
    var showDeleteCodeDialog by remember { mutableStateOf(false) }
    var deleteEmail by remember { mutableStateOf("") }
    var deleteCode by remember { mutableStateOf("") }
    var localDeleteError by remember { mutableStateOf<String?>(null) }
    var deleteSuccess by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }

    // Mostrar DatePickerDialog de forma segura con LaunchedEffect
    LaunchedEffect(showDatePicker) {
        if (showDatePicker) {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val mm = (month + 1).toString().padStart(2, '0')
                    val dd = dayOfMonth.toString().padStart(2, '0')
                    date = "$year-$mm-$dd"
                    showDatePicker = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
    // Mostrar TimePickerDialog de forma segura con LaunchedEffect
    LaunchedEffect(showTimePicker) {
        if (showTimePicker) {
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val hh = hourOfDay.toString().padStart(2, '0')
                    val mm = minute.toString().padStart(2, '0')
                    time = "$hh:$mm"
                    showTimePicker = false
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    LaunchedEffect(uiState.experience) {
        uiState.experience?.let { exp ->
            date = exp.date.takeIf { it.length >= 10 }?.substring(0, 10) ?: ""
            time = exp.date.takeIf { it.length >= 16 }?.substring(11, 16) ?: ""
            capacity = exp.capacity.toString()
            status = exp.status
            price = exp.price.toString()
            location = exp.location
        }
    }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            Toast.makeText(context, "Experiencia actualizada", Toast.LENGTH_SHORT).show()
            onUpdateSuccess()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    // Show error toast for delete flow
    LaunchedEffect(deleteUiState.error) {
        deleteUiState.error?.let {
            localDeleteError = it
        }
    }

    // Show code dialog after email request success
    LaunchedEffect(deleteUiState.isLoading) {
        if (!deleteUiState.isLoading && showDeleteEmailDialog && localDeleteError == null && deleteEmail.isNotBlank()) {
            showDeleteEmailDialog = false
            showDeleteCodeDialog = true
        }
    }

    // Handle delete success (no error, not loading, after code submit)
    LaunchedEffect(deleteUiState, deleteSuccess) {
        if (!deleteUiState.isLoading && localDeleteError == null && deleteSuccess) {
            Toast.makeText(context, "Experiencia eliminada", Toast.LENGTH_SHORT).show()
            showDeleteCodeDialog = false
            onDelete()
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
                    text = "Editar experiencia",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
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
            Text(
                text = uiState.experience?.title ?: "Nombre de la experiencia",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            // Fecha
            OutlinedTextField(
                value = date,
                onValueChange = {},
                label = { Text("Fecha") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha")
                    }
                },
                enabled = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Hora
            OutlinedTextField(
                value = time,
                onValueChange = {},
                label = { Text("Hora") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimePicker = true },
                trailingIcon = {
                    IconButton(onClick = { showTimePicker = true }) {
                        Icon(Icons.Default.AccessTime, contentDescription = "Seleccionar hora")
                    }
                },
                enabled = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            ProfileTextField(
                label = "Capacidad",
                value = capacity,
                onValueChange = { capacity = it.filter { c -> c.isDigit() } },
                placeholder = "Ej: 10",
                keyboardType = KeyboardType.Number,
                focusManager = focusManager
            )
            // Estado (ComboBox)
            var expanded by remember { mutableStateOf(false) }
            val statusOptions = listOf("Activa", "Próximamente", "Agotada")
            val isProximamente = status == "Próximamente"
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = status,
                    onValueChange = {},
                    label = { Text("Estado") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Expandir")
                        }
                    }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    statusOptions.forEach { option ->
                        val enabled = if (isProximamente && option == "Activa") true else option == status
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                if (enabled) {
                                    status = option
                                    expanded = false
                                }
                            },
                            enabled = enabled
                        )
                    }
                }
            }
            ProfileTextField(
                label = "Precio",
                value = price,
                onValueChange = { price = it.filter { c -> c.isDigit() || c == '.' } },
                placeholder = "Ej: 100.0",
                keyboardType = KeyboardType.Decimal,
                focusManager = focusManager
            )
            ProfileTextField(
                label = "Ubicación",
                value = location,
                onValueChange = { location = it },
                placeholder = "Dirección",
                keyboardType = KeyboardType.Text,
                focusManager = focusManager
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    val experience = uiState.experience
                    val today = java.time.LocalDate.now()
                    val selectedDate = try {
                        java.time.LocalDate.parse(date)
                    } catch (e: Exception) { null }
                    val originalCapacity = experience?.capacity ?: 0
                    val remainingCapacity = experience?.remainingCapacity ?: 0
                    val alreadyBooked = originalCapacity - remainingCapacity
                    val newCapacity = capacity.toIntOrNull() ?: 0
                    val newPrice = price.toDoubleOrNull() ?: 0.0


                    val isLocationUrl = android.util.Patterns.WEB_URL.matcher(location).matches()

                    when {
                        selectedDate == null || selectedDate.isBefore(today) -> {
                            Toast.makeText(context, "La fecha no puede ser anterior a hoy", Toast.LENGTH_LONG).show()
                        }
                        newCapacity < alreadyBooked -> {
                            Toast.makeText(context, "La capacidad no puede ser menor que las reservas ya hechas ($alreadyBooked)", Toast.LENGTH_LONG).show()
                        }
                        newCapacity <= 0 -> {
                            Toast.makeText(context, "La capacidad debe ser mayor a cero", Toast.LENGTH_LONG).show()
                        }
                        newPrice <= 0.0 -> {
                            Toast.makeText(context, "El precio debe ser mayor a cero", Toast.LENGTH_LONG).show()
                        }
                        !isLocationUrl -> {
                            Toast.makeText(context, "La ubicación debe ser un link válido", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            viewModel.updateExperience(
                                date = "${date}T${time}:00Z",
                                location = location,
                                status = status,
                                capacity = newCapacity,
                                price = newPrice
                            )
                        }
                    }
                },
                enabled = !uiState.isLoading,
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
                        text = "Guardar cambios",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (status != "Agotada") {
                OutlinedButton(
                    onClick = { showDeleteEmailDialog = true },
                    enabled = !uiState.isLoading && !deleteUiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    // --- Delete Email Dialog ---
    if (showDeleteEmailDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteEmailDialog = false; deleteEmail = ""; localDeleteError = null },
            title = { Text("¿Eliminar experiencia?") },
            text = {
                Column {
                    Text("Esta acción es permanente. Ingresa tu correo para continuar.", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = deleteEmail,
                        onValueChange = { deleteEmail = it },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = localDeleteError != null,
                        supportingText = localDeleteError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        localDeleteError = null
                        if (deleteEmail.isBlank()) {
                            localDeleteError = "El correo es obligatorio"
                        } else {
                            deleteExperienceViewModel.RequestDeleteExperience(deleteEmail)
                        }
                    },
                    enabled = !deleteUiState.isLoading
                ) {
                    if (deleteUiState.isLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteEmailDialog = false; deleteEmail = ""; localDeleteError = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
    // --- Delete Code Dialog ---
    if (showDeleteCodeDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteCodeDialog = false; deleteCode = ""; localDeleteError = null },
            title = { Text("Verificación de eliminación") },
            text = {
                Column {
                    Text("Ingresa el código de verificación enviado a tu correo.")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = deleteCode,
                        onValueChange = { deleteCode = it },
                        label = { Text("Código de verificación") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = localDeleteError != null,
                        supportingText = localDeleteError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        localDeleteError = null
                        if (deleteCode.isBlank()) {
                            localDeleteError = "El código es obligatorio"
                        } else {
                            deleteSuccess = true
                            deleteExperienceViewModel.DeleteExperience(deleteCode)
                        }
                    },
                    enabled = !deleteUiState.isLoading
                ) {
                    if (deleteUiState.isLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Text("Eliminar definitivamente")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteCodeDialog = false; deleteCode = ""; localDeleteError = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

