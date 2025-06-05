package gourmetgo.client.utils

object DNIUtils {


    fun formatDNI(dni: String): String {
        if (dni.isBlank()) return ""

        val numbers = dni.replace(Regex("[^0-9]"), "")

        return when {
            numbers.length == 9 -> {
                "${numbers.substring(0, 1)}-${numbers.substring(1, 5)}-${numbers.substring(5)}"
            }
            else -> dni
        }
    }


    fun isValidDNI(dni: String): Boolean {
        if (dni.isBlank()) return false

        val numbers = dni.replace(Regex("[^0-9]"), "")

        return numbers.length == 9
    }
}