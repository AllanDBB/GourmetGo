package gourmetgo.client.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gourmetgo.client.viewmodel.UpdateExperienceViewModel
import gourmetgo.client.ui.components.ProfileTextField
import java.util.Calendar

@Composable
fun UpdateExperienceScreen(
    viewModel: UpdateExperienceViewModel,
    onNavigateBack: () -> Unit,
    onDelete: () -> Unit,
    onUpdateSuccess: () -> Unit
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

    val calendar = remember { Calendar.getInstance() }
    // DatePicker
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val mm = (month + 1).toString().padStart(2, '0')
            val dd = dayOfMonth.toString().padStart(2, '0')
            date = "$year-$mm-$dd"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    // TimePicker
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val hh = hourOfDay.toString().padStart(2, '0')
            val mm = minute.toString().padStart(2, '0')
            time = "$hh:$mm"
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ProfileTextField(
                    label = "Fecha",
                    value = date,
                    onValueChange = {},
                    placeholder = "YYYY-MM-DD",
                    keyboardType = KeyboardType.Text,
                    focusManager = focusManager,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { datePickerDialog.show() }) {
                    Text("📅")
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ProfileTextField(
                    label = "Hora",
                    value = time,
                    onValueChange = {},
                    placeholder = "HH:MM",
                    keyboardType = KeyboardType.Text,
                    focusManager = focusManager,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { timePickerDialog.show() }) {
                    Text("⏰")
                }
            }
            ProfileTextField(
                label = "Capacidad",
                value = capacity,
                onValueChange = { capacity = it.filter { c -> c.isDigit() } },
                placeholder = "Ej: 10",
                keyboardType = KeyboardType.Number,
                focusManager = focusManager
            )
            ProfileTextField(
                label = "Estado",
                value = status,
                onValueChange = { status = it },
                placeholder = "Activa/Inactiva",
                keyboardType = KeyboardType.Text,
                focusManager = focusManager
            )
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
                    viewModel.updateExperience(
                        date = "${date}T${time}:00Z",
                        location = location,
                        status = status,
                        capacity = capacity.toIntOrNull() ?: 0,
                        price = price.toDoubleOrNull() ?: 0.0
                    )
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
            OutlinedButton(
                onClick = onDelete,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors()
            ) {
                Text("Eliminar", color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

