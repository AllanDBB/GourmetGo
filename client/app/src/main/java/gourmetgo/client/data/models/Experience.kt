package gourmetgo.client.data.models

import com.google.gson.annotations.SerializedName

data class Experience(
    val _id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val location: String = "",
    val capacity: Int = 0,
    val remainingCapacity: Int = 0,
    val price: Double = 0.0,
    val duration: Double = 0.0,
    val category: String = "",
    val images: List<String> = emptyList(),
    val requirements: String = "",
    val status: String = "Activa",
    val chef: String = "", // ← Cambio: de String a ChefBasic
    val createdAt: String = "",
    val menu: Menu? = null
)

data class Menu(
    val image: String? = null,
    val text: String? = null
)
