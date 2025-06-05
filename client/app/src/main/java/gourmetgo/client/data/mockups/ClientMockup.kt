package gourmetgo.client.data.mockups

import gourmetgo.client.data.models.Client

object ClientMockup {

    private val testClients = mutableListOf(
        Client(
            name = "Juan Pérez",
            email = "juan@test.com",
            phone = "88887777",
            identification = "123456789",
            password = "abcdeF.1234",
            photoUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150",
            preferences = listOf("Italiana", "Vegana")
        ),
        Client(
            name = "Chef María González",
            email = "maria@chef.com",
            phone = "99998888",
            identification = "123456789",
            password = "aaaaaa1234.",
            photoUrl = "https://images.unsplash.com/photo-1559548331-f9cb98001426?w=150",
            preferences = listOf("Fusión", "Gourmet", "Internacional")
        ),
        Client(
            name = "Restaurante La Sabrosa",
            email = "info@lasabrosa.com",
            phone = "22445566",
            identification = "123456789",
            password = "123456",
            photoUrl = "https://images.unsplash.com/photo-1577219491135-ce391730fb2c?w=150",
            preferences = listOf("Tradicional", "Costarricense")
        ),
        Client(
            name = "Ana Rodríguez",
            email = "ana@test.com",
            phone = "77776666",
            identification = "123456789",
            password = "123456",
            photoUrl = "https://images.unsplash.com/photo-1494790108755-2616b332c3db?w=150",
            preferences = listOf("Asiática", "Saludable", "Vegana")
        )
    )

    private val testCredentials = mutableMapOf(
        "juan@test.com" to "abcdeF.1234",
        "maria@chef.com" to "aaaaaa1234.",
        "info@lasabrosa.com" to "123456",
        "ana@test.com" to "123456"
    )

    // ========== FUNCIONES DE LOGIN ==========

    fun getAllUsers(): List<Client> = testClients


    private fun getUserByEmail(email: String): Client? =
        testClients.find { it.email.equals(email, ignoreCase = true) }


    fun validateCredentials(email: String, password: String): Client? {
    val user = getUserByEmail(email)
    return if (user != null && user.password == password) {
        user
    } else null
}


    fun emailExists(email: String): Boolean {
        return testClients.any { it.email.equals(email, ignoreCase = true) }
    }

    fun addUser(client: Client): Client {
        testClients.add(client)
        testCredentials[client.email] = client.password
        return client
    }
}