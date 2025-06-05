package gourmetgo.client.data.mockups

import gourmetgo.client.data.models.Chef

object ChefMockup {
    
    private val testChefs = mutableListOf(
        Chef(
            name = "Restaurante La Cocina de María",
            contactPerson = "María González",
            email = "maria@lacocinademaria.com",
            phone = "88887777",
            location = "San José, Costa Rica",
            cuisineType = "Comida Tradicional Costarricense",
            password = "password123.",
            photoUrl = "https://images.unsplash.com/photo-1577219491135-ce391730fb2c?w=150",
            bio = "Chef especializada en comida tradicional costarricense con más de 15 años de experiencia. Apasionada por preservar las recetas ancestrales de nuestro país.",
            experience = "15 años",
            socialLinks = listOf(
                "https://instagram.com/lacocinademaria",
                "https://facebook.com/lacocinademaria"
            )
        ),
        Chef(
            name = "Chef Italiano Giuseppe",
            contactPerson = "Giuseppe Rossi",
            email = "giuseppe@italianoautentico.com",
            phone = "88886666",
            location = "Escazú, San José",
            cuisineType = "Cocina Italiana",
            password = "pasta123456.",
            photoUrl = "https://images.unsplash.com/photo-1559548331-f9cb98001426?w=150",
            bio = "Chef italiano con experiencia en las mejores cocinas de Roma y Milán. Especialista en pasta fresca y pizzas artesanales.",
            experience = "20 años",
            socialLinks = listOf(
                "https://instagram.com/chefgiuseppe",
                "https://facebook.com/italianoautentico"
            )
        ),
        Chef(
            name = "Sushi Master Tanaka",
            contactPerson = "Hiroshi Tanaka",
            email = "tanaka@sushimaster.com",
            phone = "88885555",
            location = "Santa Ana, San José",
            cuisineType = "Cocina Japonesa",
            password = "sushi789123.",
            photoUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150",
            bio = "Maestro sushiman con certificación de Tokio. Especialista en sushi tradicional y fusión japonesa-costarricense.",
            experience = "12 años",
            socialLinks = listOf(
                "https://instagram.com/sushimastertanaka"
            )
        ),
        Chef(
            name = "Panadería Artesanal El Horno",
            contactPerson = "Carlos Jiménez",
            email = "carlos@elhorno.com",
            phone = "88884444",
            location = "Cartago, Costa Rica",
            cuisineType = "Panadería y Repostería",
            password = "bread456789.",
            photoUrl = "https://images.unsplash.com/photo-1494790108755-2616b332c3db?w=150",
            bio = "Maestro panadero especializado en panes artesanales y repostería tradicional. Utilizamos ingredientes locales y técnicas ancestrales.",
            experience = "10 años",
            socialLinks = listOf(
                "https://instagram.com/panaderiaelhorno",
                "https://facebook.com/elhornoartesanal"
            )
        )
    )

    private val testCredentials = mutableMapOf(
        "maria@lacocinademaria.com" to "password123.",
        "giuseppe@italianoautentico.com" to "pasta123456.",
        "tanaka@sushimaster.com" to "sushi789123.",
        "carlos@elhorno.com" to "bread456789."
    )

    fun getAllChefs(): List<Chef> = testChefs

    private fun getChefByEmail(email: String): Chef? =
        testChefs.find { it.email.equals(email, ignoreCase = true) }

    fun validateCredentials(email: String, password: String): Chef? {
        val chef = getChefByEmail(email)
        return if (chef != null && chef.password == password) {
            chef
        } else null
    }

    fun emailExists(email: String): Boolean {
        return testChefs.any { it.email.equals(email, ignoreCase = true) }
    }

    fun addChef(chef: Chef): Chef {
        testChefs.add(chef)
        testCredentials[chef.email] = chef.password 
        return chef 
    }

    val availableCuisineTypes = listOf(
        "Cocina Italiana",
        "Cocina Japonesa",
        "Cocina Francesa",
        "Cocina Mexicana",
        "Cocina Vegetariana",
        "Mariscos y Pescados",
        "Carnes y Parrilla",
        "Cocina Fusión",
    )
}