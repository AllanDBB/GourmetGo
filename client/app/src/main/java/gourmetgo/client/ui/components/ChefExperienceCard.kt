package gourmetgo.client.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gourmetgo.client.data.models.Experience

@Composable
fun ChefExperienceCard(
    experience: Experience,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = experience.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = experience.description,
                fontSize = 14.sp,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "₡${String.format("%,.0f", experience.price)}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${experience.duration}h",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Entradas reservadas: ${experience.capacity - experience.remainingCapacity}",
                    fontSize = 13.sp
                )
                Text(
                    text = "Capacidad restante: ${experience.remainingCapacity}",
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Estado: ${experience.status}",
                fontSize = 13.sp,
                color = when (experience.status) {
                    "Activa" -> MaterialTheme.colorScheme.primary
                    "Agotada" -> MaterialTheme.colorScheme.error
                    "Próximamente" -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDetailsClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ver más detalles")
                }
                OutlinedButton(
                    onClick = { /* TODO: Acción de ver asistencia */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ver asistencia")
                }
            }
        }
    }
}
