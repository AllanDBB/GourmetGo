package gourmetgo.client.data.models

data class Booking(
    val _id: String = "",
    val user: String = "",
    val experience: Experience? = null,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val people: Int = 0,
    val termsAccepted: Boolean = false,
    val paymentMethod: String = "",
    val status: String = "pending",
    val bookingCode: String = "",
    val qrCodes: List<String> = emptyList(),
    val createdAt: String = ""
)