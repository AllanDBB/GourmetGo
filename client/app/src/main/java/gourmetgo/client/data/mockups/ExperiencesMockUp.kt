package gourmetgo.client.data.mockups
import gourmetgo.client.data.models.Experience


object ExperiencesMockup {

    private val experiences = emptyList(),

    fun getAllExperiences(): List<Experience> = experiences

    fun getExperienceById(id: String): Experience? =
        experiences.find { it.id == id }

    fun getExperiencesByStatus(status: String): List<Experience> =
        experiences.filter { it.status.equals(status, ignoreCase = true) }

    fun getExperiencesByCategory(category: String): List<Experience> =
        experiences.filter { it.category.equals(category, ignoreCase = true) }

    fun getExperiencesByChef(chefId: String): List<Experience> =
        experiences.filter { it.chef == chefId }

    fun getAvailableCategories(): List<String> =
        experiences.map { it.category }.distinct().sorted()

    fun getExperiencesByPriceRange(minPrice: Double, maxPrice: Double): List<Experience> =
        experiences.filter { it.price in minPrice..maxPrice }

    fun getExperiencesByDuration(minHours: Double, maxHours: Double): List<Experience> =
        experiences.filter { it.duration in minHours..maxHours }
}