package gourmetgo.client.utils

object PhoneUtils {


    fun formatPhone(phone: String): String {
        if (phone.isBlank()) return ""

        val numbers = phone.replace(Regex("[^0-9]"), "")

        return when {
            numbers.length == 11 && numbers.startsWith("506") -> {
                val localNumber = numbers.substring(3)
                "+506 ${localNumber.substring(0, 4)} ${localNumber.substring(4)}"
            }
            numbers.length == 8 -> {
                "+506 ${numbers.substring(0, 4)} ${numbers.substring(4)}"
            }
            else -> phone
        }
    }

    fun isValidPhone(phone: String): Boolean {
        if (phone.isBlank()) return false

        val numbers = phone.replace(Regex("[^0-9]"), "")

        return when {
            numbers.length == 11 && numbers.startsWith("506") -> true
            numbers.length == 8 -> true
            else -> false
        }
    }
}