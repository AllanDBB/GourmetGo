package gourmetgo.client.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GastronomicPreferencesSection(
    selectedPreferences: Set<String>,
    onPreferencesChange: (Set<String>) -> Unit,
    error: String? = null,
    modifier: Modifier = Modifier
) {
    val preferences = listOf("Italiano", "Hindú", "Asiático", "Americano", "Otros")
    
    Column(modifier = modifier) {
        Text(
            text = "Preferencias gastronómicas (opcional)",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(preferences) { preference ->
                PreferenceChip(
                    text = preference,
                    isSelected = selectedPreferences.contains(preference),
                    onClick = {
                        val newPreferences = if (selectedPreferences.contains(preference)) {
                            selectedPreferences - preference
                        } else {
                            selectedPreferences + preference
                        }
                        onPreferencesChange(newPreferences)
                    }
                )
            }
        }
        
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun PreferenceChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = { Text(text) },
        selected = isSelected,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}