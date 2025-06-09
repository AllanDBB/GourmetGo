package gourmetgo.client.utils

object BookingUtils {

    fun formatPhoneForDisplay(phone: String): String {
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
            numbers.length == 10 && numbers.startsWith("06") -> {
                val localNumber = numbers.substring(2)
                "+506 ${localNumber.substring(0, 4)} ${localNumber.substring(4)}"
            }
            numbers.length >= 4 -> {
                val cleanLocal = if (numbers.startsWith("506")) {
                    numbers.substring(3)
                } else {
                    numbers.take(8)
                }

                when {
                    cleanLocal.length <= 4 -> "+506 $cleanLocal"
                    else -> "+506 ${cleanLocal.substring(0, 4)} ${cleanLocal.substring(4)}"
                }
            }
            else -> numbers
        }
    }

    fun phoneToApiFormat(phone: String): String {
        val numbers = phone.replace(Regex("[^0-9]"), "")

        return when {
            numbers.length == 11 && numbers.startsWith("506") -> numbers.substring(3)
            numbers.length == 8 -> numbers
            else -> numbers
        }
    }

    fun isValidPhone(phone: String): Boolean {
        val numbers = phone.replace(Regex("[^0-9]"), "")

        return when {
            numbers.length == 11 && numbers.startsWith("506") -> true
            numbers.length == 8 -> true
            else -> false
        }
    }

    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidName(name: String): Boolean {
        if (name.isBlank()) return false
        return name.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) && name.trim().length >= 2
    }

    fun cleanPhoneInput(input: String): String {
        return input.replace(Regex("[^0-9]"), "")
    }
}