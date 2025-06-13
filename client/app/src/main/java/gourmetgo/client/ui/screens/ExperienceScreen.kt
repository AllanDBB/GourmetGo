package gourmetgo.client.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gourmetgo.client.viewmodel.ExperiencesViewModel
import gourmetgo.client.ui.components.ExperienceCard
import gourmetgo.client.ui.components.FilterChip

@Composable
fun ExperiencesScreen(
    viewModel: ExperiencesViewModel,
    onNavigateToProfile: () -> Unit,
    onNavigateToBooking: (String) -> Unit,
    onNavigateToRating: (String) -> Unit,
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    val focusManager = LocalFocusManager.current

    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GourmetGo",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 20.sp
                    )
                    Row {
                        IconButton(onClick = onNavigateToProfile) {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = "Perfil",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        IconButton(onClick = onLogout) {
                            Icon(
                                Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar Sesión",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.experiences.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cargando experiencias...")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            if (it.isBlank()) {
                                viewModel.clearSearch()
                            }
                        },
                        label = { Text("Buscar experiencias...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Buscar")
                        },
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = {
                                    searchText = ""
                                    viewModel.clearSearch()
                                    focusManager.clearFocus()
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (searchText.isNotBlank()) {
                                    viewModel.searchExperiences(searchText)
                                }
                                focusManager.clearFocus()
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        singleLine = true
                    )
                }

                if (uiState.categories.isNotEmpty() && uiState.searchQuery.isBlank()) {
                    LazyRow(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                onClick = { viewModel.clearCategoryFilter() },
                                label = { Text("Todas") },
                                selected = uiState.selectedCategory == null
                            )
                        }
                        items(uiState.categories) { category ->
                            FilterChip(
                                onClick = { viewModel.filterByCategory(category) },
                                label = { Text(category) },
                                selected = uiState.selectedCategory == category
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                val experiencesToShow = viewModel.getCurrentExperiences()

                if (experiencesToShow.isEmpty() && !uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = when {
                                    uiState.searchQuery.isNotBlank() -> "No se encontraron experiencias"
                                    uiState.selectedCategory != null -> "No hay experiencias en esta categoría"
                                    else -> "No hay experiencias disponibles"
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = when {
                                    uiState.searchQuery.isNotBlank() -> "Intenta con otros términos de búsqueda"
                                    uiState.selectedCategory != null -> "Prueba con otra categoría"
                                    else -> "Vuelve pronto para descubrir nuevas experiencias"
                                },
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (uiState.searchQuery.isNotBlank() || uiState.selectedCategory != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        searchText = ""
                                        viewModel.clearSearch()
                                        viewModel.clearCategoryFilter()
                                    }
                                ) {
                                    Text("Ver todas las experiencias")
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = viewModel.getCurrentSectionTitle(),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                if (uiState.isSearching) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }

                        items(experiencesToShow) { experience ->
                            ExperienceCard(
                                experience = experience,
                                onBookClick = { onNavigateToBooking(experience._id) },
                                onRateClick = { onNavigateToRating(experience._id) }
                            )
                        }

                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.refreshExperiences() },
                                    enabled = !uiState.refreshing
                                ) {
                                    if (uiState.refreshing) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Text("Actualizar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}