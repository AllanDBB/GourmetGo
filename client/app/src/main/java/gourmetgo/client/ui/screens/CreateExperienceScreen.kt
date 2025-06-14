package gourmetgo.client.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import gourmetgo.client.viewmodel.CreateExperienceViewModel
import gourmetgo.client.ui.components.ProfileTextField
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExperienceScreen(
    viewModel: CreateExperienceViewModel,
    onNavigateBack: () -> Unit,
    onCreateSuccess: () -> Unit
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()

    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedMenuImage by remember { mutableStateOf<Uri?>(null) }

    val calendar = remember { Calendar.getInstance() }

    // Date picker
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
    // Time picker
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

    LaunchedEffect(uiState.createSuccess) {
        if (uiState.createSuccess) {
            Toast.makeText(context, "Experiencia creada", Toast.LENGTH_SHORT).show()
            onCreateSuccess()
        }
    }
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }
    if (uiState.error != null) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text(
                text = uiState.error ?: "",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }
    }

    // Picker para imágenes múltiples
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            selectedImages = selectedImages + uris.filter { it !in selectedImages }
        }
    }
    // Picker para imagen de menú
    val menuImagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedMenuImage = uri
        }
    }

    val categoryOptions = listOf("Taller", "Cena", "Almuerzo", "Desayuno", "Brunch", "Cata", "Showcooking", "Degustación", "Otro")
    val statusOptions = listOf("Activa", "Próximamente")
    var categoryExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

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
                    text = "Crear experiencia",
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
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
                label = { Text("Nombre de la experiencia") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    label = { Text("Fecha") },
                    readOnly = true,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDatePicker = true },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha")
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = {},
                    label = { Text("Hora") },
                    readOnly = true,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showTimePicker = true },
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(Icons.Default.AccessTime, contentDescription = "Seleccionar hora")
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.duration.takeIf { it > 0 }?.toString() ?: "",
                    onValueChange = { viewModel.updateDuration(it.toIntOrNull() ?: 0) },
                    label = { Text("Duración (h)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = uiState.category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        modifier = Modifier.menuAnchor().weight(1f),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categoryOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    viewModel.updateCategory(option)
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.capacity.takeIf { it > 0 }?.toString() ?: "",
                    onValueChange = { viewModel.updateCapacity(it.toIntOrNull() ?: 0) },
                    label = { Text("Capacidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded }
                ) {
                    OutlinedTextField(
                        value = uiState.status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado") },
                        modifier = Modifier.menuAnchor().weight(1f),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        isError = uiState.status.isNotBlank() && uiState.status !in statusOptions
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    viewModel.updateStatus(option)
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Descripción detallada...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.price.takeIf { it > 0 }?.toString() ?: "",
                onValueChange = { viewModel.updatePrice(it.toDoubleOrNull() ?: 0.0) },
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.location,
                onValueChange = { viewModel.updateLocation(it) },
                label = { Text("Ubicación") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.requirements,
                onValueChange = { viewModel.updateRequirements(it) },
                label = { Text("Requisitos o recomendaciones especiales (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Imágenes
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Imágenes:", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar imagen")
                }
                selectedImages.forEach { uri ->
                    Box(modifier = Modifier.padding(2.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(4.dp),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { selectedImages = selectedImages - uri },
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Eliminar imagen", tint = Color.Red)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Menú
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.text,
                    onValueChange = { viewModel.updateMenuText(it) },
                    label = { Text("Escribe el menú:") },
                    modifier = Modifier.weight(1f),
                    minLines = 2
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("O menú por medio de imagen:")
                    IconButton(onClick = { menuImagePickerLauncher.launch("image/*") }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar menú imagen")
                    }
                    selectedMenuImage?.let { uri ->
                        Box {
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { selectedMenuImage = null },
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.TopEnd)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Eliminar menú imagen", tint = Color.Red)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.updateImages(selectedImages)
                    viewModel.updateMenuImage(selectedMenuImage)
                    viewModel.updateDate(if (date.isNotBlank() && time.isNotBlank()) "${date}T${time}:00Z" else "")
                    viewModel.createExperience()
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
                        text = "Crear",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

