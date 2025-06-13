package gourmetgo.client.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gourmetgo.client.utils.BookingHistoryUtils
import gourmetgo.client.viewmodel.BookingHistoryViewModel
import gourmetgo.client.data.models.dtos.BookingSummary
import gourmetgo.client.ui.components.FilterChip
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookingHistoryScreen(
    viewModel: BookingHistoryViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRating: (BookingSummary) -> Unit = {}
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState

    // Estados para filtros
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedDateFilter by remember { mutableStateOf<DateFilter?>(null) }
    var selectedStatusFilter by remember { mutableStateOf<String?>(null) }
    var selectedLocationFilter by remember { mutableStateOf<String?>(null) }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadBookingHistory()
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            Column {
                // Header principal
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "Historial de Reservas",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    // Botón de filtros
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filtros",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Barra de búsqueda
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por nombre de experiencia...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    singleLine = true
                )

                // Chips de filtros activos
                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        selectedDateFilter?.let { dateFilter ->
                            FilterChip(
                                onClick = { selectedDateFilter = null },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(dateFilter.displayName, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Quitar filtro",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                },
                                selected = true
                            )
                        }
                    }

                    item {
                        selectedStatusFilter?.let { status ->
                            FilterChip(
                                onClick = { selectedStatusFilter = null },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(getStatusDisplayText(status), fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Quitar filtro",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                },
                                selected = true
                            )
                        }
                    }

                    item {
                        selectedLocationFilter?.let { location ->
                            FilterChip(
                                onClick = { selectedLocationFilter = null },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(location, fontSize = 12.sp, maxLines = 1)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Quitar filtro",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                },
                                selected = true
                            )
                        }
                    }

                    item {
                        selectedCategoryFilter?.let { category ->
                            FilterChip(
                                onClick = { selectedCategoryFilter = null },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(category, fontSize = 12.sp, maxLines = 1)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Quitar filtro",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                },
                                selected = true
                            )
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Filtrar reservas según los criterios seleccionados
            val filteredBookings = filterBookings(
                bookings = uiState.bookings,
                searchQuery = searchQuery,
                dateFilter = selectedDateFilter,
                statusFilter = selectedStatusFilter,
                locationFilter = selectedLocationFilter,
                categoryFilter = selectedCategoryFilter
            )

            // Separar reservas futuras y pasadas
            val now = Calendar.getInstance().time
            val (futureBookings, pastBookings) = filteredBookings.partition { booking ->
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    val expDate = sdf.parse(booking.experience.date)
                    expDate != null && expDate.after(now)
                } catch (e: Exception) {
                    false
                }
            }

            if (filteredBookings.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Event,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (uiState.bookings.isEmpty()) "No tienes reservas" else "No se encontraron reservas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (uiState.bookings.isEmpty())
                                "Cuando hagas una reserva aparecerá aquí"
                            else
                                "Intenta ajustar los filtros de búsqueda",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Agregar header con estadísticas
                    item {
                        BookingStatsCard(bookings = uiState.bookings)
                    }

                    if (futureBookings.isNotEmpty()) {
                        item {
                            Text(
                                text = "Próximamente",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                            )
                        }
                        items(futureBookings) { booking ->
                            EnhancedBookingHistoryCard(
                                booking = booking,
                                onDownloadPDF = {
                                    viewModel.downloadBookingPDF(context, booking) { _, message ->
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    }
                                },
                                onRateClick = {
                                    onNavigateToRating(booking)
                                }
                            )
                        }
                    }

                    if (pastBookings.isNotEmpty()) {
                        item {
                            Text(
                                text = "Pasados",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                            )
                        }
                        items(pastBookings) { booking ->
                            EnhancedBookingHistoryCard(
                                booking = booking,
                                onDownloadPDF = {
                                    viewModel.downloadBookingPDF(context, booking) { _, message ->
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    }
                                },
                                onRateClick = {
                                    onNavigateToRating(booking)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Dialog de filtros
        if (showFilterDialog) {
            FilterDialog(
                onDismiss = { showFilterDialog = false },
                onApplyFilters = { dateFilter, statusFilter, locationFilter, categoryFilter ->
                    selectedDateFilter = dateFilter
                    selectedStatusFilter = statusFilter
                    selectedLocationFilter = locationFilter
                    selectedCategoryFilter = categoryFilter
                    showFilterDialog = false
                },
                availableLocations = getAvailableLocations(uiState.bookings),
                availableCategories = getAvailableCategories(uiState.bookings),
                currentDateFilter = selectedDateFilter,
                currentStatusFilter = selectedStatusFilter,
                currentLocationFilter = selectedLocationFilter,
                currentCategoryFilter = selectedCategoryFilter
            )
        }
    }
}

// Card de estadísticas
@Composable
fun BookingStatsCard(bookings: List<BookingSummary>) {
    val stats = calculateBookingStats(bookings)

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
                text = "Resumen",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Total",
                    value = stats.total.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    label = "Confirmadas",
                    value = stats.confirmed.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    label = "Canceladas",
                    value = stats.cancelled.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    label = "Asistidas",
                    value = stats.attended.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = color.copy(alpha = 0.8f)
        )
    }
}

// Card mejorada para booking history
@Composable
fun EnhancedBookingHistoryCard(
    booking: BookingSummary,
    onDownloadPDF: () -> Unit,
    onRateClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con título y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.experience.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Código: ${booking.bookingCode}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Estado mejorado
                Surface(
                    color = getStatusColor(booking.status),
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = getStatusIcon(booking.status),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = getStatusTextColor(booking.status)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = getStatusDisplayText(booking.status),
                            fontSize = 12.sp,
                            color = getStatusTextColor(booking.status),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información de la experiencia
            ExperienceInfoRow(
                icon = Icons.Default.CalendarToday,
                text = BookingHistoryUtils.formatCreatedDate(booking.experience.date)
            )

            ExperienceInfoRow(
                icon = Icons.Default.LocationOn,
                text = booking.experience.location.take(50) + if (booking.experience.location.length > 50) "..." else ""
            )

            ExperienceInfoRow(
                icon = Icons.Default.Person,
                text = "${booking.people} ${if (booking.people == 1) "persona" else "personas"}"
            )

            ExperienceInfoRow(
                icon = Icons.Default.CreditCard,
                text = booking.paymentMethod
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Información de reserva
            Text(
                text = "Reservado: ${BookingHistoryUtils.formatCreatedDate(booking.createdAt)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botones de acción
            val isPastBooking = try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val expDate = sdf.parse(booking.experience.date)
                expDate != null && expDate.before(Calendar.getInstance().time)
            } catch (e: Exception) {
                false
            }

            if (booking.status == "confirmed" && isPastBooking) {
                // Mostrar botones de calificar y descargar PDF lado a lado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onRateClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Calificar",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Calificar",
                            fontSize = 14.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onDownloadPDF,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Descargar",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PDF",
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                // Solo mostrar botón de descarga PDF
                OutlinedButton(
                    onClick = onDownloadPDF,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        Icons.Default.Download,
                        contentDescription = "Descargar",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Descargar Comprobante PDF",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ExperienceInfoRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Dialog de filtros
@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onApplyFilters: (DateFilter?, String?, String?, String?) -> Unit,
    availableLocations: List<String>,
    availableCategories: List<String>,
    currentDateFilter: DateFilter?,
    currentStatusFilter: String?,
    currentLocationFilter: String?,
    currentCategoryFilter: String?
) {
    var selectedDateFilter by remember { mutableStateOf(currentDateFilter) }
    var selectedStatusFilter by remember { mutableStateOf(currentStatusFilter) }
    var selectedLocationFilter by remember { mutableStateOf(currentLocationFilter) }
    var selectedCategoryFilter by remember { mutableStateOf(currentCategoryFilter) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Filtros",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                // Filtro por fecha
                Text(
                    text = "Fecha",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    items(DateFilter.entries) { dateFilter ->
                        FilterChip(
                            onClick = {
                                selectedDateFilter = if (selectedDateFilter == dateFilter) null else dateFilter
                            },
                            label = { Text(dateFilter.displayName, fontSize = 12.sp) },
                            selected = selectedDateFilter == dateFilter
                        )
                    }
                }

                // Filtro por estado
                Text(
                    text = "Estado",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val statuses = listOf("pending", "confirmed", "cancelled", "attended")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    items(statuses) { status ->
                        FilterChip(
                            onClick = {
                                selectedStatusFilter = if (selectedStatusFilter == status) null else status
                            },
                            label = { Text(getStatusDisplayText(status), fontSize = 12.sp) },
                            selected = selectedStatusFilter == status
                        )
                    }
                }

                // Filtro por ubicación
                if (availableLocations.isNotEmpty()) {
                    Text(
                        text = "Ubicación",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.height(120.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(availableLocations.take(5)) { location ->
                            FilterChip(
                                onClick = {
                                    selectedLocationFilter = if (selectedLocationFilter == location) null else location
                                },
                                label = {
                                    Text(
                                        text = location.take(30) + if (location.length > 30) "..." else "",
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                },
                                selected = selectedLocationFilter == location,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Filtro por tipo de evento (categoría)
                if (availableCategories.isNotEmpty()) {
                    Text(
                        text = "Tipo de evento",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(availableCategories) { category ->
                            FilterChip(
                                onClick = {
                                    selectedCategoryFilter = if (selectedCategoryFilter == category) null else category
                                },
                                label = {
                                    Text(category, fontSize = 12.sp, maxLines = 1)
                                },
                                selected = selectedCategoryFilter == category
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row {
                TextButton(
                    onClick = {
                        selectedDateFilter = null
                        selectedStatusFilter = null
                        selectedLocationFilter = null
                        selectedCategoryFilter = null
                    }
                ) {
                    Text("Limpiar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        onApplyFilters(selectedDateFilter, selectedStatusFilter, selectedLocationFilter, selectedCategoryFilter)
                    }
                ) {
                    Text("Aplicar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Enums y funciones de utilidad
enum class DateFilter(val displayName: String) {
    TODAY("Hoy"),
    THIS_WEEK("Esta semana"),
    THIS_MONTH("Este mes"),
    LAST_MONTH("Mes pasado"),
    THIS_YEAR("Este año")
}

data class BookingStats(
    val total: Int,
    val confirmed: Int,
    val cancelled: Int,
    val attended: Int,
    val pending: Int
)

// Funciones de utilidad
fun calculateBookingStats(bookings: List<BookingSummary>): BookingStats {
    return BookingStats(
        total = bookings.size,
        confirmed = bookings.count { it.status == "confirmed" },
        cancelled = bookings.count { it.status == "cancelled" },
        attended = bookings.count { it.status == "attended" },
        pending = bookings.count { it.status == "pending" }
    )
}

fun getStatusDisplayText(status: String): String {
    return when (status) {
        "pending" -> "Pendiente"
        "confirmed" -> "Confirmada"
        "cancelled" -> "Cancelada"
        "attended" -> "Asistido"
        "expired" -> "Expirada"
        else -> "Desconocido"
    }
}

fun getStatusColor(status: String): Color {
    return when (status) {
        "pending" -> Color(0xFFFFF3CD) // Amarillo claro
        "confirmed" -> Color(0xFFD1ECF1) // Azul claro
        "cancelled" -> Color(0xFFF8D7DA) // Rojo claro
        "attended" -> Color(0xFFD4EDDA) // Verde claro
        "expired" -> Color(0xFFE2E3E5) // Gris claro
        else -> Color(0xFFE2E3E5)
    }
}

fun getStatusTextColor(status: String): Color {
    return when (status) {
        "pending" -> Color(0xFF856404)
        "confirmed" -> Color(0xFF0C5460)
        "cancelled" -> Color(0xFF721C24)
        "attended" -> Color(0xFF155724)
        "expired" -> Color(0xFF383D41)
        else -> Color(0xFF383D41)
    }
}

fun getStatusIcon(status: String): ImageVector {
    return when (status) {
        "pending" -> Icons.Default.Schedule
        "confirmed" -> Icons.Default.CheckCircle
        "cancelled" -> Icons.Default.Cancel
        "attended" -> Icons.Default.TaskAlt
        "expired" -> Icons.Default.AccessTime
        else -> Icons.Default.Info
    }
}

fun getAvailableLocations(bookings: List<BookingSummary>): List<String> {
    return bookings.map { it.experience.location }
        .distinct()
        .sorted()
}

fun getAvailableCategories(bookings: List<BookingSummary>): List<String> {
    return bookings.map { it.experience.category }
        .filter { it.isNotBlank() }
        .distinct()
        .sorted()
}

fun filterBookings(
    bookings: List<BookingSummary>,
    searchQuery: String,
    dateFilter: DateFilter?,
    statusFilter: String?,
    locationFilter: String?,
    categoryFilter: String?
): List<BookingSummary> {
    return bookings.filter { booking ->
        // Filtro por búsqueda
        val matchesSearch = if (searchQuery.isBlank()) {
            true
        } else {
            booking.experience.title.contains(searchQuery, ignoreCase = true)
        }

        // Filtro por fecha
        val matchesDate = if (dateFilter == null) {
            true
        } else {
            val bookingDate = parseDate(booking.createdAt)
            val now = Calendar.getInstance()

            when (dateFilter) {
                DateFilter.TODAY -> isSameDay(bookingDate, now)
                DateFilter.THIS_WEEK -> isThisWeek(bookingDate, now)
                DateFilter.THIS_MONTH -> isThisMonth(bookingDate, now)
                DateFilter.LAST_MONTH -> isLastMonth(bookingDate, now)
                DateFilter.THIS_YEAR -> isThisYear(bookingDate, now)
            }
        }

        // Filtro por estado
        val matchesStatus = statusFilter == null || booking.status == statusFilter

        // Filtro por ubicación
        val matchesLocation = locationFilter == null || booking.experience.location == locationFilter

        // Filtro por categoría
        val matchesCategory = categoryFilter == null || booking.experience.category == categoryFilter

        matchesSearch && matchesDate && matchesStatus && matchesLocation && matchesCategory
    }.sortedByDescending { it.createdAt }
}

// Funciones auxiliares para fechas
fun parseDate(dateString: String): Calendar {
    val calendar = Calendar.getInstance()
    try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        calendar.time = format.parse(dateString) ?: Date()
    } catch (e: Exception) {
        // Si hay error en el parsing, usar fecha actual
    }
    return calendar
}

fun isSameDay(date1: Calendar, date2: Calendar): Boolean {
    return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
            date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR)
}

fun isThisWeek(date: Calendar, now: Calendar): Boolean {
    return date.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
            date.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR)
}

fun isThisMonth(date: Calendar, now: Calendar): Boolean {
    return date.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
            date.get(Calendar.MONTH) == now.get(Calendar.MONTH)
}

fun isLastMonth(date: Calendar, now: Calendar): Boolean {
    val lastMonth = Calendar.getInstance()
    lastMonth.time = now.time
    lastMonth.add(Calendar.MONTH, -1)

    return date.get(Calendar.YEAR) == lastMonth.get(Calendar.YEAR) &&
            date.get(Calendar.MONTH) == lastMonth.get(Calendar.MONTH)
}

fun isThisYear(date: Calendar, now: Calendar): Boolean {
    return date.get(Calendar.YEAR) == now.get(Calendar.YEAR)
}