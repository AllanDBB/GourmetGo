package gourmetgo.client.utils

enum class Preferences {
    ITALIAN,
    ASIAN,
    AMERICAN,
    OTHERS;

    override fun toString(): String {
        return when (this) {
            ITALIAN -> "Italiano"
            ASIAN -> "Asiático"
            AMERICAN -> "Americano"
            OTHERS -> "Otros"
        }
    }
}
