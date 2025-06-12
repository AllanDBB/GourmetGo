package gourmetgo.client.data.models.dtos

data class AssistanceResponse(
    val _id: String?,
    val user: AssistanceUser?,
    val experience: AssistanceExperience?,
    val name: String?,
    val email: String?,
    val phone: String?,
    val people: Int?,
    val termsAccepted: Boolean?,
    val paymentMethod: String?,
    val status: String?,
    val bookingCode: String?,
    val qrCodes: List<String>?,
    val createdAt: String?,
    
    val __v: Int? = null
)

data class AssistanceUser(
    val _id: String?,
    val name: String?,
    val email: String?,
    val avatar: String?
)

data class AssistanceExperience(
    val _id: String?,
    val title: String?,
    val date: String?,
    val remainingCapacity: Int?
)