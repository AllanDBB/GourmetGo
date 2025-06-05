package gourmetgo.client.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gourmetgo.client.data.mockups.ChefMockup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuisineTypeSection(
    selectedCuisineType: String,
    onCuisineTypeChange: (String) -> Unit,
    error: String? = null
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column {
        // Título de la sección
        Text(
            text = "Tipo de Cocina",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Campo dropdown
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedCuisineType,
                onValueChange = { }, // No editable directamente
                readOnly = true,
                label = { Text("Selecciona tu especialidad") },
                leadingIcon = { 
                    Icon(
                        Icons.Default.Restaurant, 
                        contentDescription = null
                    ) 
                },
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Desplegar opciones",
                        modifier = Modifier.clickable { isExpanded = !isExpanded }
                    )
                },
                isError = error != null,
                supportingText = error?.let { { Text(it) } },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                    focusedBorderColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor() // Importante para el ExposedDropdownMenuBox
            )

            // Menu desplegable
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                ChefMockup.availableCuisineTypes.forEach { cuisineType ->
                    DropdownMenuItem(
                        text = { 
                            Text(
                                text = cuisineType,
                                fontSize = 14.sp
                            ) 
                        },
                        onClick = {
                            onCuisineTypeChange(cuisineType)
                            isExpanded = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Mostrar selección actual si hay una
        if (selectedCuisineType.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Icon(
                        Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedCuisineType,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}