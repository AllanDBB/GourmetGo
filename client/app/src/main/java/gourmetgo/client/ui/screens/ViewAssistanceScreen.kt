package gourmetgo.client.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gourmetgo.client.data.models.Experience
import gourmetgo.client.viewmodel.ViewAssistanceViewModel
import gourmetgo.client.ui.components.BookingInfoCard
import gourmetgo.client.data.models.dtos.AssistanceResponse
import gourmetgo.client.viewmodel.statesUi.ViewAssistanceUiState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import gourmetgo.client.utils.ViewAssistanceExportUtils
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun ViewAssistanceScreen(
    viewModel: ViewAssistanceViewModel,
    onDownloadPdf: () -> Unit,
    onDownloadCsv: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel::uiState
    val experience = uiState.experience
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack, 
                    contentDescription = "Regresar"
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { viewModel.refreshAssistance() }) {
                Text("Actualizar")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Asistencia del evento",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        experience?.let { exp ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = exp.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                val formattedDate = try {
                    val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
                    inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
                    val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                    val date = inputFormat.parse(exp.date)
                    if (date != null) outputFormat.format(date) else exp.date
                } catch (e: Exception) {
                    exp.date
                }
                Text(
                    text = "Fecha: $formattedDate",
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(2.dp))
                val bookingsList = uiState.bookings ?: emptyList()
                val totalPeople = bookingsList.sumOf { it.people ?: 0 }
                val totalCapacity = exp.capacity ?: "-"
                Text(
                    text = "Confirmados: $totalPeople / $totalCapacity",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Text(
                text = uiState.error ?: "Error desconocido",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        } else {
            val bookingsList = uiState.bookings ?: emptyList()
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(bookingsList) { booking ->
                        BookingInfoCard(booking = booking, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                val bookingsList = uiState.bookings ?: emptyList()
                val file = ViewAssistanceExportUtils.exportBookingsToPdf(context, bookingsList)
                if (file != null) {
                    Toast.makeText(context, "PDF guardado en Descargas", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Error al generar PDF", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.weight(1f)) {
                Text("Guardar PDF")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                val bookingsList = uiState.bookings ?: emptyList()
                val file = ViewAssistanceExportUtils.exportBookingsToCsv(context, bookingsList)
                if (file != null) {
                    Toast.makeText(context, "CSV guardado en Descargas", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Error al generar CSV", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.weight(1f)) {
                Text("Guardar CSV")
            }
        }
    }
}

