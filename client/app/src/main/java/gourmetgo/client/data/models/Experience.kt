package gourmetgo.client.data.models


data class Experience(
    val menu: Menu,
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
    val chef: String = "",
    val createdAt: String = ""
)

data class Menu(
    val image: String = "",
    val text: String = "",
)