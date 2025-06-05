package gourmetgo.client.utils

enum class Preferences {
    ITALIAN,
    HINDU,
    ASIAN,
    AMERICAN,
    OTHERS;

    override fun toString(): String {
        return when (this) {
            ITALIAN -> "Italiano"
            HINDU -> "Hindu"
            ASIAN -> "Asiático"
            AMERICAN -> "Americano"
            OTHERS -> "Otros"
        }
    }
}
