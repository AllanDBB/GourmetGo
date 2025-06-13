package gourmetgo.client.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.request.ImageRequest
import gourmetgo.client.ui.components.ExperienceCard
import gourmetgo.client.data.models.Experience
import gourmetgo.client.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onGoToExperiences: () -> Unit = {},
    onNavigateToExperienceDetails: (String) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onLogout: () -> Unit = {},
    currentNavIndex: Int = 0,
    onNavIndexChanged: (Int) -> Unit = {}
) {val context = LocalContext.current
    val uiState = viewModel.uiState
    
    var searchText by remember { mutableStateOf("") }
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    
    val categories = if (uiState.categories.isNotEmpty()) {
        listOf("Todas") + uiState.categories
    } else {
        listOf("Todas", "Eventos", "Clases", "Restaurantes")
    }
      
    LaunchedEffect(uiState.searchQuery) {
        if (searchText != uiState.searchQuery) {
            searchText = uiState.searchQuery
        }
    }
    
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    
    // Enhanced color scheme
    val primaryGreen = Color(0xFF2E7D32)
    val lightGreen = Color(0xFF4CAF50)
    val accentGreen = Color(0xFF66BB6A)
    val backgroundGray = Color(0xFFF8F9FA)
    val cardBackground = Color.White
    val textPrimary = Color(0xFF212121)
    val textSecondary = Color(0xFF757575)
    
    if (uiState.isLoading && !uiState.hasData()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = primaryGreen)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Cargando experiencias...",
                    color = textSecondary
                )
            }
        }
        return
    }
      Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundGray,
                        Color(0xFFF0F4F0)
                    )
                )
            )
    ) {        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 85.dp) // Ajustado para coincidir con la altura del navbar
        ) {
            // Top Bar con navegación
            TopAppBar(
                onNavigateToProfile = onNavigateToProfile,
                onLogout = onLogout,
                backgroundColor = backgroundGray,
                showingAllExperiences = uiState.showingAllExperiences,
                onBackToHome = { viewModel.showHomeView() }
            )
            
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Contenido condicional basado en el estado
                if (uiState.showingAllExperiences) {
                    // Vista de todas las experiencias
                    AllExperiencesSection(
                        experiences = uiState.allExperiences,
                        isLoading = uiState.isLoading,
                        onExperienceClick = onNavigateToExperienceDetails,
                        primaryColor = primaryGreen,
                        accentColor = accentGreen,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary
                    )
                } else {
                    // Vista home normal
                    HomeContent(
                        uiState = uiState,
                        searchText = searchText,
                        selectedCategoryIndex = selectedCategoryIndex,
                        categories = categories,
                        onSearchChange = { newText ->
                            searchText = newText
                            if (newText.isBlank()) {
                                viewModel.clearSearch()
                            } else {
                                viewModel.searchExperiences(newText)
                            }
                        },                        
                        onCategorySelected = { index, category ->
                            selectedCategoryIndex = index
                            if (index == 0) {
                                viewModel.clearCategoryFilter()
                            } else {
                                viewModel.filterByCategory(category)
                            }
                        },
                        onLoadAllExperiences = { viewModel.loadAllExperiences() },
                        onNavigateToExperienceDetails = onNavigateToExperienceDetails,
                        onGoToExperiences = onGoToExperiences,                        onRefresh = { viewModel.refreshData() },
                        primaryGreen = primaryGreen,
                        lightGreen = lightGreen,
                        accentGreen = accentGreen,
                        cardBackground = cardBackground,
                        textPrimary = textPrimary,                        textSecondary = textSecondary
                    )
                }
            }
        }
          // Bottom Navigation Bar
        BottomNavigationBar(
            currentIndex = currentNavIndex,
            onNavIndexChanged = onNavIndexChanged,
            onNavigateToHome = { 
                println("DEBUG: Click en Inicio")
                // Ya estamos en home, pero actualizamos el estado
            },
            onNavigateToHistory = {
                println("DEBUG: Click en Historial")
                onNavigateToHistory()
            },
            onNavigateToProfile = {
                println("DEBUG: Click en Perfil")
                onNavigateToProfile()
            },
            primaryColor = primaryGreen,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun HomeContent(
    uiState: gourmetgo.client.viewmodel.statesUi.HomeUiState,
    searchText: String,
    selectedCategoryIndex: Int,
    categories: List<String>,
    onSearchChange: (String) -> Unit,
    onCategorySelected: (Int, String) -> Unit,
    onLoadAllExperiences: () -> Unit,
    onNavigateToExperienceDetails: (String) -> Unit,
    onGoToExperiences: () -> Unit,    onRefresh: () -> Unit,
    primaryGreen: Color,
    lightGreen: Color,
    accentGreen: Color,
    cardBackground: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Welcome section
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "¡Hola!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Text(
                text = "Descubre experiencias gastronómicas únicas",
                fontSize = 16.sp,
                color = textSecondary,
                fontWeight = FontWeight.Medium
            )
        }
        
        // Enhanced search bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = primaryGreen.copy(alpha = 0.1f)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                placeholder = { 
                    Text(
                        "Buscar experiencias...", 
                        color = textSecondary
                    ) 
                },
                leadingIcon = { 
                    Icon(
                        Icons.Default.Search, 
                        contentDescription = "Buscar",
                        tint = lightGreen
                    ) 
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = lightGreen,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = cardBackground,
                    unfocusedContainerColor = cardBackground
                ),
                singleLine = true
            )
        }

        // Enhanced category tabs
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = primaryGreen.copy(alpha = 0.1f)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {            if (categories.size > 3) {
                // Si hay más de 3 categorías: "Todas" fija + resto scrolleable
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // "Todas" siempre visible y fija
                    val isTodasSelected = selectedCategoryIndex == 0
                    Card(
                        modifier = Modifier
                            .width(80.dp) // Ancho más pequeño para "Todas"
                            .clickable { 
                                onCategorySelected(0, categories[0])
                            }
                            .shadow(
                                elevation = if (isTodasSelected) 8.dp else 2.dp,
                                shape = RoundedCornerShape(16.dp),
                                ambientColor = if (isTodasSelected) primaryGreen.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.1f)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isTodasSelected) {
                                primaryGreen
                            } else {
                                Color(0xFFF5F5F5)
                            }
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = categories[0], // "Todas"
                                color = if (isTodasSelected) Color.White else textPrimary,
                                fontWeight = if (isTodasSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    
                    // Resto de categorías en LazyRow scrolleable
                    LazyRow(
                        modifier = Modifier
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(categories.size - 1) { index ->
                            val categoryIndex = index + 1 // +1 porque empezamos desde la segunda categoría
                            val category = categories[categoryIndex]
                            val isSelected = selectedCategoryIndex == categoryIndex
                            Card(
                                modifier = Modifier
                                    .width(120.dp)
                                    .clickable { 
                                        onCategorySelected(categoryIndex, category)
                                    }
                                    .shadow(
                                        elevation = if (isSelected) 8.dp else 2.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        ambientColor = if (isSelected) primaryGreen.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.1f)
                                    ),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        primaryGreen
                                    } else {
                                        Color(0xFFF5F5F5)
                                    }
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp, horizontal = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = category,
                                        color = if (isSelected) Color.White else textPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Si hay 3 o menos categorías, usar Row normal
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEachIndexed { index, category ->
                        val isSelected = selectedCategoryIndex == index
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { 
                                    onCategorySelected(index, category)
                                }
                                .shadow(
                                    elevation = if (isSelected) 8.dp else 2.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    ambientColor = if (isSelected) primaryGreen.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.1f)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) {
                                    primaryGreen
                                } else {
                                    Color(0xFFF5F5F5)
                                }
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = category,
                                    color = if (isSelected) Color.White else textPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }}        // Filtrar experiencias localmente basado en categoría seleccionada
        val selectedCategory = if (selectedCategoryIndex == 0) null else categories[selectedCategoryIndex]
        
        val filteredPopularExperiences = if (selectedCategory != null) {
            uiState.popularExperiences.filter { experience ->
                experience.category.equals(selectedCategory, ignoreCase = true) ||
                experience.category.lowercase().contains(selectedCategory.lowercase()) ||
                (selectedCategory.lowercase() == "eventos" && experience.category.lowercase().contains("evento")) ||
                (selectedCategory.lowercase() == "clases" && (experience.category.lowercase().contains("clase") || experience.category.lowercase().contains("taller"))) ||
                (selectedCategory.lowercase() == "restaurantes" && experience.category.lowercase().contains("restaurante"))
            }
        } else {
            uiState.popularExperiences
        }
        
        val filteredUpcomingExperiences = if (selectedCategory != null) {
            uiState.upcomingExperiences.filter { experience ->
                experience.category.equals(selectedCategory, ignoreCase = true) ||
                experience.category.lowercase().contains(selectedCategory.lowercase()) ||
                (selectedCategory.lowercase() == "eventos" && experience.category.lowercase().contains("evento")) ||
                (selectedCategory.lowercase() == "clases" && (experience.category.lowercase().contains("clase") || experience.category.lowercase().contains("taller"))) ||
                (selectedCategory.lowercase() == "restaurantes" && experience.category.lowercase().contains("restaurante"))
            }
        } else {
            uiState.upcomingExperiences
        }
        
        // "Los más populares" section
        if (filteredPopularExperiences.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {                    Text(
                        text = if (selectedCategory != null) {
                            "Experiencias de ${selectedCategory}"
                        } else {
                            "Los más populares"
                        },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    TextButton(
                        onClick = onGoToExperiences,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = lightGreen
                        )
                    ) {
                    }                }
                
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredPopularExperiences) { experience ->
                        EnhancedExperienceCard(
                            experience = experience,
                            onClick = { onNavigateToExperienceDetails(experience._id) },
                            primaryColor = primaryGreen,
                            accentColor = accentGreen
                        )
                    }
                }
            }
        }
          // "Próximamente" section - Solo mostrar si no hay filtro de categoría o si hay experiencias próximas filtradas
        if (filteredUpcomingExperiences.isNotEmpty() && selectedCategory == null) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Próximamente",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    TextButton(
                        onClick = onGoToExperiences,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = lightGreen
                        )
                    ) {
                    }
                }
                  LazyRow(
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredUpcomingExperiences) { experience ->
                        EnhancedExperienceCard(
                            experience = experience,
                            onClick = { onNavigateToExperienceDetails(experience._id) },
                            primaryColor = primaryGreen,
                            accentColor = accentGreen,
                            isUpcoming = true
                        )
                    }
                }
            }
        }
          // Empty state with "Ver todas las experiencias" button
        if ((filteredPopularExperiences.isEmpty() && filteredUpcomingExperiences.isEmpty()) && !uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_search),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = textSecondary
                    )
                    Text(
                        text = when {
                            uiState.searchQuery.isNotBlank() -> "No se encontraron experiencias"
                            selectedCategory != null -> "No hay experiencias en esta categoría"
                            else -> "No hay experiencias disponibles"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = textPrimary
                    )
                    Text(
                        text = when {
                            uiState.searchQuery.isNotBlank() -> "Intenta con otros términos de búsqueda"
                            selectedCategory != null -> "Prueba con otra categoría"
                            else -> "Vuelve pronto para descubrir nuevas experiencias"
                        },
                        fontSize = 14.sp,
                        color = textSecondary
                    )
                    
                    Button(
                        onClick = onLoadAllExperiences,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = lightGreen
                        )
                    ) {
                        Text("Ver todas las experiencias")
                    }
                }
            }
        }
        
        // Refresh button
        if (uiState.hasData()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                OutlinedButton(
                    onClick = onRefresh,
                    enabled = !uiState.refreshing,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = lightGreen
                    )
                ) {
                    if (uiState.refreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = lightGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Actualizar")
                }
            }
        }
    }
}

