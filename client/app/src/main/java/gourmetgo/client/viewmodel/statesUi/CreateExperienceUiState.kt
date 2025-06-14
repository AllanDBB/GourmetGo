package gourmetgo.client.viewmodel.statesUi

import gourmetgo.client.data.models.dtos.Menu
import android.net.Uri

data class CreateExperienceUiState(
    val isLoading: Boolean = false,
    val createSuccess: Boolean = false,
    val error: String? = null,
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val location: String = "",
    val capacity: Int = 0,
    val price: Double = 0.0,
    val duration: Int = 0,
    val category: String = "",
    val images: List<Uri> = emptyList(), // Uri para selección
    val requirements: String = "",
    val status: String = "",
    // del menu
    val image: Uri? = null, // Uri para selección
    val text: String = ""
)