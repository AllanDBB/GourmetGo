package gourmetgo.client.data.models.dtos

data class BookingRequest(
    val experienceId: String,
    val people: Int,
    val name: String,
    val email: String,
    val phone: String,
    val termsAccepted: Boolean,
    val paymentMethod: String
)