@Composable
private fun TopAppBar(
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    backgroundColor: Color,
    showingAllExperiences: Boolean,
    onBackToHome: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = backgroundColor,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo/Title section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (showingAllExperiences) {
                    IconButton(
                        onClick = onBackToHome,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_revert),
                            contentDescription = "Volver al inicio",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                // Logo/App Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column {
                        Text(
                            text = if (showingAllExperiences) "Todas las Experiencias" else "GourmetGo",
                            fontSize = if (showingAllExperiences) 18.sp else 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        if (!showingAllExperiences) {
                            Text(
                                text = "Experiencias Gastronómicas",
                                fontSize = 12.sp,
                                color = Color(0xFF66BB6A),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            
            // Action buttons with better design
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically            ) {
                // Logout button
                Card(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFF5722).copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_lock_power_off),
                            contentDescription = "Cerrar sesión",
                            tint = Color(0xFFFF5722),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AllExperiencesSection(
    experiences: List<Experience>,
    isLoading: Boolean,
    onExperienceClick: (String) -> Unit,
    primaryColor: Color,
    accentColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Enhanced Header with stats
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = primaryColor.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Todas las Experiencias",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        text = "${experiences.size} experiencias disponibles",
                        fontSize = 14.sp,
                        color = textSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Stats icon
                Card(
                    modifier = Modifier.size(48.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = primaryColor
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
        
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = primaryColor,
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp
                    )
                    Text(
                        text = "Cargando todas las experiencias...",
                        fontSize = 16.sp,
                        color = textSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else if (experiences.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_search),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = textSecondary.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "No hay experiencias disponibles",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        text = "Pronto habrá nuevas experiencias gastronómicas esperándote",
                        fontSize = 14.sp,
                        color = textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }        } else {
            // Enhanced Grid de experiencias con mejor layout
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 280.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(experiences) { experience ->
                    EnhancedExperienceCard(
                        experience = experience,
                        onClick = { onExperienceClick(experience._id) },
                        primaryColor = primaryColor,
                        accentColor = accentColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )
                }
            }
            
            // Footer info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_dialog_info),
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "¡Explora todas nuestras experiencias gastronómicas!",
                        fontSize = 14.sp,
                        color = textSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedExperienceCard(
    experience: Experience,
    onClick: () -> Unit,
    primaryColor: Color,
    accentColor: Color,
    isUpcoming: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(280.dp)
            .height(200.dp)
            .clickable { onClick() }
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = primaryColor.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                primaryColor.copy(alpha = 0.05f)
                            )
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header section
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = if (isUpcoming) "Próximamente" else "Disponible",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isUpcoming) Color(0xFFFF6B35) else accentColor,
                            modifier = Modifier
                                .background(
                                    color = if (isUpcoming) Color(0xFFFF6B35).copy(alpha = 0.1f) else accentColor.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)                        )
                          // Imagen de la experiencia en lugar del ícono
                        if (experience.images.isNotEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(experience.images.first())
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Imagen de ${experience.title}",
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(
                                        color = primaryColor.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentScale = ContentScale.Crop,
                                error = painterResource(id = android.R.drawable.ic_menu_gallery),
                                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                                contentDescription = null,
                                tint = primaryColor.copy(alpha = 0.3f),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                    
                    Text(
                        text = experience.title.ifBlank { "Experiencia Gastronómica" },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    
                    Text(
                        text = experience.description.ifBlank { "Descubre sabores únicos en esta increíble experiencia culinaria" },
                        fontSize = 14.sp,
                        color = Color(0xFF757575),
                        lineHeight = 18.sp,
                        maxLines = 2
                    )
                }
                
                // Footer section with price and rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Desde",
                            fontSize = 12.sp,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = if (experience.price > 0) "₡${String.format("%,.0f", experience.price)}" else "₡50.000",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.btn_star_big_on),
                            contentDescription = null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "4.8",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF212121)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    currentIndex: Int,
    onNavIndexChanged: (Int) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(85.dp),
        color = Color.White,
        shadowElevation = 12.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {            // Inicio
            EnhancedBottomNavItem(
                icon = Icons.Default.Home,
                label = "Inicio",
                isSelected = currentIndex == 0,
                onClick = {
                    println("DEBUG: EnhancedBottomNavItem - Inicio clicked")
                    onNavIndexChanged(0)
                    onNavigateToHome()
                },
                primaryColor = primaryColor
            )
            
            // Historial
            EnhancedBottomNavItem(
                icon = Icons.Default.History,
                label = "Historial",
                isSelected = currentIndex == 1,
                onClick = {
                    println("DEBUG: EnhancedBottomNavItem - Historial clicked")
                    onNavIndexChanged(1)
                    onNavigateToHistory()
                },
                primaryColor = primaryColor
            )
            
            // Perfil
            EnhancedBottomNavItem(
                icon = Icons.Default.Person,
                label = "Perfil",
                isSelected = currentIndex == 2,
                onClick = {
                    println("DEBUG: EnhancedBottomNavItem - Perfil clicked")
                    onNavIndexChanged(2)
                    onNavigateToProfile()
                },
                primaryColor = primaryColor
            )
        }
    }
}

@Composable
private fun EnhancedBottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    primaryColor: Color
) {
    Column(
        modifier = Modifier
            .clickable { 
                println("DEBUG: Item $label clicked")
                onClick() 
            }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) primaryColor else Color(0xFF9E9E9E),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) primaryColor else Color(0xFF9E9E9E),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        // Indicador simple
        Box(
            modifier = Modifier
                .width(6.dp)
                .height(2.dp)
                .background(
                    color = if (isSelected) primaryColor else Color.Transparent,
                    shape = RoundedCornerShape(1.dp)
                )
        )
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    primaryColor: Color
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = if (isSelected) primaryColor.copy(alpha = 0.1f) else Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) primaryColor else Color(0xFF757575),
                modifier = Modifier.size(24.dp)
            )
        }
        
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) primaryColor else Color(0xFF757575)
        )
    }
}
