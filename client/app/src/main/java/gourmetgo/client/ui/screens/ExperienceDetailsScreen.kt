package gourmetgo.client.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import gourmetgo.client.viewmodel.statesUi.ExperienceDetailsUiState
import gourmetgo.client.data.repository.ExperienceDetailsRepository
import gourmetgo.client.viewmodel.ExperienceDetailsViewModel

@Composable
fun ExperienceDetailsScreen(
    viewModel: ExperienceDetailsViewModel
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
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = experience.name, style = MaterialTheme.typography.headlineMedium)
                    Text(text = "Anfitrión: ${experience.host}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Ubicación: ${experience.location}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Fecha: ${experience.date}", style = MaterialTheme.typography.bodyMedium)
                    Divider()
                    Text(text = experience.description, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}