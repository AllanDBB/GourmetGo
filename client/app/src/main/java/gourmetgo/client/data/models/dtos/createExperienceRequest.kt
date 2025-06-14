package gourmetgo.client.data.models.dtos

import android.net.Uri

data class CreateExperienceRequest(
  val title: String,
  val description: String,
  val date: String,
  val location: String, 
  val capacity: Int,
  val price: Double,
  val duration: Int,
  val category: String,
  val images: List<String>,
  val requirements: String,
  val status: String,
  val menu: Menu
)

data class Menu(
  val image: String,
  val text: String 
)