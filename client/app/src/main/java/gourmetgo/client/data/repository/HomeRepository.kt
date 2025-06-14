package gourmetgo.client.data.repository

import android.util.Log
import gourmetgo.client.AppConfig
import gourmetgo.client.data.localStorage.SharedPrefsManager
import gourmetgo.client.data.models.Experience
import gourmetgo.client.data.remote.ApiService
import kotlinx.coroutines.delay

class HomeRepository(
    private val apiService: ApiService,
    private val sharedPrefs: SharedPrefsManager
) {

    suspend fun getPopularExperiences(): Result<List<Experience>> {
        return try {
            if (AppConfig.USE_MOCKUP) {
                getPopularExperiencesMockup()
            } else {
                getPopularExperiencesFromApi()
            }
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error in getPopularExperiences", e)
            Result.failure(Exception("Error cargando experiencias populares: ${e.message}"))
        }
    }

    suspend fun getUpcomingExperiences(): Result<List<Experience>> {
        return try {
            if (AppConfig.USE_MOCKUP) {
                getUpcomingExperiencesMockup()
            } else {
                getUpcomingExperiencesFromApi()
            }
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error in getUpcomingExperiences", e)
            Result.failure(Exception("Error cargando experiencias próximas: ${e.message}"))
        }
    }

    suspend fun getCategories(): Result<List<String>> {
        return try {
            if (AppConfig.USE_MOCKUP) {
                getCategoriesMockup()
            } else {
                getCategoriesFromApi()
            }
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error in getCategories", e)
            Result.failure(Exception("Error cargando categorías: ${e.message}"))
        }
    }

    suspend fun searchExperiences(query: String): Result<List<Experience>> {
        return try {
            if (AppConfig.USE_MOCKUP) {
                searchExperiencesMockup(query)
            } else {
                searchExperiencesFromApi(query)
            }
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error in searchExperiences", e)
            Result.failure(Exception("Error buscando experiencias: ${e.message}"))
        }
    }

    suspend fun getExperiencesByCategory(category: String): Result<List<Experience>> {
        return try {
            if (AppConfig.USE_MOCKUP) {
                getExperiencesByCategoryMockup(category)
            } else {
                getExperiencesByCategoryFromApi(category)
            }
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error in getExperiencesByCategory", e)
            Result.failure(Exception("Error cargando experiencias por categoría: ${e.message}"))
        }
    }

    // Obtener TODAS las experiencias
    suspend fun getAllExperiences(): Result<List<Experience>> {
        return if (AppConfig.USE_MOCKUP) {
            getAllExperiencesMockup()
        } else {
            getAllExperiencesFromApi()
        }
    }

    // API Methods
    private suspend fun getPopularExperiencesFromApi(): Result<List<Experience>> {
        return try {
            Log.d("HomeRepository", "Fetching popular experiences from API")            // Llamar al endpoint de experiencias - las populares son las que tienen status "Activa"
            val experiences = apiService.getExperiences()
            
            // Filtrar solo las activas y limitamos a las primeras 10 para "populares"
            val popularExperiences = experiences
                .filter { it.status == "Activa" }
                .take(10)
            
            if (AppConfig.ENABLE_LOGGING) {
                Log.d("HomeRepository", "Fetched ${popularExperiences.size} popular experiences")
            }
            
            Result.success(popularExperiences)
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error fetching popular experiences from API", e)
            Result.failure(e)
        }
    }

    private suspend fun getUpcomingExperiencesFromApi(): Result<List<Experience>> {
        return try {            Log.d("HomeRepository", "Fetching upcoming experiences from API")
            
            // Llamar al endpoint de experiencias - las próximas son las que tienen status "Próximamente"
            val experiences = apiService.getExperiences()
            
            // Filtrar solo las próximas y limitamos a las primeras 10
            val upcomingExperiences = experiences
                .filter { it.status == "Próximamente" }
                .take(10)
            
            if (AppConfig.ENABLE_LOGGING) {
                Log.d("HomeRepository", "Fetched ${upcomingExperiences.size} upcoming experiences")
            }
            
            Result.success(upcomingExperiences)
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error fetching upcoming experiences from API", e)
            Result.failure(e)
        }
    }

    private suspend fun getCategoriesFromApi(): Result<List<String>> {
        return try {            Log.d("HomeRepository", "Fetching categories from API")
            
            // Obtener todas las experiencias y extraer categorías únicas
            val experiences = apiService.getExperiences()
            val categories = experiences
                .map { it.category }
                .distinct()
                .sorted()
            
            if (AppConfig.ENABLE_LOGGING) {
                Log.d("HomeRepository", "Fetched ${categories.size} categories")
            }
            
            Result.success(categories)
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error fetching categories from API", e)
            Result.failure(e)
        }
    }

    private suspend fun searchExperiencesFromApi(query: String): Result<List<Experience>> {
        return try {            Log.d("HomeRepository", "Searching experiences from API with query: $query")
            
            // Obtener todas las experiencias y filtrar del lado del cliente
            val allExperiences = apiService.getExperiences()
            
            // Filtrar experiencias por título, descripción o categoría
            val experiences = allExperiences.filter { experience ->
                experience.title.contains(query, ignoreCase = true) ||
                experience.description.contains(query, ignoreCase = true) ||
                experience.category.contains(query, ignoreCase = true)
            }
            
            if (AppConfig.ENABLE_LOGGING) {
                Log.d("HomeRepository", "Found ${experiences.size} experiences for query: $query")
            }
            
            Result.success(experiences)
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error searching experiences from API", e)
            Result.failure(e)
        }
    }    private suspend fun getExperiencesByCategoryFromApi(category: String): Result<List<Experience>> {
        return try {            Log.d("HomeRepository", "Fetching experiences by category from API: $category")
            
            // Obtener todas las experiencias y filtrar por categoría
            val experiences = apiService.getExperiences()
            
            // Log para debug: mostrar todas las categorías disponibles
            if (AppConfig.ENABLE_LOGGING) {
                val allCategories = experiences.map { it.category }.distinct()
                Log.d("HomeRepository", "Available categories in API: $allCategories")
            }
              // Filtro más flexible: búsqueda por coincidencia parcial
            val categoryExperiences = experiences.filter { experience ->
                when (category.lowercase()) {
                    "todos" -> true
                    "eventos" -> {
                        val expCategory = experience.category.lowercase()
                        expCategory.contains("evento") || 
                        expCategory == "eventos" ||
                        expCategory == "evento"
                    }
                    "clases" -> {
                        val expCategory = experience.category.lowercase()
                        expCategory.contains("clase") || 
                        expCategory.contains("taller") ||
                        expCategory == "clases" ||
                        expCategory == "clase"
                    }
                    "restaurantes" -> {
                        val expCategory = experience.category.lowercase()
                        expCategory.contains("restaurante") || 
                        expCategory == "restaurantes" ||
                        expCategory == "restaurante"
                    }
                    else -> experience.category.equals(category, ignoreCase = true)
                }
            }
            
            if (AppConfig.ENABLE_LOGGING) {
                Log.d("HomeRepository", "Found ${categoryExperiences.size} experiences for category: $category")
                Log.d("HomeRepository", "Matched experiences: ${categoryExperiences.map { "${it.title} (${it.category})" }}")
            }
            
            Result.success(categoryExperiences)
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error fetching experiences by category from API", e)
            Result.failure(e)
        }
    }

    private suspend fun getAllExperiencesFromApi(): Result<List<Experience>> {
        return try {            Log.d("HomeRepository", "Fetching ALL experiences from API")
            
            // Llamar al endpoint de experiencias - obtener todas sin filtros
            val experiences = apiService.getExperiences()
            
            if (AppConfig.ENABLE_LOGGING) {
                Log.d("HomeRepository", "Fetched ${experiences.size} total experiences")
            }
            
            Result.success(experiences)
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error fetching all experiences from API", e)
            Result.failure(e)
        }
    }

    // Mockup Methods
    private suspend fun getPopularExperiencesMockup(): Result<List<Experience>> {
        delay(800) // Simular delay de red
        
        val mockExperiences = listOf(
            Experience(
                _id = "pop1",
                title = "Cena Gourmet Italiana",
                description = "Una experiencia culinaria única con los mejores sabores de Italia",
                date = "2024-12-15T19:00:00Z",
                location = "https://maps.google.com/?q=San+José+Centro",
                capacity = 20,
                remainingCapacity = 8,
                price = 45000.0,
                duration = 3.0,
                category = "Eventos",
                images = listOf("https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400"),
                requirements = "Traer apetito y mente abierta",
                status = "Activa",
                chef = "chef1",
                createdAt = "2024-01-01T10:00:00Z"
            ),
            Experience(
                _id = "pop2",
                title = "Clase de Sushi Tradicional",
                description = "Aprende a hacer sushi auténtico con un chef especializado",
                date = "2024-12-18T16:00:00Z",
                location = "https://maps.google.com/?q=Escazú+Centro",
                capacity = 12,
                remainingCapacity = 5,
                price = 35000.0,
                duration = 2.5,
                category = "Clases",
                images = listOf("https://images.unsplash.com/photo-1579952363873-27d3bfad9c0d?w=400"),
                requirements = "Ninguno, materiales incluidos",
                status = "Activa",
                chef = "chef2",
                createdAt = "2024-01-02T10:00:00Z"
            ),            Experience(
                _id = "pop3",
                title = "Tour Gastronómico Costarricense",
                description = "Descubre los sabores tradicionales de Costa Rica",
                date = "2024-12-20T11:00:00Z",
                location = "https://maps.google.com/?q=Mercado+Central+San+José",
                capacity = 15,
                remainingCapacity = 10,
                price = 25000.0,
                duration = 4.0,
                category = "Eventos",
                images = listOf("https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400"),
                requirements = "Zapatos cómodos",
                status = "Activa",
                chef = "chef3",
                createdAt = "2024-01-03T10:00:00Z"
            ),
            Experience(
                _id = "pop4",
                title = "Taller de Panadería Artesanal",
                description = "Aprende técnicas tradicionales de panadería con masa madre",
                date = "2024-12-22T09:00:00Z",
                location = "https://maps.google.com/?q=Alajuela+Centro",
                capacity = 10,
                remainingCapacity = 3,
                price = 28000.0,
                duration = 5.0,
                category = "Clases",
                images = listOf("https://images.unsplash.com/photo-1509440159596-0249088772ff?w=400"),                requirements = "Ropa cómoda y delantal",
                status = "Activa",
                chef = "chef4",
                createdAt = "2024-01-04T10:00:00Z"
            )
        )
        return Result.success(mockExperiences)
    }

    private suspend fun getUpcomingExperiencesMockup(): Result<List<Experience>> {
        delay(600) // Simular delay de red
          val mockExperiences = listOf(
            Experience(
                _id = "up1",
                title = "Masterclass de Repostería Francesa",
                description = "Crea postres franceses clásicos con técnicas profesionales",
                date = "2025-01-10T14:00:00Z",
                location = "https://maps.google.com/?q=Heredia+Centro",
                capacity = 8,
                remainingCapacity = 8,
                price = 55000.0,
                duration = 4.0,
                category = "Clases",
                images = listOf("https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400"),
                requirements = "Nivel intermedio en cocina",
                status = "Próximamente",
                chef = "chef4",
                createdAt = "2024-01-04T10:00:00Z"
            ),
            Experience(
                _id = "up2",
                title = "Cena Temática Asiática",
                description = "Un viaje culinario por Asia con platos auténticos",
                date = "2025-01-15T18:30:00Z",
                location = "https://maps.google.com/?q=Cartago+Centro",
                capacity = 25,
                remainingCapacity = 25,
                price = 50000.0,
                duration = 3.5,
                category = "Eventos",
                images = listOf("https://images.unsplash.com/photo-1559847844-5315695dadae?w=400"),
                requirements = "Ninguno",
                status = "Próximamente",
                chef = "chef5",
                createdAt = "2024-01-05T10:00:00Z"
            ),
            Experience(
                _id = "up3",
                title = "Clase Magistral de Cocina Molecular",
                description = "Descubre las técnicas más avanzadas de la gastronomía moderna",
                date = "2025-01-20T16:00:00Z",
                location = "https://maps.google.com/?q=San+Pedro+Centro",
                capacity = 6,
                remainingCapacity = 6,
                price = 75000.0,
                duration = 6.0,
                category = "Eventos",
                images = listOf("https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400"),
                requirements = "Experiencia básica en cocina",
                status = "Próximamente",
                chef = "chef6",
                createdAt = "2024-01-06T10:00:00Z"
            )
        )
          return Result.success(mockExperiences)
    }

    private suspend fun getCategoriesMockup(): Result<List<String>> {
        delay(300)
        
        // Obtener categorías dinámicamente de los datos mockup
        val allExperiences = getPopularExperiencesMockup().getOrNull().orEmpty() + 
                           getUpcomingExperiencesMockup().getOrNull().orEmpty()
        
        val dynamicCategories = allExperiences
            .map { it.category }
            .distinct()
            .sorted()
        
        // Categorías principales que siempre mostramos, más las dinámicas
        val mainCategories = listOf("Todos", "Eventos", "Clases", "Restaurantes")
        val allCategories = (mainCategories + dynamicCategories)
            .distinct()
        
        if (AppConfig.ENABLE_LOGGING) {
            Log.d("HomeRepository", "Mockup categories: $allCategories")
        }
        
        return Result.success(allCategories)
    }

    private suspend fun searchExperiencesMockup(query: String): Result<List<Experience>> {
        delay(500)
        
        // Simular búsqueda en experiencias mock
        val allExperiences = getPopularExperiencesMockup().getOrNull().orEmpty() + 
                           getUpcomingExperiencesMockup().getOrNull().orEmpty()
        
        val filtered = allExperiences.filter { 
            it.title.contains(query, ignoreCase = true) || 
            it.description.contains(query, ignoreCase = true) ||
            it.category.contains(query, ignoreCase = true)
        }
          return Result.success(filtered)
    }

    private suspend fun getExperiencesByCategoryMockup(category: String): Result<List<Experience>> {
        delay(400)
        
        val allExperiences = getPopularExperiencesMockup().getOrNull().orEmpty() + 
                           getUpcomingExperiencesMockup().getOrNull().orEmpty()
          // Filtro más flexible para mockup también
        val filtered = allExperiences.filter { experience ->
            when (category.lowercase()) {
                "todos" -> true
                "eventos" -> {
                    val expCategory = experience.category.lowercase()
                    expCategory.contains("evento") || 
                    expCategory == "eventos" ||
                    expCategory == "evento"
                }
                "clases" -> {
                    val expCategory = experience.category.lowercase()
                    expCategory.contains("clase") || 
                    expCategory.contains("taller") ||
                    expCategory == "clases" ||
                    expCategory == "clase"
                }
                "restaurantes" -> {
                    val expCategory = experience.category.lowercase()
                    expCategory.contains("restaurante") || 
                    expCategory == "restaurantes" ||
                    expCategory == "restaurante"
                }
                else -> experience.category.equals(category, ignoreCase = true)
            }
        }
          if (AppConfig.ENABLE_LOGGING) {
            Log.d("HomeRepository", "🔍 MOCKUP FILTER: Searching for category: '$category'")
            Log.d("HomeRepository", "🔍 Total experiences to filter: ${allExperiences.size}")
            allExperiences.forEach { exp ->
                Log.d("HomeRepository", "  📋 Available: '${exp.title}' (Category: '${exp.category}')")
            }
            Log.d("HomeRepository", "🎯 MOCKUP RESULTS: Found ${filtered.size} experiences for category: $category")
            filtered.forEach { exp ->
                Log.d("HomeRepository", "  ✅ Matched: '${exp.title}' (Category: '${exp.category}')")
            }
        }
        
        return Result.success(filtered)
    }
    
    private suspend fun getAllExperiencesMockup(): Result<List<Experience>> {
        delay(500)
        
        // Combinar todas las experiencias: populares + upcoming
        val popularExperiences = getPopularExperiencesMockup().getOrNull().orEmpty()
        val upcomingExperiences = getUpcomingExperiencesMockup().getOrNull().orEmpty()
        
        val allExperiences = popularExperiences + upcomingExperiences
        
        return Result.success(allExperiences)
    }
}
