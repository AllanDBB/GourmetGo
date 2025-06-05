package gourmetgo.client.data.mockups

import gourmetgo.client.data.models.User
import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.Chef

object UserMockup {

    private val testUsers = listOf(
        User(
            id = "user_001",
            name = "Juan Pérez",
            email = "juan@test.com",
            role = "user"
        ),
        User(
            id = "user_002", 
            name = "Ana Rodríguez",
            email = "ana@test.com",
            role = "user"
        ),
        User(
            id = "user_003",
            name = "Carlos López",
            email = "carlos@test.com", 
            role = "user"
        ),
        
        User(
            id = "chef_001",
            name = "Restaurante La Cocina de María",
            email = "maria@lacocinademaria.com",
            role = "chef"
        ),
        User(
            id = "chef_002",
            name = "Chef Italiano Giuseppe", 
            email = "giuseppe@italianoautentico.com",
            role = "chef"
        ),
        User(
            id = "chef_003",
            name = "Sushi Master Tanaka",
            email = "tanaka@sushimaster.com",
            role = "chef"
        ),
        User(
            id = "chef_004",
            name = "Panadería Artesanal El Horno",
            email = "carlos@elhorno.com",
            role = "chef"
        )
    )

    private val testCredentials = mapOf(
        // Usuarios normales
        "juan@test.com" to "abcdeF.1234",
        "ana@test.com" to "123456", 
        "carlos@test.com" to "123456",
        
        // Chefs
        "maria@lacocinademaria.com" to "password123.",
        "giuseppe@italianoautentico.com" to "pasta123456.",
        "tanaka@sushimaster.com" to "sushi789123.",
        "carlos@elhorno.com" to "bread456789."
    )

    fun validateCredentials(email: String, password: String): User? {
        val user = getUserByEmail(email)
        val storedPassword = testCredentials[email]
        
        return if (user != null && storedPassword == password) {
            user
        } else null
    }


    private fun getUserByEmail(email: String): User? {
        return testUsers.find { it.email.equals(email, ignoreCase = true) }
    }

    fun getAllUsers(): List<User> = testUsers


    fun getUsersByRole(role: String): List<User> {
        return testUsers.filter { it.role == role }
    }

    fun mapUserToClient(user: User): Client? {
        if (user.role != "user") return null
        
        return ClientMockup.getAllUsers().find { 
            it.email.equals(user.email, ignoreCase = true) 
        }
    }

    fun mapUserToChef(user: User): Chef? {
        if (user.role != "chef") return null
        
        return ChefMockup.getAllChefs().find {
            it.email.equals(user.email, ignoreCase = true)
        }
    }

    fun emailExists(email: String): Boolean {
        return testUsers.any { it.email.equals(email, ignoreCase = true) }
    }


    fun simulateUserRegistration(email: String, name: String): User {
        return User(
            id = "user_${System.currentTimeMillis()}",
            name = name,
            email = email,
            role = "user"
        )
    }
    
    fun simulateChefRegistration(email: String, name: String): User {
        return User(
            id = "chef_${System.currentTimeMillis()}",
            name = name, 
            email = email,
            role = "chef"
        )
    }
}