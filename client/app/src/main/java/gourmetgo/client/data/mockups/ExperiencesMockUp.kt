package gourmetgo.client.data.mockups
import gourmetgo.client.data.models.Experience
import gourmetgo.client.data.models.Experience.Menu


object ExperiencesMockup {

    private val experiences = listOf(
        Experience(
            menu = Experience.Menu(
                image = "https://images.unsplash.com/photo-1579952363873-27d3bfad9c0d?w=600",
                text = "Taller de Sushi: Aprende a Hacer Sushi Tradicional"
            ),
            _id = "exp_001",
            title = "Taller de Sushi para Principiantes",
            description = "Aprende a preparar sushi tradicional japonés con ingredientes frescos y técnicas auténticas. Incluye degustación y certificado de participación. Una experiencia perfecta para iniciarse en la cocina japonesa.",
            date = "2025-06-15T19:00:00Z",
            location = "San José Centro, Costa Rica",
            capacity = 15,
            remainingCapacity = 8,
            price = 25000.0,
            duration = 3.0,
            category = "Taller",
            images = listOf(
                "https://images.unsplash.com/photo-1579952363873-27d3bfad9c0d?w=600",
                "https://images.unsplash.com/photo-1611143669185-af224c5e3252?w=600"
            ),
            requirements = "Traer delantal y muchas ganas de aprender. No se requiere experiencia previa.",
            status = "Activa",
            chef = "chef_001",
            createdAt = "2025-01-15T10:00:00Z"
        ),
        Experience(
            menu = Experience.Menu(
                image = "https://images.unsplash.com/photo-1551782450-17144efb9c50?w=600",
                text = "Cena Italiana Gourmet: Platos Toscani"
            ),
            _id = "exp_002",
            title = "Cena Italiana Gourmet",
            description = "Una experiencia culinaria italiana auténtica con platos tradicionales de la Toscana. Menú de 5 tiempos con mar_idaje de vinos selectos. Ambiente romántico y elegante.",
            date = "2025-06-20T20:00:00Z",
            location = "Escazú, San José",
            capacity = 20,
            remainingCapacity = 5,
            price = 35000.0,
            duration = 2.5,
            category = "Cena",
            images = listOf(
                "https://images.unsplash.com/photo-1551782450-17144efb9c50?w=600",
                "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=600"
            ),
            requirements = "Código de vestimenta: Casual elegante. Reservas con 48h de anticipación.",
            status = "Activa",
            chef = "chef_002",
            createdAt = "2025-01-18T14:30:00Z"
        ),
        Experience(
            menu = Experience.Menu(
                image = "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=600",
                text = "Tapas Españolas: Variedad de Sabores"
            ),
            _id = "exp_003",
            title = "Cocina Mediterránea: Tapas Españolas",
            description = "Descubre los sabores auténticos de España preparando una variedad de tapas tradicionales con ingredientes importados directamente de la península ibérica.",
            date = "2025-06-25T18:30:00Z",
            location = "Heredia Centro",
            capacity = 12,
            remainingCapacity = 12,
            price = 28000.0,
            duration = 3.5,
            category = "Taller",
            images = listOf(
                "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=600",
                "https://images.unsplash.com/photo-1565299507177-b0ac66763828?w=600"
            ),
            requirements = "Nivel principiante a intermedio. Incluye recetas para llevar a casa.",
            status = "Próximamente",
            chef = "chef_001",
            createdAt = "2025-01-20T09:15:00Z"
        ),
        Experience(
            menu = Experience.Menu(
                image = "https://images.unsplash.com/photo-1511690743698-d9d85f2fbf38?w=600",
                text = "Brunch Vegano Gourmet: Opciones Creativas"
            ),
            _id = "exp_004",
            title = "Brunch Vegano Gourmet",
            description = "Un brunch completamente vegano con opciones creativas y nutritivas. Incluye bat_idos verdes, pancakes de avena, tostadas de aguacate y postres sin azúcar refinada.",
            date = "2025-06-10T10:00:00Z",
            location = "Santa Ana, San José",
            capacity = 16,
            remainingCapacity = 0,
            price = 22000.0,
            duration = 2.0,
            category = "Brunch",
            images = listOf(
                "https://images.unsplash.com/photo-1511690743698-d9d85f2fbf38?w=600",
                "https://images.unsplash.com/photo-1567306226416-28f0efdc88ce?w=600"
            ),
            requirements = "Apto para vegetarianos y veganos. Ingredientes 100% orgánicos.",
            status = "Agotada",
            chef = "chef_002",
            createdAt = "2025-01-12T16:45:00Z"
        ),
        Experience(
            menu = Experience.Menu(
                image = "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=600",
                text = "Repostería Francesa: Macarons y Éclairs"
            ),
            _id = "exp_005",
            title = "Repostería Francesa: Macarons y Éclairs",
            description = "Aprende las técnicas clásicas de la repostería francesa para crear macarons perfectos y éclairs deliciosos. Incluye técnicas de merengue y cremas pasteleras.",
            date = "2025-07-05T15:00:00Z",
            location = "Cartago Centro",
            capacity = 10,
            remainingCapacity = 7,
            price = 32000.0,
            duration = 4.0,
            category = "Repostería",
            images = listOf(
                "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=600",
                "https://images.unsplash.com/photo-1587668178277-295251f900ce?w=600"
            ),
            requirements = "Experiencia básica en repostería recomendada. Incluye kit de herramientas.",
            status = "Activa",
            chef = "chef_001",
            createdAt = "2025-01-25T11:20:00Z"
        ),
        Experience(
            menu = Experience.Menu(
                image = "https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?w=600",
                text = "Asado Argentino Tradicional: Parrilla y Vinos"
            ),
            _id = "exp_006",
            title = "Asado Argentino Tradicional",
            description = "Una experiencia completa de asado argentino con cortes premium, chimichurri casero y vinos malbec. Aprende sobre el punto perfecto de la carne.",
            date = "2025-06-30T17:00:00Z",
            location = "Alajuela Centro",
            capacity = 25,
            remainingCapacity = 18,
            price = 45000.0,
            duration = 4.0,
            category = "Asado",
            images = listOf(
                "https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?w=600",
                "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=600"
            ),
            requirements = "Apto para todos los niveles. Incluye mar_idaje con vinos argentinos.",
            status = "Activa",
            chef = "chef_002",
            createdAt = "2025-01-28T13:10:00Z"
        ),
        Experience(
            menu = Experience.Menu(
                image = "https://images.unsplash.com/photo-1617093727343-374698b1b08d?w=600",
                text = "Cocina Asiática Fusión: Pad Thai y Sushi Rolls"
            ),
            _id = "exp_007",
            title = "Cocina Asiática Fusión",
            description = "Explora la fusión de sabores asiáticos con técnicas modernas. Prepara pad thai, sushi rolls creativos y postres asiáticos innovadores.",
            date = "2025-07-10T18:00:00Z",
            location = "San Pedro, San José",
            capacity = 14,
            remainingCapacity = 11,
            price = 30000.0,
            duration = 3.0,
            category = "Fusión",
            images = listOf(
                "https://images.unsplash.com/photo-1617093727343-374698b1b08d?w=600"
            ),
            requirements = "Nivel intermedio. Se proporcionan todos los ingredientes especiales.",
            status = "Activa",
            chef = "chef_001",
            createdAt = "2025-02-01T09:30:00Z"
        ),
        Experience(
            menu = Experience.Menu(
                image = "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=600",
                text = "Panadería Artesanal: Pan de Masa Madre"
            ),
            _id = "exp_008",
            title = "Panadería Artesanal: Pan de Masa Madre",
            description = "Aprende el arte milenario de hacer pan con masa madre. Desde crear tu propia madre hasta hornear diferentes tipos de pan artesanal.",
            date = "2025-07-15T08:00:00Z",
            location = "Moravia, San José",
            capacity = 8,
            remainingCapacity = 3,
            price = 26000.0,
            duration = 5.0,
            category = "Panadería",
            images = listOf(
                "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=600",
                "https://images.unsplash.com/photo-1549931319-a545dcf3bc73?w=600"
            ),
            requirements = "Madrugadores bienven_idos. Llevas tu pan y starter a casa.",
            status = "Activa",
            chef = "chef_002",
            createdAt = "2025-02-03T12:15:00Z"
        )
    )

    fun getAllExperiences(): List<Experience> = experiences

    fun getExperienceById(_id: String): Experience? =
        experiences.find { it._id == _id }

    fun getExperiencesByStatus(status: String): List<Experience> =
        experiences.filter { it.status.equals(status, ignoreCase = true) }

    fun getExperiencesByCategory(category: String): List<Experience> =
        experiences.filter { it.category.equals(category, ignoreCase = true) }

    fun getExperiencesByChef(chef__id: String): List<Experience> =
        experiences.filter { it.chef == chef__id }

    fun getAvailableCategories(): List<String> =
        experiences.map { it.category }.distinct().sorted()

    fun getExperiencesByPriceRange(minPrice: Double, maxPrice: Double): List<Experience> =
        experiences.filter { it.price in minPrice..maxPrice }

    fun getExperiencesByDuration(minHours: Double, maxHours: Double): List<Experience> =
        experiences.filter { it.duration in minHours..maxHours }
}