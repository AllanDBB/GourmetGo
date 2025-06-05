package gourmetgo.client.data.mockups

import gourmetgo.client.data.models.Client


object UserMockup {

    private val testClients = listOf(
        Client(
            id = "user_001",
            name = "Juz",
            email = "juan@test.com",
            phone = "88887777",
            role = "user",
            dni = "1234222",
            avatar = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150",
            preferences = listOf("Italiana", "Vegana", "Mediterránea"),
            createdAt = "2024-01-15T10:30:00Z"
        ),
        Client(
            id = "chef_001",
            name = "Chef María González",
            email = "maria@chef.com",
            phone = "99998888",
            role = "chef",
            dni = "1234222",
            avatar = "https://images.unsplash.com/photo-1559548331-f9cb98001426?w=150",
            preferences = listOf("Fusión", "Gourmet", "Internacional"),
            createdAt = "2024-01-10T08:15:00Z"
        ),
        Client(
            id = "chef_002",
            name = "Restaurante La Sabrosa",
            email = "info@lasabrosa.com",
            phone = "22445566",
            role = "chef",
            dni = "1234222",
            avatar = "https://images.unsplash.com/photo-1577219491135-ce391730fb2c?w=150",
            preferences = listOf("Tradicional", "Costarricense"),
            createdAt = "2024-01-05T14:20:00Z"
        ),
        Client(
            id = "user_002",
            name = "Ana Rodríguez",
            email = "ana@test.com",
            phone = "77776666",
            role = "user",
            dni = "1234222",
            avatar = "https://images.unsplash.com/photo-1494790108755-2616b332c3db?w=150",
            preferences = listOf("Asiática", "Saludable", "Vegana"),
            createdAt = "2024-01-20T16:45:00Z"
        )
    )

    private val testCredentials = mapOf(
        "juan@test.com" to "123456",
        "maria@chef.com" to "123456",
        "info@lasabrosa.com" to "123456",
        "ana@test.com" to "123456"
    )

    fun getAllUsers(): List<Client> = testClients

    private fun getUserByEmail(email: String): Client? =
        testClients.find { it.email.equals(email, ignoreCase = true) }

    fun getUserById(id: String): Client? =
        testClients.find { it.id == id }

    fun validateCredentials(email: String, password: String): Client? {
        return if (testCredentials[email] == password) {
            getUserByEmail(email)
        } else null
    }

    fun getUsersByRole(role: String): List<Client> =
        testClients.filter { it.role.equals(role, ignoreCase = true) }
}