package gourmetgo.client.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.lifecycle.viewmodel.compose.viewModel
import gourmetgo.client.viewmodel.statesUi.ExperienceDetailsUiState
import gourmetgo.client.data.repository.ExperienceDetailsRepository
import gourmetgo.client.viewmodel.ExperienceDetailsViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceDetailsScreen(
    viewModel: ExperienceDetailsViewModel,
    onBack: (() -> Unit)? = null
) {
    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de la experiencia") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: ${uiState.error}")
                }
            }
            uiState.experience != null -> {
                val experience = uiState.experience
                var showImageDialog by remember { mutableStateOf<String?>(null) }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Título y estado
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = experience.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "¡${experience.status}!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    // Categoría
                    Text(
                        text = "Categoría: ${experience.category}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .border(1.dp, MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.small)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                    // Imagen principal
                    if (experience.images.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .padding(vertical = 8.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline)
                                .clickable { showImageDialog = experience.images[0] },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = experience.images[0],
                                contentDescription = "Imagen principal",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    // Detalles principales
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            // Formatear fecha y hora entendibles usando SimpleDateFormat
                            val formattedDateTime = try {
                                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                                val outputFormat = SimpleDateFormat("d 'de' MMM yyyy, HH:mm", Locale("es"))
                                val dateStr = experience.date.trim().replace("T", " ").replace("Z", "")
                                val date: Date? = inputFormat.parse(dateStr)
                                if (date != null) outputFormat.format(date) else experience.date
                            } catch (e: Exception) {
                                experience.date
                            }
                            Text(formattedDateTime, style = MaterialTheme.typography.bodyMedium)
                            Text("${experience.duration} hrs", style = MaterialTheme.typography.bodyMedium)
                            Text("$${experience.price}", style = MaterialTheme.typography.bodyMedium)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Capacidad: ${experience.capacity}", style = MaterialTheme.typography.bodyMedium)
                            Text(experience.location, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    // Disponibilidad
                    Text(
                        text = "¡${experience.remainingCapacity} espacios disponibles!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    // Descripción
                    Text(
                        text = experience.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    // Imágenes secundarias (dinámicas)
                    if (experience.images.size > 1) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            experience.images.drop(1).forEach { img ->
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .border(1.dp, MaterialTheme.colorScheme.outline)
                                        .padding(4.dp)
                                        .clickable { showImageDialog = img },
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = img,
                                        contentDescription = "Imagen secundaria",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                    // Recomendaciones o requerimientos
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        experience.requirements.split("\n").forEach {
                            Text(it, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    // Menú
                    if (experience.menu.image.isNotBlank() || experience.menu.text.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (experience.menu.image.isNotBlank()) {
                                AsyncImage(
                                    model = experience.menu.image,
                                    contentDescription = "Imagen menú",
                                    modifier = Modifier.size(80.dp).padding(end = 8.dp).clickable { showImageDialog = experience.menu.image },
                                    contentScale = ContentScale.Crop
                                )
                            }
                            if (experience.menu.text.isNotBlank()) {
                                Column(
                                    modifier = Modifier
                                        .border(1.dp, MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.small)
                                        .padding(8.dp)
                                ) {
                                    Text("MENU", style = MaterialTheme.typography.labelLarge)
                                    experience.menu.text.split("\n").forEach {
                                        Text(it, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                    // Botones
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { /* TODO: Ver asistencia */ }) {
                            Text("Ver asistencia")
                        }
                        OutlinedButton(onClick = { /* TODO: Editar */ }) {
                            Text("Editar")
                        }
                    }
                }
                // Diálogo para mostrar imagen en grande
                if (showImageDialog != null) {
                    Dialog(onDismissRequest = { showImageDialog = null }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = showImageDialog,
                                contentDescription = "Imagen ampliada",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }
        }
    }
}